package org.example;

import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubConsumerAsyncClient;

import javax.swing.*;
import java.io.IOException;

public class TelemetryReaderFrame extends JFrame{
    private JPanel jPanelMain;
    private JTextArea jTextAreaTelemetryLog;


    private static final String EH_COMPATIBLE_CONNECTION_STRING_FORMAT = "Endpoint=%s/;EntityPath=%s;"
            + "SharedAccessKeyName=%s;SharedAccessKey=%s";

    // az iot hub show --query properties.eventHubEndpoints.events.endpoint --name {your IoT Hub name}
    private static final String EVENT_HUBS_COMPATIBLE_ENDPOINT = "sb://ihsuprodamres087dednamespace.servicebus.windows.net/";

    // az iot hub show --query properties.eventHubEndpoints.events.path --name {your IoT Hub name}
    private static final String EVENT_HUBS_COMPATIBLE_PATH = "iothub-ehub-project-ad-7642159-9a9b8d64f8";

    // az iot hub policy show --name service --query primaryKey --hub-name {your IoT Hub name}
    private static final String IOT_HUB_SAS_KEY = "cBdFVo3Xdvqz+sX5JO4/z67oL/HOyIG8gyxxQ+T73LI=";
    private static final String IOT_HUB_SAS_KEY_NAME = "service";


    public TelemetryReaderFrame(String title){
        super(title);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setContentPane(jPanelMain);
        this.pack();

    }

    private void connect(){
        String eventHubCompatibleConnectionString = String.format(EH_COMPATIBLE_CONNECTION_STRING_FORMAT,
                EVENT_HUBS_COMPATIBLE_ENDPOINT, EVENT_HUBS_COMPATIBLE_PATH, IOT_HUB_SAS_KEY_NAME, IOT_HUB_SAS_KEY);



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
                    System.out.println();

                    jTextAreaTelemetryLog.append("\n");

                    jTextAreaTelemetryLog.append(String.format("%nTelemetry received from partition %s:%n%s",
                            partitionEvent.getPartitionContext().getPartitionId(), partitionEvent.getData().getBodyAsString()));
                    jTextAreaTelemetryLog.append(String.format("%nApplication properties (set by device):%n%s", partitionEvent.getData().getProperties()));
                    jTextAreaTelemetryLog.append(String.format("%nSystem properties (set by IoT Hub):%n%s",
                            partitionEvent.getData().getSystemProperties()));
                }, ex -> {
                    System.out.println("Error receiving events " + ex);
                }, () -> {
                    System.out.println("Completed receiving events");
                });

    }

    public static void main(String[] args) {
        TelemetryReaderFrame frame =  new TelemetryReaderFrame("telemetry reader");
        frame.setVisible(true);
        frame.connect();
    }

}
