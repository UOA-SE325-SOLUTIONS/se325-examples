package se325.asyncchat.runner;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import se325.asyncchat.common.Keyboard;
import se325.asyncchat.services.ChatApplication;

import java.util.Scanner;

public class ChatServerRunner {

    private static final int SERVER_PORT = 10000;

    public static void main(String[] args) throws Exception {

        // Start the embedded servlet container and host the Web service.
        ServletHolder servletHolder = new ServletHolder(new HttpServletDispatcher());
        servletHolder.setInitParameter("javax.ws.rs.Application", ChatApplication.class.getName());

        ServletContextHandler servletCtxHandler = new ServletContextHandler();
        servletCtxHandler.setContextPath("/services");
        servletCtxHandler.addServlet(servletHolder, "/");

        Server server = new Server(SERVER_PORT);
        server.setHandler(servletCtxHandler);

        server.start();

        System.out.println("Listening on port " + SERVER_PORT + "...");

        Scanner scanner = new Scanner(System.in);
        System.out.println("Press ENTER to quit server...");

        Keyboard.readInput();

        server.stop();

        System.out.println("Server stopped!");

    }

}
