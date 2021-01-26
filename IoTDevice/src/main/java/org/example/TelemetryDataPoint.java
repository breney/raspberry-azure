package org.example;

import com.google.gson.Gson;

public class TelemetryDataPoint {
    public double distance;

    public TelemetryDataPoint(double distance){
        this.distance = distance;
    }

    // Serialize object to JSON format.
    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
