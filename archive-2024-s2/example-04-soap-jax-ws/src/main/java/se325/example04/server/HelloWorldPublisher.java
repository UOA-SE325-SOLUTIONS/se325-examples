package se325.example04.server;

import javax.xml.ws.Endpoint;

public class HelloWorldPublisher {

    public static void main(String[] args) {
        Endpoint.publish("http://localhost:10000/ws/hello", new HelloWorldImpl());
        System.out.println("WSDL published: http://localhost:10000/ws/hello");
    }

}
