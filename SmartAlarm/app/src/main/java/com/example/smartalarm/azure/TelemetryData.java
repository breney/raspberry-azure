package com.example.smartalarm.azure;

import com.google.gson.Gson;

public class TelemetryData {
    public double distance;
    public boolean alarm;
    public String date;
    public int telemetryInterval;

    public boolean isAlarm() {
        return alarm;
    }

    public String getDate() {
        return date;
    }

    public String shouldAlert() {
        return distance > telemetryInterval ? "False" : "Verdadeiro";
    }

    public String getAlarm() {
        return alarm ? "Ativado" : "Desativado";
    }

    public String getDistance() {
        return distance + "cm";
    }

    public int getTelemetryInterval() {
        return telemetryInterval;
    }

    public static TelemetryData deserialize(String data) {
        return new Gson().fromJson(data, TelemetryData.class);
    }
}
