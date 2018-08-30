/*
 * ClientException.java
 *
 * created at 2017-09-20 by Petya Petrova p.petrova <p.petrova@seeburger.com>
 *
 * Copyright (c) SEEBURGER AG, Germany. All Rights Reserved.
 */
package clientserver.server;


import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import clientserver.client.ChatClient;


/**
 * Represents the server side from Client-Server application. Starts by default on localhost at port 7777.
 * <p>
 * created at Aug 16, 2017 by @author Petya Petrova p.petrova
 */
public class ChatServer
{
    private static final int PORT_SERVER = 7777;
    private static final String WAIT_CLIENT_MSG = "Waiting for client to connect...";
    private static final String START_SERVER_MSG = "Starting server at port: ";
    private static final Logger LOGGER = Logger.getLogger(ChatServer.class.getName());
    private static ChatServer server;


    private ChatServer()
    {}


    public static synchronized ChatServer getInstance()
    {
        if (server == null)
        {
            server = new ChatServer();
        }
        return server;
    }


    /**
     * Creates {@link ServerSocket} and waits for {@link ChatClient}s to connect. {@link ChatCLient}s are represented with {@link Executors}
     * thread pool.
     *
     * @throws IOException
     */
    private void startServer()
    {

        LOGGER.log(Level.INFO, START_SERVER_MSG + PORT_SERVER);
        try (ServerSocket server = new ServerSocket(PORT_SERVER))
        {
            LOGGER.log(Level.INFO, WAIT_CLIENT_MSG);
            while (true)
            {
                Thread requestThread = new Thread(new ClientHandler(server));
                requestThread.start();
                requestThread.join();
            }
        }
        catch (IOException | InterruptedException e)
        {
            LOGGER.log(Level.SEVERE, "Failed to start server!", e);
            throw new RuntimeException("Failed to start server!");
        }
    }


    /**
     * Entry point in the application. Starts the {@link ChatServer} on the default host and port.
     *
     * @param args
     */
    public static void main(String[] args)
    {
        ChatServer chatServer = ChatServer.getInstance();
        chatServer.startServer();
    }
}
