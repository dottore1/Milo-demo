package com.mycompany.mavenopcua;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author aqs
 */

import java.util.List;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfigBuilder;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;

public class Write {

    public static void main(String[] args) {
        try 
        {
            List<EndpointDescription> endpoints = DiscoveryClient.getEndpoints("opc.tcp://10.112.254.47:12686/milo").get();

            OpcUaClientConfigBuilder cfg = new OpcUaClientConfigBuilder();
            cfg.setEndpoint(endpoints.get(0));

            OpcUaClient client = OpcUaClient.create(cfg.build());
            client.connect().get();

            NodeId nodeId  = new NodeId(2, "O15-610-2/Power_2A7B_turned");

            client.writeValue(nodeId, DataValue.valueOnly(new Variant(true))).get();

        }
        catch(Throwable ex)
        {
            ex.printStackTrace();
        }

    }
}
