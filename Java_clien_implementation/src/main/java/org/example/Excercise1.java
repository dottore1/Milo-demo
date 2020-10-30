package org.example;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfigBuilder;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;

import javax.xml.crypto.Data;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Excercise1 {

    public static void main(String[] args) throws ExecutionException, InterruptedException {


        OpcUaClient client = create();

        //create Nodeid for:
        // - TemperatureNode: ns=2;s=O15-610-2/Window_2C59_temperature
        // - AlarmNode: ns=2;s=O15-610-2/Window_2C59_status
        NodeId temperatureNode = new NodeId(2, "O15-610-2/Window_2C59_temperature");
        NodeId alarmNode = new NodeId(2, "O15-610-2/Window_2C59_status");

        //read temperature
        DataValue tDataValue = client.readValue(0, TimestampsToReturn.Both, temperatureNode).get();
        Variant variant = tDataValue.getValue();
        float value = (float) variant.getValue();
        System.out.println("Temperature of the windowSensor: " + value);

        //read alarm
        DataValue aDatavalue = client.readValue(0, TimestampsToReturn.Both, alarmNode).get();
        variant = aDatavalue.getValue();
        boolean value2 = (boolean) variant.getValue();
        System.out.println("Alarm status of windowSensor: " + value2);


    }

    /**
     * @author Dottore
     * this creates and connects a client for the OPC UA endpoint.
     * @return OPCuaClient client
     */
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
