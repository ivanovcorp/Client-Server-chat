/*
 * ClientException.java
 *
 * created at 2017-09-20 by Petya Petrova p.petrova <p.petrova@seeburger.com>
 *
 * Copyright (c) SEEBURGER AG, Germany. All Rights Reserved.
 */
package clientserver.client;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import clientserver.server.ChatServer;


/**
 * Represents client side from Client-Server application. Starts on the given host and port, or on localhost at port 7777 by default.
 * <p>
 * created at Aug 16, 2017 by @author Petya Petrova p.petrova
 */
public class ChatClient
{
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 7777;
    private static final int ARGS_NUM = 2;
    private static final String CURSOR = ">";
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final String SPACE_SEPARATOR = " ";
    private static final String MESSAGE_SEPARATOR = "#";
    private static final String USER_CONNECTED_MSG = "Connected to the Server!";
    private static final String ENTER_USERNAME_MSG = "Enter your username";
    private static final String INVALID_USERNAME_MSG = "Invalid username!Please enter correct username: ";
    private static final String USERNAME_REGEX = "^(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$";
    private static final int MIN_LENGTH_USERNAME = 3;
    private static final int MAX_LENGTH_USERNAME = 50;
    private static final String EXIT_CMD = "!exit";
    private static final String UPDATE_CMD = "!update";
    private static final Logger LOGGER = Logger.getLogger(ChatClient.class.getName());
    private final String username;
    private final int port;
    private final String host;


    /**
     * Constructs {@link ChatClient} with provided from the user username, default host: localhost and default port: 7777.
     *
     * @param username is provided by the client and should be unique, with length more than 3 and less than 50 symbols.
     */
    public ChatClient(String username)
    {
        this(username, DEFAULT_HOST, DEFAULT_PORT);
    }


    /**
     * Constructs {@link ChatClient} by provided arguments from client.
     *
     * @param username is provided {@link String}, should be unique and between 3 and 50 symbols.
     * @param host is provided {@link String}.
     * @param port is provided int.
     */
    public ChatClient(String username, String host, int port)
    {
        this.username = username;
        this.host = host;
        this.port = port;
    }


    /**
     * Connecting to the {@link ChatServer}. Opens a {@link Socket} at given or default host and port. Creates {@link BufferedReader} and
     * {@link PrintWriter} for communication between {@link ChatClient} and {@link ChatServer}.
     */
    private void sendMessage(String message)
    {
        try (Socket socket = new Socket(InetAddress.getByName(host), port);
                        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter output = new PrintWriter(socket.getOutputStream(), true);)
        {
            output.println(username + SPACE_SEPARATOR + message);
            printInput(input.readLine());
        }
        catch (IOException e)
        {
            LOGGER.log(Level.SEVERE, "Failed to start client on given host and port!", e);
            throw new ClientException("Failed to start!");
        }
    }


    private static boolean isValidUsername(String username)
    {
        return (username != null && username.length() >= MIN_LENGTH_USERNAME && username.length() <= MAX_LENGTH_USERNAME && username
                                                                                                                                    .matches(USERNAME_REGEX));
    }


    private void printInput(String line)
    {
        System.out.println(CURSOR + " " + line.replaceAll(MESSAGE_SEPARATOR, LINE_SEPARATOR));
        // new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
    }


    /**
     * Main point in {@link ChatClient}. Input arguments should be in the following order - host, port; or without any arguments.
     *
     * @param args
     * @throws UnknownHostException
     * @throws IOException
     */
    public static void main(String[] args) throws UnknownHostException, IOException
    {
        int port = DEFAULT_PORT;
        String host = DEFAULT_HOST;
        if (args.length == ARGS_NUM)
        {
            port = Integer.parseInt(args[1]);
            host = args[0];
        }
        else if (args.length > ARGS_NUM)
        {
            LOGGER.log(Level.INFO, "Unsuported number of arguments!");
            throw new ClientException("Unsuported number of arguments!Usage: client.jar <host> <port>");
        }
        System.out.println(ENTER_USERNAME_MSG);
        Scanner scan = new Scanner(System.in);
        String username = scan.nextLine();
        while (!isValidUsername(username))
        {
            System.out.println(INVALID_USERNAME_MSG);
            scan = new Scanner(System.in);
            username = scan.nextLine();
        }
        ChatClient chatClient = new ChatClient(username, host, port);
        System.out.println(USER_CONNECTED_MSG);
        while (true)
        {
            System.out.println(CURSOR);
            String line = scan.nextLine();
            if (line.equalsIgnoreCase(EXIT_CMD))
            {
                break;
            }
            chatClient.sendMessage(line);
            chatClient.sendMessage(UPDATE_CMD);
        }
        scan.close();
    }
}
