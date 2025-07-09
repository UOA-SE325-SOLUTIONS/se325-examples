package se325.example04.client;

import se325.example04.server.HelloWorld;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.URL;

public class HelloWorldClient {

    public static void main(String[] args) throws Exception {

        URL url = new URL("http://localhost:10000/ws/hello");

        QName qname = new QName("http://server.example04.se325/", "HelloWorldImplService");
        Service service = Service.create(url, qname);
        HelloWorld helloService = service.getPort(HelloWorld.class);

        String name = Keyboard.prompt("Enter your name:");
        String response = helloService.getHelloWorldAsString(name);

        System.out.println("Server says: " + response);

    }
}
