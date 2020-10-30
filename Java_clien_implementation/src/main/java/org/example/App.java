package org.example;

import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;

import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        try {
            List<EndpointDescription> endpoints = DiscoveryClient
                    .getEndpoints("opc.tcp://localhost:12686/milo")
                    .get();

            for (EndpointDescription endpoint : endpoints) {
                System.out.println(endpoint);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
