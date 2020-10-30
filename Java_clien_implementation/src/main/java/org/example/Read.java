package org.example;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfigBuilder;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;

import java.util.List;

public class Read {

    public static void main( String[] args )
    {
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


            //read values from Nodeid - ns=2;s=O15-610-2/Motion_2BBF_illuminance
            NodeId nodeid = new NodeId(2, "O15-610-2/Motion_2BBF_illuminance");

            //read actual values from the node/sensor
            DataValue dataValue = client.readValue(0, TimestampsToReturn.Both, nodeid).get();
            System.out.println("Datavalue = " + dataValue);

            Variant variant = dataValue.getValue();
            System.out.println("Variant: " + variant);

            float value = (float) variant.getValue();
            System.out.println("dataType: " + variant.getDataType());
            System.out.println("Value: " + value);






        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
