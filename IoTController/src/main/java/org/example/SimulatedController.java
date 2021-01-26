package org.example;

import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceMethod;
import com.microsoft.azure.sdk.iot.service.devicetwin.MethodResult;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class SimulatedController {
    // Connection string for your IoT Hub
    // az iot hub show-connection-string --hub-name {your iot hub name} --policy-name service
    public static final String iotHubConnectionString = "HostName=project-ad.azure-devices.net;SharedAccessKeyName=service;SharedAccessKey=cBdFVo3Xdvqz+sX5JO4/z67oL/HOyIG8gyxxQ+T73LI=";

    // Device to call direct method on.
    public static final String deviceId = "myDeviceId";

    // Name of direct method and payload.
    public static final String methodName = "SetTelemetryInterval";
    public static final int payload = 10; // Number of seconds for telemetry interval.

    public static final String methodNameHeater = "SetHeaterOn";
    public static final boolean status = true; // Number of seconds for telemetry interval.

    public static final Long responseTimeout = TimeUnit.SECONDS.toSeconds(30);
    public static final Long connectTimeout = TimeUnit.SECONDS.toSeconds(5);

    public static void main(String[] args) {

        try {
            // Create a DeviceMethod instance to call a direct method.
            DeviceMethod methodClient = DeviceMethod.createFromConnectionString(iotHubConnectionString);
            // Call the direct method
                //MethodResult result = methodClient.invoke(deviceId, methodName, responseTimeout, connectTimeout, payload);
            MethodResult result = methodClient.invoke(deviceId, methodNameHeater, responseTimeout, connectTimeout, status);

                if (result == null) {
                    throw new IOException("Direct method invoke returns null");
                }

                System.out.println("Status: " + result.getStatus());
                System.out.println("Response: " + result.getPayload());


        } catch (IOException e) {
            e.printStackTrace();
        } catch (IotHubException e) {
            e.printStackTrace();
        }

    }

}
