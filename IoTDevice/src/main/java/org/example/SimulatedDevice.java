package org.example;

import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.device.Message;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;

public class SimulatedDevice {

    // az iot hub device-identity show-connection-string --hub-name {YourIoTHubName} --device-id MyJavaDevice --output table
    private static final String connString = "HostName=cstep.azure-devices.net;DeviceId=MyJavaDevice;SharedAccessKey=OF/2wyYVslPSrwhn/bd8bzTpbAheTBWq0/SkV7yEYtY=";
    private static DeviceClient client;
    private static int telemetryInterval = 1000;
    private static boolean heaterState = false;
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
        System.out.println("Direct method # Setting telemetry interval (seconds): " + interval);
        telemetryInterval = interval * 1000;
    }

    /*public void SetHeaterOn(boolean state) {
        System.out.println("Direct method # Setting Heater On: " + state);
        heaterState=state;
    }*/

    private void start() throws Exception {

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {

                    System.out.println("Sending message");
                    //sendMessage();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, 2000);
    }

    private void sendMessage() throws Exception {

        int distanceValue = ultraSonicSensor.getDistance();
        TelemetryDataPoint data = new TelemetryDataPoint(distanceValue);
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
