package org.example;

import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceMethod;
import com.microsoft.azure.sdk.iot.service.devicetwin.MethodResult;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class SimulatedController extends JFrame {

    public enum MethodsNamesEnum {
        SET_TELEMETRY_INTERVAL,
        SET_ALARM,
    }


    // Connection string for your IoT Hub
    // az iot hub show-connection-string --hub-name {your iot hub name} --policy-name service
    public static final String iotHubConnectionString = "HostName=project-ad.azure-devices.net;SharedAccessKeyName=service;SharedAccessKey=cBdFVo3Xdvqz+sX5JO4/z67oL/HOyIG8gyxxQ+T73LI=";

    // Device to call direct method on.
    public static final String deviceId = "myDeviceId";

    public static final int payload = 10; // Number of seconds for telemetry interval.
    public static final boolean status = true; // Number of seconds for telemetry interval.

    public static final Long responseTimeout = TimeUnit.SECONDS.toSeconds(30);
    public static final Long connectTimeout = TimeUnit.SECONDS.toSeconds(5);

    private DeviceMethod deviceMethod = null;

    private JPanel JPanelMain;
    private JTextArea textArea1;
    private JSlider slider1;
    private JButton distanceButton;
    private JButton alarmButton;


    public SimulatedController(String title) throws IOException {

        super(title);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setContentPane(JPanelMain);
        this.pack();
        slider1.setMajorTickSpacing(50);
        slider1.setMinorTickSpacing(10);
        slider1.setPaintTicks(true);
        slider1.setPaintLabels(true);


        distanceButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                sendTelemetryIntervalData();
                System.out.println("Distance Clicked");
            }
        });


        alarmButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                sendAlarmData();
                System.out.println("Alarm Button Clicked");
            }
        });

        deviceMethod = DeviceMethod.createFromConnectionString(iotHubConnectionString);
    }

    public static void main(String[] args) throws IOException {

        SimulatedController frame = new SimulatedController("Controller frame");
        frame.setVisible(true);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);

    }

    public void sendTelemetryIntervalData() {
        try {
            // Call the direct method
            MethodResult result = deviceMethod.invoke(
                    deviceId,
                    MethodsNamesEnum.SET_TELEMETRY_INTERVAL.name(),
                    responseTimeout,
                    connectTimeout,
                    status
            );

            if (result == null) {
                throw new IOException("Direct method invoke returns null");
            }

            System.out.println("sendTelemetryIntervalData Status: " + result.getStatus());
            System.out.println("sendTelemetryIntervalData Response: " + result.getPayload());

        } catch (IotHubException | IOException e) {
            e.printStackTrace();
        }
    }

    public void sendAlarmData() {
        try {
            // Call the direct method
            MethodResult result = deviceMethod.invoke(
                    deviceId,
                    MethodsNamesEnum.SET_ALARM.name(),
                    responseTimeout,
                    connectTimeout,
                    status
            );

            if (result == null) {
                throw new IOException("Direct method invoke returns null");
            }

            System.out.println("sendAlarmData Status: " + result.getStatus());
            System.out.println("sendAlarmData Response: " + result.getPayload());

        } catch (IotHubException | IOException e) {
            e.printStackTrace();
        }
    }

}
