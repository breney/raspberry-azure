package org.example;

import com.google.gson.Gson;

public class TelemetryData {
    private boolean alarm;
    private String distance;

    public String getAlarm() {
        return alarm ? "Ativado" : "Desativado";
    }

    public String getDistance() {
        return distance + "cm";
    }

    public static TelemetryData deserialize(String data) {
        return new Gson().fromJson(data, TelemetryData.class);
    }
}
