package org.example;

import com.google.gson.Gson;

public class TelemetryDataPoint {
    public double distance;
    public boolean alarm;
    public String date;
    public int telemetryInterval;

    public TelemetryDataPoint() {
        this.distance = -1;
        this.alarm = false;
        this.date = null;
        this.telemetryInterval = 0;
    }

    public TelemetryDataPoint(double distance, boolean alarm, String date, int telemetryInterval) {
        this.distance = distance;
        this.alarm = alarm;
        this.date = date;
        this.telemetryInterval = telemetryInterval;
    }

    // Serialize object to JSON format.
    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setAlarm(boolean alarm) {
        this.alarm = alarm;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTelemetryInterval(int telemetryInterval) {
        this.telemetryInterval = telemetryInterval;
    }
}
