package com.example.smartalarm.azure;

import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceMethod;
import com.microsoft.azure.sdk.iot.service.devicetwin.MethodResult;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AlarmController {

    // Connection string for your IoT Hub
    // az iot hub show-connection-string --hub-name {your iot hub name} --policy-name service
    public static final String iotHubConnectionString = "HostName=cstep.azure-devices.net;SharedAccessKeyName=service;SharedAccessKey=4CT3Evjb8j8aLAf3OvGdoJkbtpQI6YYeH94SWfwUhCA=";

    // Device to call direct method on.
    public static final String deviceId = "MyJavaDevice";

    public static final Long responseTimeout = TimeUnit.SECONDS.toSeconds(30);
    public static final Long connectTimeout = TimeUnit.SECONDS.toSeconds(5);




    public void setAlarm(boolean payload) {
        try {
            // Call the direct method
            DeviceMethod deviceMethod = DeviceMethod.createFromConnectionString(iotHubConnectionString);
            MethodResult result = deviceMethod.invoke(
                    deviceId,
                    "SET_ALARM",
                    responseTimeout,
                    connectTimeout,
                    payload
            );

            if (result == null) {
                throw new IOException("Direct method invoke returns null");
            }

            System.out.println("sendAlarmData Status: " + result.getStatus());
            System.out.println("sendAlarmData Response: " + result.getPayload());
            System.out.println(String.format("Alarme %s!", payload ? "ativado" : "desativado"));

        } catch (IotHubException | IOException e) {
            System.out.println("Impossivel conectar ao IoTHUB.");
            e.printStackTrace();
        }
    }

}
