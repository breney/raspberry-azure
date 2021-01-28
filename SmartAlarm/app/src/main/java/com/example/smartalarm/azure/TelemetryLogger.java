package com.example.smartalarm.azure;

import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubConsumerAsyncClient;


public class TelemetryLogger {
    private Callback callback;

    public interface Callback {
        void call(TelemetryData telemetryData);
    }

    private static final String EH_COMPATIBLE_CONNECTION_STRING_FORMAT = "Endpoint=%s/;EntityPath=%s;"
            + "SharedAccessKeyName=%s;SharedAccessKey=%s";

    // az iot hub show --query properties.eventHubEndpoints.events.endpoint --name {your IoT Hub name}
    private static final String EVENT_HUBS_COMPATIBLE_ENDPOINT = "sb://ihsuprodamres003dednamespace.servicebus.windows.net/";

    // az iot hub show --query properties.eventHubEndpoints.events.path --name {your IoT Hub name}
    private static final String EVENT_HUBS_COMPATIBLE_PATH = "iothub-ehub-cstep-6674549-5c7ca2abb8";

    // az iot hub policy show --name service --query primaryKey --hub-name {your IoT Hub name}
    private static final String IOT_HUB_SAS_KEY = "4CT3Evjb8j8aLAf3OvGdoJkbtpQI6YYeH94SWfwUhCA=";
    private static final String IOT_HUB_SAS_KEY_NAME = "service";

    public TelemetryLogger() {

    }

    public void connect() {
        String eventHubCompatibleConnectionString = String.format(
                EH_COMPATIBLE_CONNECTION_STRING_FORMAT,
                EVENT_HUBS_COMPATIBLE_ENDPOINT,
                EVENT_HUBS_COMPATIBLE_PATH,
                IOT_HUB_SAS_KEY_NAME,
                IOT_HUB_SAS_KEY
        );

        EventHubConsumerAsyncClient consumer = new EventHubClientBuilder()
                .connectionString(eventHubCompatibleConnectionString)
                .consumerGroup(EventHubClientBuilder.DEFAULT_CONSUMER_GROUP_NAME)
                .buildAsyncConsumerClient();

        // BEGIN: com.azure.messaging.eventhubs.eventhubconsumerasyncclient.receive#boolean
        // Receives events from all partitions from the beginning of each partition.
//        consumer.receive(true).subscribe(partitionEvent -> {
//            PartitionContext context = partitionEvent.getPartitionContext();
//            EventData event = partitionEvent.getData();
//            System.out.printf("Event %s is from partition %s%n.", event.getSequenceNumber(), context.getPartitionId());
//        });

//        try (EventHubConsumerAsyncClient eventHubConsumerAsyncClient = c.buildAsyncConsumerClient()) {

            receiveFromAllPartitions(consumer);

//
//            // Shut down cleanly.
//            System.out.println("Press ENTER to exit.");
//            try {
//                System.in.read();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            System.out.println("Shutting down...");
//        }
    }

    private void receiveFromAllPartitions(EventHubConsumerAsyncClient eventHubConsumerAsyncClient) {

//
        eventHubConsumerAsyncClient
                .receive(false) // set this to false to read only the newly available events
                .subscribe(partitionEvent -> {

                    TelemetryData telemetryData = TelemetryData.deserialize(
                            partitionEvent.getData().getBodyAsString()
                    );

                    this.callback.call(telemetryData);


//                    String.format(
//                            "[%s] Distância: %s, Alarme Ligado: %s, Alerta: %s\n",
//                            telemetryData.getDate(),
//                            telemetryData.getDistance(),
//                            telemetryData.getAlarm(),
//                            telemetryData.shouldAlert()
//                    );

                }, ex -> {
                    System.out.println("Error receiving events " + ex);
                }, () -> {
                    System.out.println("Completed receiving events");
                });

    }


    public void setCallback(Callback callback) {
        this.callback = callback;

    }


}