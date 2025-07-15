package se325.example01.basictcp;

import se325.util.Keyboard;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        try {

            InetAddress serverAddress = InetAddress.getByName(Keyboard.prompt("Server address:"));
            int serverPort = Integer.parseInt(Keyboard.prompt("Server port:"));
            int num1 = Integer.parseInt(Keyboard.prompt("Enter first number:"));
            int num2 = Integer.parseInt(Keyboard.prompt("Enter second number:"));

            try (Socket socket = new Socket(serverAddress, serverPort)) {

                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                out.writeInt(num1);
                out.writeInt(num2);

                int product = in.readInt();
                System.out.println("Received: " + product);

            }

        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        }
    }
}
