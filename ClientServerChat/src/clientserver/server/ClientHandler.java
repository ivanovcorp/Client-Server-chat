/*
 * ClientException.java
 *
 * created at 2017-09-20 by Petya Petrova p.petrova <p.petrova@seeburger.com>
 *
 * Copyright (c) SEEBURGER AG, Germany. All Rights Reserved.
 */
package clientserver.server;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import clientserver.dao.DaoException;
import clientserver.dao.db.ChatDaoDB;
import clientserver.dao.db.UserDaoDB;
import clientserver.entities.ChatEntity;
import clientserver.entities.UserEntity;
import clientserver.entities.UserEntity.UserStatus;


/**
 * Handles incoming connection in separate thread from {@link ChatServer}.
 * <p>
 * created at Aug 16, 2017 by @author Petya Petrova p.petrova
 */
public class ClientHandler implements Runnable
{
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final String SPACE_SEPARATOR = " ";
    private static final String MESSAGE_SEPARATOR = "#";
    private static final String UPDATE_CMD = "!update";
    private static final String PRIVATE_MSG_CMD = "@";
    private static final String CURSOR = ">";
    private static final String SENDER = "sender: ";
    private static final String RECIPIENT = "recipient: ";
    private static final String CLIENT_SAYS_MSG = "client: ";
    private static final String NO_SUCH_USER_MSG = "No such user!";
    private static final int MAX_LENGTH_MESSAGE = 300;
    private static final Logger LOGGER = Logger.getLogger(ClientHandler.class.getName());
    private final UserDaoDB userDao;
    private final ChatDaoDB chatDao;
    private final ServerSocket serverSocket;


    /**
     * Creates {@link ClientHandler} by given {@link ServerSocket} server socket. Throws {@link RuntimeException} if server socket is not
     * properly passed.
     *
     * @param serverSocket
     */
    public ClientHandler(ServerSocket serverSocket)
    {
        if (serverSocket == null)
        {
            LOGGER.log(Level.SEVERE, "Server socket hasn't been initialized!");
            throw new RuntimeException("Server did not respond!");
        }
        this.serverSocket = serverSocket;
        userDao = UserDaoDB.getInstance();
        chatDao = ChatDaoDB.getInstance();
    }


    @Override
    public void run()
    {
        try (Socket clientSocket = serverSocket.accept();
                        DataInputStream input = new DataInputStream(clientSocket.getInputStream());
                        BufferedReader bufReader = new BufferedReader(new InputStreamReader(input));
                        PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream(), true);)
        {
            String inputLine;
            while ((inputLine = bufReader.readLine()) != null)
            {
                validateUserInput(inputLine);
                printWriter.println(processInput(inputLine));
            }
        }
        catch (RuntimeException e)
        {
            LOGGER.log(Level.SEVERE, "Invalid input from client", e);
            throw e;
        }
        catch (IOException e)
        {
            LOGGER.log(Level.SEVERE, "Failed to read from client!", e);
            throw new RuntimeException("Internal error!Please, try again later");
        }
    }


    private String processInput(String input)
    {
        String username = input.substring(0, input.indexOf(SPACE_SEPARATOR));
        String message = input.substring(input.indexOf(SPACE_SEPARATOR), input.length());
        message = message.trim();

        System.out.println(CLIENT_SAYS_MSG + username + SPACE_SEPARATOR + CURSOR + SPACE_SEPARATOR + message);
        saveUser(username);

        if (message.equalsIgnoreCase(UPDATE_CMD))
        {
            return sendHistory(username);
        }
        else if (message.startsWith(PRIVATE_MSG_CMD))
        {
            sendPrivateMsg(username, message);
        }
        else
        {
            saveChat(username, message);
        }
        return "";
    }


    private void saveUser(String username)
    {
        if (username == null || username.isEmpty())
        {
            throw new IllegalArgumentException("Username is mandatory! Please, provide valid one!");
        }
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setStatus(UserStatus.ONLINE);
        try
        {
            userDao.userExists(username);
        }
        catch (DaoException e)
        {
            LOGGER.log(Level.WARNING, "User does not exist!", e.getMessage());
            try
            {
                userDao.saveUser(user);
            }
            catch (DaoException e1)
            {
                LOGGER.log(Level.WARNING, "Failed to save user!", e.getMessage());
            }
        }
    }


    private void saveChat(String sender, String message)
    {
        try
        {
            List<String> usernames = userDao.getAllUsers();
            for (String username : usernames)
            {
                if (!username.equalsIgnoreCase(sender))
                {
                    saveChat(sender, username, message);
                }
            }
        }
        catch (DaoException e)
        {
            LOGGER.log(Level.WARNING, "Failed to send messages!", e);
        }
    }


    private void saveChat(String sender, String recipient, String message)
    {
        ChatEntity chatEntity = new ChatEntity();
        chatEntity.setMessage(message);
        chatEntity.setSender(sender);
        chatEntity.setRecipient(recipient);
        try
        {
            chatDao.saveMsg(chatEntity);
        }
        catch (DaoException e)
        {
            LOGGER.log(Level.WARNING, "Failed to save chat!", e);
        }
    }


    private String sendHistory(String username)
    {
        StringBuilder history = new StringBuilder();
        List<ChatEntity> chats = null;
        try
        {
            chats = userDao.getHistory(username);
            for (ChatEntity chat : chats)
            {
                history.append(SENDER + chat.getSender() + SPACE_SEPARATOR + RECIPIENT + chat.getRecipient() + SPACE_SEPARATOR + chat
                                                                                                                                     .getMessage()
                               + MESSAGE_SEPARATOR);
            }
            history.append(LINE_SEPARATOR);
        }
        catch (DaoException e)
        {
            LOGGER.log(Level.WARNING, "Failed to send history to the user!", e);
        }
        return history.toString();
    }


    private void sendPrivateMsg(String sender, String message)
    {
        String recipient = message.substring(1, message.indexOf(SPACE_SEPARATOR));
        String privateMsg = message.substring(message.indexOf(SPACE_SEPARATOR), message.length());
        privateMsg = privateMsg.trim();
        try
        {
            userDao.userExists(recipient);
            saveChat(sender, recipient, privateMsg);
        }
        catch (DaoException e)
        {
            LOGGER.log(Level.WARNING, "User does not exist!", e.getMessage());
            saveChat(sender, recipient, NO_SUCH_USER_MSG);
        }

    }


    private void validateUserInput(String message)
    {
        if (message == null || message.length() >= MAX_LENGTH_MESSAGE)
        {
            throw new RuntimeException("Invalid input from client: " + message);
        }

        if (!message.contains(SPACE_SEPARATOR))
        {
            throw new RuntimeException("Invalid input from client. All messages must start with username and space!" + message);
        }
    }
}
