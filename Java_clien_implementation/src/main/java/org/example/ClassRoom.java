package org.example;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfigBuilder;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MonitoringMode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoredItemCreateRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoringParameters;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

public class ClassRoom {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        OpcUaClient client = create();

        //Create NodeId for:
        // - ns=2;O15-610-2/Power_2B67_turned
        // - ns=2;O15-610-2/Motion_2BBF_temperature
        NodeId powerPlug = new NodeId(2, "O15-610-2/Power_2B67_turned");
        NodeId motionTemperature = new NodeId(2, "O15-610-2/Motion_2BBF_temperature");


        //read Motionsensor Temperature
        DataValue value = client.readValue(0, TimestampsToReturn.Both, motionTemperature).get();
        Variant variant = value.getValue();
        float flo = (float) variant.getValue();
        System.out.println("Temperature of Motionsensor: " + flo + "C");


        //write on/off to powerPlug
        System.out.println("Current status of powerplug: " + readValuePower(client, powerPlug));
        if (readValuePower(client, powerPlug)) {
            System.out.println("Turning off powerplug" +
                    "\nWriting...");

            DataValue datavalue = new DataValue(new Variant(false));
            client.writeValue(powerPlug, datavalue).get();

            System.out.println("New status: " + readValuePower(client, powerPlug));
        } else {
            System.out.println("Turning on powerplug" +
                    "\nWriting...");

            DataValue datavalue = new DataValue(new Variant(true));
            client.writeValue(powerPlug, datavalue).get();

            System.out.println("New status: " + readValuePower(client, powerPlug));
        }


        //read value
        ReadValueId valueId = new ReadValueId(powerPlug, AttributeId.Value.uid(), null, null);

        //parameters
        int clientHandle = 1;
        MonitoringParameters parameters = new MonitoringParameters(uint(clientHandle), 1000.0, null, uint(10), true);

        //create request
        MonitoredItemCreateRequest request = new MonitoredItemCreateRequest(valueId, MonitoringMode.Reporting, parameters);

        //the actual consumer
        BiConsumer<UaMonitoredItem, DataValue> consumer =
                (item, value4) ->
                        System.out.format("%s -> %s%n", item, value4);

        //setting the consumer afther the subscription creation

        BiConsumer<UaMonitoredItem, Integer> onItemCreated =
                (monitoredItem, id) ->
                        monitoredItem.setValueConsumer(consumer);

        //creating the subscription

        UaSubscription subscription =
                client.getSubscriptionManager().createSubscription(1000.0).get();

        List<UaMonitoredItem> items = subscription.createMonitoredItems(
                TimestampsToReturn.Both,
                Arrays.asList(request),
                onItemCreated
        ).get();

        for (UaMonitoredItem item : items) {
            if (item.getStatusCode().isGood()) {
                System.out.println("item created for nodeId=" + item.getReadValueId().getNodeId());
            } else{
                System.out.println("failed to create item for nodeId=" + item.getReadValueId().getNodeId() + " (status=" + item.getStatusCode() + ")");
            }
        }

        // let the example run for 50 seconds then terminate
        Thread.sleep(50_000);




    }


    /**
     * @author Dottore
     * @param client the client used to connect to the server.
     * @param nodeId  The node used to retrieve the value(powerplug node)
     * @return The readed value from the sensor.
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static boolean readValuePower(OpcUaClient client, NodeId nodeId) throws ExecutionException, InterruptedException {
        DataValue datavalue = client.readValue(0, TimestampsToReturn.Both, nodeId).get();
        Variant variant = datavalue.getValue();
        return (boolean) variant.getValue();
    }


    public static OpcUaClient create() {
        try {
            //get all endpoints from server
            List<EndpointDescription> endpoints = DiscoveryClient
                    .getEndpoints("opc.tcp://localhost:12686/milo")
                    .get();
            System.out.println(endpoints);
            System.out.println("connected.");
            //loading endpoints into configuration
            OpcUaClientConfigBuilder cfg = new OpcUaClientConfigBuilder();
            cfg.setEndpoint(endpoints.get(0));


            //setting up client with config
            OpcUaClient client = OpcUaClient.create(cfg.build());

            //connecting client
            client.connect().get();

            return client;

        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

}
