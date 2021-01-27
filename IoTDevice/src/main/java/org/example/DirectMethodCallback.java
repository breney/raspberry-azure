package org.example;

import com.microsoft.azure.sdk.iot.device.DeviceTwin.DeviceMethodCallback;
import com.microsoft.azure.sdk.iot.device.DeviceTwin.DeviceMethodData;
import com.pi4j.io.gpio.*;

public class DirectMethodCallback implements DeviceMethodCallback {

    private static final int METHOD_SUCCESS = 200;
    private static final int METHOD_NOT_DEFINED = 404;
    private static final int INVALID_PARAMETER = 400;

    private final SimulatedDevice simulatedDevice;

    public DirectMethodCallback(SimulatedDevice simulatedDevice) {
        this.simulatedDevice = simulatedDevice;
    }

    private void blinkLed(final int times) {
//        new Thread() {
//            @Override
//            public void run() {
        GpioPinDigitalOutput led = simulatedDevice.getLed();

        try {
            for (int i = 0; i < (times * 2) - 1; i++) {
                led.toggle();
                Thread.sleep(500);
            }

            led.low();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


//        }.start();

    }

    private DeviceMethodData handleSetAlarm(String payload, String methodName) {
        try {

            boolean state = Boolean.parseBoolean(payload);
            simulatedDevice.setAlarm(state);

            if (state) {
                blinkLed(3);
            }

            return new DeviceMethodData(
                    METHOD_SUCCESS,
                    "Executed direct method " + methodName
            );
        } catch (NumberFormatException e) {
            return new DeviceMethodData(
                    INVALID_PARAMETER,
                    "Invalid parameter " + payload
            );
        }
    }

    private DeviceMethodData handleSendTelemetryData(String payload, String methodName) {
        try {
            int interval = Integer.parseInt(payload);
            simulatedDevice.setTelemetryInterval(interval);

            return new DeviceMethodData(
                    METHOD_SUCCESS,
                    "Executed direct method " + methodName
            );

        } catch (NumberFormatException e) {
            return new DeviceMethodData(
                    INVALID_PARAMETER,
                    "Invalid parameter " + payload
            );
        }
    }

    @Override
    public DeviceMethodData call(String methodName, Object methodData, Object context) {

        System.out.println("Calling DirectMethodCallback");
        String payload = new String((byte[]) methodData);
        switch (methodName) {
            case "SET_ALARM":
                return handleSetAlarm(payload, methodName);
            case "SEND_TELEMETRY_DATA":
                return handleSendTelemetryData(payload, methodName);
            default:
                return new DeviceMethodData(
                        METHOD_NOT_DEFINED,
                        "Not defined direct method " + methodName
                );

        }
    }
}