package org.example;

import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.device.Message;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
public class SimulatedDevice {

    // az iot hub device-identity show-connection-string --hub-name {YourIoTHubName} --device-id MyJavaDevice --output table
    private static String connString = "HostName=uma-ctesp-tpsi-arqd-2020.azure-devices.net;DeviceId=MyJavaDevice;SharedAccessKey=9NWJ79SzLETAg3ZCRQk7sUOcp1zOhPSSdrWbPC/tUi0=";

    // Using the MQTT protocol to connect to IoT Hub
    private static IotHubClientProtocol protocol = IotHubClientProtocol.MQTT;
    private static DeviceClient client;
    private static int telemetryInterval = 1000;
    private static boolean heaterState =false;
    private static UltraSonicSensor sonic = null;
    public SimulatedDevice() throws URISyntaxException, IOException {
        // Connect to the IoT hub.
        client = new DeviceClient(connString, protocol);
        client.open();

        // Register to receive direct method calls.
        client.subscribeToDeviceMethod(new DirectMethodCallback(this), null, new DirectMethodStatusCallback(), null);


        System.out.println("Device connected to hub!");
    }

    public void setTelemetryInterval(int interval)
    {
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
                    sendMessage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(task,0,1000);
    }

    private void sendMessage() throws Exception {

        int distanceValue = sonic.getDistance();
        TelemetryDataPoint data = new TelemetryDataPoint(distanceValue);
        String dataJson = data.serialize();
        Message msg = new Message(dataJson);

        Object lockobj = new Object();

        System.out.println("Sending Message" + dataJson);

        client.sendEventAsync(msg, new EventCallback(),lockobj);

        synchronized (lockobj) {
            lockobj.wait();
        }

    }

    public static void main(String[] args) throws Exception {
       sonic = new UltraSonicSensor(0,//ECO PIN (physical 11)
               7,//TRIG PIN (pysical 22)
               1000,//REJECTION_START ; long (nano seconds)
               1000); //REJECTION_TIME ; long (nano seconds))

        try {
            SimulatedDevice device = new SimulatedDevice();
            device.start();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
