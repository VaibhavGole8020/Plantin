 package com.example.plant_care;

import com.example.plant_care.databinding.ActivitySenoReadBinding;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.os.CountDownTimer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SenoRead extends AppCompatActivity {
    private static final String TAG = "SenoReadActivity";
    private ActivitySenoReadBinding binding;
    private DatabaseReference reference;
    private LineChart lineChart;
    Button PumpOn,back;
    EditText Duration;
    boolean relay = false;
    long count;
    String str;
    private CountDownTimer countDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize View Binding
        binding = ActivitySenoReadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase reference
        reference = FirebaseDatabase.getInstance().getReference("iotmost");
        Duration = findViewById(R.id.Duration);
        // Read data from Firebase
        readData();
        lineChart = findViewById(R.id.lineChart);
        setupLineChart();
        PumpOn = findViewById(R.id.PumpOn);
        back = findViewById(R.id.Back);
        str = Duration.getText().toString();
        count = Long.parseLong(str);

        PumpOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCountdown(count);
//                setData2(true);
                setData(!relay);
                Toast.makeText(SenoRead.this, "Pump On", Toast.LENGTH_SHORT).show();
            }
            });
        PumpOn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
//                startCountdown(count * 1000);
                setData(relay);
                Toast.makeText(SenoRead.this, "Pump Off", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SenoRead.this, MainActivity.class);
                startActivity(intent);
            }});

    }
    private void setupLineChart() {
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 4));
        entries.add(new Entry(1, 8));
        entries.add(new Entry(2, 6));
        entries.add(new Entry(3, 2));
        entries.add(new Entry(4, 7));

        LineDataSet dataSet = new LineDataSet(entries, "Label");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.RED);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate(); // refresh

    }

    private void setData(boolean isDataAvailable) {
        DatabaseReference ledRef = reference.child("Firebase").child("Led");
        ledRef.setValue(isDataAvailable);

//    private void setData2(boolean relay) {
//        DatabaseReference relyRef = reference.child("Firebase").child("Led");
//        relyRef.setValue(relay);
  }
    private void startCountdown(long durationMillis) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(durationMillis, 1) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsRemaining = millisUntilFinished / 1000;
                Duration.setText("Seconds remaining: " + secondsRemaining);
            }

            @Override
            public void onFinish() {
                Duration.setText("Timer finished!");
            }
        };

        countDownTimer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void readData() {
        // Add value event listener to read data
        reference.child("Firebase").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Fetch and display soil moisture
                    Long soilMoisture = dataSnapshot.child("moisture").getValue(Long.class);
                    if (soilMoisture != null) {
                        binding.soilMoisture.setText(+soilMoisture+"point");
                        Log.d(TAG, "Soil Moisture: " + soilMoisture);
                    } else {
                        binding.soilMoisture.setText("N/A");
                        Log.d(TAG, "Soil Moisture data not found");
                    }
//                    if (soilMoisture <= ) {
//                        setData(true);
//                    } else {
//                        setData(false);
//                    }

                    // Fetch and display temperature
                    Long temp = dataSnapshot.child("temp").getValue(Long.class);
                    if (temp != null) {
                        binding.temp.setText(+temp+"°C");
                        Log.d(TAG, "Temperature: " + temp);
                    } else {
                        binding.temp.setText("N/A");
                        Log.d(TAG, "Temperature data not found");
                    }

                    // Fetch and display humidity
                    Long humdty = dataSnapshot.child("humty").getValue(Long.class);
                    if (humdty != null) {
                        binding.humdty.setText(+humdty+"%");
                        Log.d(TAG, "Humidity: " + humdty);
                    } else {
                        binding.humdty.setText("N/A");
                        Log.d(TAG, "Humidity data not found");
                    }
                } else {
                    Log.d(TAG, "No data found at the specified path.");
                    Toast.makeText(SenoRead.this, "No data found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
                Toast.makeText(SenoRead.this, "Failed to read data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
