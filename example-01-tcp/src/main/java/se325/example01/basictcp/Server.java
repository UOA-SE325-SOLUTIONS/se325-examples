package se325.example01.basictcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) {
        try (ServerSocket socket = new ServerSocket(10000)) {

            InetAddress serverHost = InetAddress.getLocalHost();
            System.out.println("Server destination: " + serverHost.getHostAddress() + ":" + socket.getLocalPort());

            /* Repeatedly handle requests for processing. */
            while (true) {

                try (Socket clientConnection = socket.accept()) {
                    DataInputStream in = new DataInputStream(clientConnection.getInputStream());
                    DataOutputStream out = new DataOutputStream(clientConnection.getOutputStream());

                    /* Read numbers to multiply. */
                    int x = in.readInt();
                    int y = in.readInt();

                    /* Compute the product and send it back to the client. */
                    int result = x * y;
                    out.writeInt(result);

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
