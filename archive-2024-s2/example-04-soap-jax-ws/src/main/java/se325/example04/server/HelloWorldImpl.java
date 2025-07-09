package se325.example04.server;

import javax.jws.WebService;

@WebService(endpointInterface = "se325.example04.server.HelloWorld")
public class HelloWorldImpl implements HelloWorld {

    @Override
    public String getHelloWorldAsString(String name) {
        return "Hello " + name + ", from your friendly JAX-WS SOAP Service!";
    }
}
