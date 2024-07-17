package com.example.plant_care;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class aiactivity extends AppCompatActivity {
    private static final String TAG = "AiDetectActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String API_KEY = ""; // Replace with your API key
    ImageView i;
    private String currentPhotoPath;
    private TextView tvshow;
    String str;
    EditText tvquk;
    Button btnTakePicture,back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aiactivity);
        i = findViewById(R.id.camimg);
       btnTakePicture = findViewById(R.id.btnTakePicture);
       back = findViewById(R.id.color);
        tvshow = findViewById(R.id.tvshow);
        tvquk = findViewById(R.id.quk);
        str = tvquk.getText().toString();
        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvquk.getText().toString().isEmpty()) {
                    Toast.makeText(aiactivity.this, "Enter Question", Toast.LENGTH_SHORT).show();
                } else {
                    dispatchTakePictureIntent();
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(aiactivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "Unable to open camera", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            i.setImageBitmap(imageBitmap);
            askAIQuestions(imageBitmap);
        }
    }


    private void askAIQuestions(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        GenerativeModel generativeModel = new GenerativeModel("gemini-1.5-flash",API_KEY );
        GenerativeModelFutures modelFutures = GenerativeModelFutures.from(generativeModel);
        Content.Builder contentRequestBuilder = new Content.Builder()
                .addText(str)
                .addImage(bitmap); // Add the image directly

        Content contentRequest = contentRequestBuilder.build();
        ListenableFuture<GenerateContentResponse> response = modelFutures.generateContent(contentRequest);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(@NonNull GenerateContentResponse result) {
                runOnUiThread(() -> {
                    String resultText = result.getText();
                    tvshow.setText(resultText);
                });
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                Log.e(TAG, "Failed to generate AI content", t);
            }
        }, getMainExecutor());
    }
}
