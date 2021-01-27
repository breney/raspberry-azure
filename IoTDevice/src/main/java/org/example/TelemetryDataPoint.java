package org.example;

import com.google.gson.Gson;

public class TelemetryDataPoint {
    public double distance;
    public boolean alarm;

    public TelemetryDataPoint(double distance, boolean alarm) {
        this.distance = distance;
        this.alarm = alarm;
    }

    // Serialize object to JSON format.
    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
