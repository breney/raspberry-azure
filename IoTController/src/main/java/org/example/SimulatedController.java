package org.example;

import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceMethod;
import com.microsoft.azure.sdk.iot.service.devicetwin.MethodResult;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class SimulatedController extends JFrame {

    // Connection string for your IoT Hub
    // az iot hub show-connection-string --hub-name {your iot hub name} --policy-name service
    public static final String iotHubConnectionString = "HostName=cstep.azure-devices.net;SharedAccessKeyName=service;SharedAccessKey=4CT3Evjb8j8aLAf3OvGdoJkbtpQI6YYeH94SWfwUhCA=";

    // Device to call direct method on.
    public static final String deviceId = "MyJavaDevice";
    public static final String methodName = "SetTelemetryInterval";

    public static final Long responseTimeout = TimeUnit.SECONDS.toSeconds(30);
    public static final Long connectTimeout = TimeUnit.SECONDS.toSeconds(5);

    private DeviceMethod deviceMethod;

    private JPanel JPanelMain;
    private JTextArea textArea1;
    private JSlider slider1;
    private JButton distanceButton;
    private JToggleButton alarmButton;


    public SimulatedController(String title) {

        super(title);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setContentPane(JPanelMain);
        this.pack();
        slider1.setMajorTickSpacing(50);
        slider1.setMinorTickSpacing(10);
        slider1.setPaintTicks(true);
        slider1.setPaintLabels(true);


        try {
            deviceMethod = DeviceMethod.createFromConnectionString(iotHubConnectionString);
            logEvent("Connectando ao dispotivo...");
        } catch (IOException e) {
            logEvent("Impossivel conectar ao IoTHUB.");
        }


        distanceButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                sendTelemetryIntervalData(slider1.getValue());
                System.out.println("Distance Clicked value: " + slider1.getValue());
            }
        });

        alarmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean value = ((JToggleButton) e.getSource()).isSelected();
                alarmButton.setText(value ? "Desativar" : "Ativar");
                sendAlarmData(value);
            }
        });


    }

    public static void main(String[] args) {
        SimulatedController frame = new SimulatedController("Controller frame");
        frame.setVisible(true);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
    }

    public void sendTelemetryIntervalData(float payload) {
        try {
            // Call the direct method
            MethodResult result = deviceMethod.invoke(
                    deviceId,
                    "SEND_TELEMETRY_DATA",
                    responseTimeout,
                    connectTimeout,
                    payload
            );

            if (result == null) {
                throw new IOException("Direct method invoke returns null");
            }
            logEvent("Distância minima alterada.");

            System.out.println("sendTelemetryIntervalData Status: " + result.getStatus());
            System.out.println("sendTelemetryIntervalData Response: " + result.getPayload());

        }
        catch (IotHubException | IOException e) {
            logEvent("Impossivel conectar ao IoTHUB.");
            e.printStackTrace();
        }
    }

    public void sendAlarmData(boolean payload) {
        try {
            // Call the direct method
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
            logEvent(String.format("Alarme %s!", payload ? "ativado" : "desativado"));

        } catch (IotHubException | IOException e) {
            logEvent("Impossivel conectar ao IoTHUB.");
            e.printStackTrace();
        }
    }

    public void logEvent(String log) {
        textArea1.append(String.format("%s \n", log));
    }

}
