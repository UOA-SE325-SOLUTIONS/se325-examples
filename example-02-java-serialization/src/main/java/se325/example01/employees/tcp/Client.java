package se325.example01.employees.tcp;

import se325.util.Keyboard;
import se325.example01.employees.Employee;
import se325.example01.employees.EmployeeRequest;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        try {

            InetAddress serverAddress = InetAddress.getByName(Keyboard.prompt("Server address:"));
            int serverPort = Integer.parseInt(Keyboard.prompt("Server port:"));
            String empName = Keyboard.prompt(("Enter employee name to find:"));

            try (Socket socket = new Socket(serverAddress, serverPort)) {

                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                out.writeObject(new EmployeeRequest(empName));

                boolean found = in.readBoolean();

                if (found) {
                    Employee emp = (Employee) in.readObject();
                    System.out.println("Employee found!");
                    System.out.println(emp);
                }
                else {
                    System.out.println("No employee found with that name.");
                }

            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
