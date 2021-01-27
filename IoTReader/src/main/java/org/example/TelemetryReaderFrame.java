package org.example;

import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubConsumerAsyncClient;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class TelemetryReaderFrame extends JFrame {
    private JPanel jPanelMain;
    private JTextArea jTextAreaTelemetryLog;
    private JScrollPane jScrollPane;


    private static final String EH_COMPATIBLE_CONNECTION_STRING_FORMAT = "Endpoint=%s/;EntityPath=%s;"
            + "SharedAccessKeyName=%s;SharedAccessKey=%s";

    // az iot hub show --query properties.eventHubEndpoints.events.endpoint --name {your IoT Hub name}
    private static final String EVENT_HUBS_COMPATIBLE_ENDPOINT = "sb://ihsuprodamres003dednamespace.servicebus.windows.net/";

    // az iot hub show --query properties.eventHubEndpoints.events.path --name {your IoT Hub name}
    private static final String EVENT_HUBS_COMPATIBLE_PATH = "iothub-ehub-cstep-6674549-5c7ca2abb8";

    // az iot hub policy show --name service --query primaryKey --hub-name {your IoT Hub name}
    private static final String IOT_HUB_SAS_KEY = "4CT3Evjb8j8aLAf3OvGdoJkbtpQI6YYeH94SWfwUhCA=";
    private static final String IOT_HUB_SAS_KEY_NAME = "service";


    public TelemetryReaderFrame(String title) {
        super(title);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);

        jScrollPane = new JScrollPane();
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane.setAutoscrolls(true);
        jTextAreaTelemetryLog.setBounds(20, 20, 500, 400);


        jScrollPane.getViewport().setBackground(Color.WHITE);
        jScrollPane.getViewport().add(jTextAreaTelemetryLog);

        add(jScrollPane);

        final int[] verticalScrollBarMaximumValue = {jScrollPane.getVerticalScrollBar().getMaximum()};
        jScrollPane.getVerticalScrollBar().addAdjustmentListener(e -> {
            if ((verticalScrollBarMaximumValue[0] - e.getAdjustable().getMaximum()) == 0)
                return;
            e.getAdjustable().setValue(e.getAdjustable().getMaximum());
            verticalScrollBarMaximumValue[0] = jScrollPane.getVerticalScrollBar().getMaximum();
        });


    }

    private void connect() {
        String eventHubCompatibleConnectionString = String.format(
                EH_COMPATIBLE_CONNECTION_STRING_FORMAT,
                EVENT_HUBS_COMPATIBLE_ENDPOINT,
                EVENT_HUBS_COMPATIBLE_PATH,
                IOT_HUB_SAS_KEY_NAME,
                IOT_HUB_SAS_KEY
        );


        EventHubClientBuilder eventHubClientBuilder = new EventHubClientBuilder();
        eventHubClientBuilder.consumerGroup(EventHubClientBuilder.DEFAULT_CONSUMER_GROUP_NAME)
                .connectionString(eventHubCompatibleConnectionString);

        try (EventHubConsumerAsyncClient eventHubConsumerAsyncClient = eventHubClientBuilder.buildAsyncConsumerClient()) {

            receiveFromAllPartitions(eventHubConsumerAsyncClient);


            // Shut down cleanly.
            System.out.println("Press ENTER to exit.");
            try {
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Shutting down...");
        }
    }

    private void receiveFromAllPartitions(EventHubConsumerAsyncClient eventHubConsumerAsyncClient) {
        eventHubConsumerAsyncClient
                .receive(false) // set this to false to read only the newly available events
                .subscribe(partitionEvent -> {
                    TelemetryData telemetryData = TelemetryData.deserialize(
                            partitionEvent.getData().getBodyAsString()
                    );

                    System.out.println(partitionEvent.getData().getBodyAsString());

                    jTextAreaTelemetryLog.append(
                            String.format(
                                    "[%s] Distância: %s, Alarme Ligado: %s. \n",
                                    java.time.LocalDateTime.now(),
                                    telemetryData.getDistance(),
                                    telemetryData.getAlarm()
                            )
                    );
//                    jTextAreaTelemetryLog.append(
//                            String.format("%n Telemetry received from partition %s:%n%s", partitionEvent.getPartitionContext().getPartitionId(), partitionEvent.getData().getBodyAsString())
//                    );
//                    jTextAreaTelemetryLog.append(
//                            String.format("%nApplication properties (set by device):%n%s", partitionEvent.getData().getProperties()));
//                    jTextAreaTelemetryLog.append(
//                            String.format("%nSystem properties (set by IoT Hub):%n%s", partitionEvent.getData().getSystemProperties())
//                    );

                }, ex -> {
                    System.out.println("Error receiving events " + ex);
                }, () -> {
                    System.out.println("Completed receiving events");
                });

    }

    public static void main(String[] args) {
        TelemetryReaderFrame frame = new TelemetryReaderFrame("Telemetry Reader");
        frame.setVisible(true);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.connect();


    }

}
