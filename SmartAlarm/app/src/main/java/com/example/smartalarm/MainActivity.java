package com.example.smartalarm;

import android.os.Bundle;
import android.os.StrictMode;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartalarm.azure.AlarmController;
import com.example.smartalarm.azure.TelemetryLogger;

public class MainActivity extends AppCompatActivity {

    private TelemetryLogger telemetryLogger;
    private AlarmController alarmController;
    public EditText text;
    public TextView textArea;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.telemetryLogger = new TelemetryLogger();
        this.alarmController = new AlarmController();
        this.text = null;

        telemetryLogger.setCallback(handleHubData());


        setContentView(R.layout.activity_main);

        textArea = findViewById(R.id.textView3);


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        Thread a = new Thread() {
            @Override
            public void run() {
                telemetryLogger.connect();

            }
        };
        a.start();

        try {
            a.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Switch switch1 = findViewById(R.id.switch1);
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                new Thread() {
                    @Override
                    public void run() {
                        if (isChecked) {
                            // Ligar o Alarme
                            System.out.println("Ligar alarme");
                            alarmController.setAlarm(true);
                            return;
                        }

                        // Desligar Alarme
                        alarmController.setAlarm(false);
                        System.out.println("Desligar alarme");
                    }
                }.start();
            }
        });

    }

    private TelemetryLogger.Callback handleHubData() {
        return data -> {
            if (data.distance < data.getTelemetryInterval() && data.isAlarm() && data.distance > 0) {
                textArea.append(String.format("Alarme disparado em %s\n", data.getDate()));
                //text.append(buffer.toString());

                //  text.append(String.format("Alarme disparado em %s\n", data.getDate()));
            }

            // System.out.println(telemetryData.getDistance());
            // editText.append("ola");
        };
//        return (TelemetryData data) -> {
//            System.out.println(data.getDistance());
//            texts.append("ola");
//
//           // text.append(String.format("Alarme disparado em %s\n", data.getDate()));
//
//
//
//
//        };

    }

}