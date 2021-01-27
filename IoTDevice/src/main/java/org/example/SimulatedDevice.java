package org.example;

import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.device.Message;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class SimulatedDevice {

    // az iot hub device-identity show-connection-string --hub-name {YourIoTHubName} --device-id MyJavaDevice --output table
    private static final String connString = "HostName=cstep.azure-devices.net;DeviceId=MyJavaDevice;SharedAccessKey=OF/2wyYVslPSrwhn/bd8bzTpbAheTBWq0/SkV7yEYtY=";
    private static DeviceClient client;
    private static int telemetryInterval = 50;
    private static boolean isAlarm = false;
    private static UltraSonicSensor ultraSonicSensor = null;

    public SimulatedDevice() throws URISyntaxException, IOException {
        // Connect to the IoT hub.
        // Using the MQTT protocol to connect to IoT Hub
        client = new DeviceClient(connString, IotHubClientProtocol.MQTT);
        client.open();
        // Register to receive direct method calls.
        client.subscribeToDeviceMethod(
                new DirectMethodCallback(this),
                null,
                new DirectMethodStatusCallback(),
                null
        );


        System.out.println("Device connected to hub!");
    }

    public void setTelemetryInterval(int interval) {
        System.out.println("Set interval to: " + interval);
        telemetryInterval = interval;
    }

    public void setAlarm(boolean state) {
        System.out.println("Setting alarm to:  " + state);
        isAlarm = state;
    }

    private void start() throws Exception {

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {

                    System.out.println("Sending message");
                    sendMessage();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, 2000);
    }

    private void sendMessage() throws Exception {
        TelemetryDataPoint data = new TelemetryDataPoint();
        data.setAlarm(isAlarm);
        data.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
        data.setDistance(ultraSonicSensor.getDistance());
        data.setTelemetryInterval(telemetryInterval);

        String dataJson = data.serialize();
        Message msg = new Message(dataJson);

        Object lockobj = new Object();

        System.out.println("Sending Message" + dataJson);

        client.sendEventAsync(msg, new EventCallback(), lockobj);

        synchronized (lockobj) {
            lockobj.wait();
        }

    }

    public static void main(String[] args) throws Exception {
        try {
            ultraSonicSensor = new UltraSonicSensor(
                    0, //ECO PIN (physical 11)
                    7, //TRIG PIN (pysical 22)
                    1000, //REJECTION_START ; long (nano seconds)
                    1000 //REJECTION_TIME ; long (nano seconds))
            );
            SimulatedDevice device = new SimulatedDevice();
            device.start();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
