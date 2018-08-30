/*
 * ClientException.java
 *
 * created at 2017-09-20 by Petya Petrova p.petrova <p.petrova@seeburger.com>
 *
 * Copyright (c) SEEBURGER AG, Germany. All Rights Reserved.
 */
package clientserver.dao.db;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import clientserver.dao.DaoException;
import clientserver.dao.UserDao;
import clientserver.database.Database;
import clientserver.entities.ChatEntity;
import clientserver.entities.UserEntity;


/**
 * Database DAO layer for actions with {@link UserEntity}.
 * <p>
 * created at Sep 18, 2017 by @author Petya Petrova p.petrova
 */
public class UserDaoDB implements UserDao
{

    private static final String SAVE_USER_SQL = "INSERT INTO chronology.users (username, status) VALUES(?,?)";
    private static final String DELETE_USER_SQL = "DELETE FROM chronology.users WHERE username=?";
    private static final String DELETE_ALL_USERS_SQL = "DELETE FROM chronology.users";
    private static final String SHOW_ALL_USERS_SQL = "SELECT username FROM chronology.users";
    private static final String RENAME_USER_SQL = "UPDATE chronology.users SET username=? WHERE username=?";
    private static final String FIND_USER_SQL = "SELECT username FROM chronology.users WHERE username=?";
    private static final String GET_HISTORY_SQL = "SELECT sender,message,recipient FROM chronology.chat WHERE RECIPIENT=? OR SENDER=?";
    private static final String SENDER_COLUMN = "SENDER";
    private static final String MESSAGE_COLUMN = "MESSAGE";
    private static final String RECIPIENT_COLUMN = "RECIPIENT";
    private static final String USERNAME_COLUMN = "USERNAME";
    private static final Logger LOGGER = Logger.getLogger(UserDaoDB.class.getName());
    private static UserDaoDB userDaoDB;


    private UserDaoDB()
    {}


    public static synchronized UserDaoDB getInstance()
    {
        if (userDaoDB == null)
        {
            userDaoDB = new UserDaoDB();
        }
        return userDaoDB;
    }


    @Override
    public void saveUser(UserEntity user) throws DaoException
    {
        if (user == null || user.getUsername() == null || user.getStatus() == null)
        {
            LOGGER.log(Level.WARNING, "User or username are not specified!");
            throw new DaoException("Username is not specified!");
        }
        try (Connection connection = Database.getInstance().getConnection();
                        PreparedStatement prStatement = connection.prepareStatement(SAVE_USER_SQL);)
        {
            prStatement.setString(1, user.getUsername());
            prStatement.setString(2, user.getStatus().toString());
            prStatement.execute();
        }
        catch (SQLException e)
        {
            LOGGER.log(Level.WARNING, "Failed to save user in the Database!", e);
            throw new DaoException("Failed to save user!");
        }
    }


    @Override
    public void deleteUser(String username) throws DaoException
    {
        if (username == null)
        {
            LOGGER.log(Level.WARNING, "User or username are not properly defined!");
            throw new DaoException("Username is not specified!");
        }
        try (Connection connection = Database.getInstance().getConnection();
                        PreparedStatement prStatement = connection.prepareStatement(DELETE_USER_SQL);)
        {
            prStatement.setString(1, username);
            prStatement.execute();
        }
        catch (SQLException e)
        {
            LOGGER.log(Level.WARNING, "Failed to delete user from Database!", e);
            throw new DaoException("Failed to delete user!");
        }
    }


    @Override
    public void deleteAllUsers() throws DaoException
    {
        try (Connection connection = Database.getInstance().getConnection();
                        PreparedStatement prStatement = connection.prepareStatement(DELETE_ALL_USERS_SQL))
        {
            prStatement.execute();
        }
        catch (SQLException e)
        {
            LOGGER.log(Level.WARNING, "Failed to delete all users in Database!", e);
            throw new DaoException("Failed to delete all users!");
        }
    }


    @Override
    public List<String> getAllUsers() throws DaoException
    {
        ResultSet resultSet = null;
        List<String> usernames = new ArrayList<String>();
        try (Connection connection = Database.getInstance().getConnection();
                        PreparedStatement prStatement = connection.prepareStatement(SHOW_ALL_USERS_SQL))
        {
            resultSet = prStatement.executeQuery();
            while (resultSet.next())
            {
                usernames.add(resultSet.getString(USERNAME_COLUMN));
            }
        }
        catch (SQLException e)
        {
            LOGGER.log(Level.WARNING, "Failed to show all users in Database!", e);
            throw new DaoException("Failed to show stored users!", e);
        }
        return usernames;
    }


    @Override
    public void renameUser(UserEntity user, String newUsername) throws DaoException
    {
        if (user == null || user.getUsername() == null || user.getUsername().isEmpty())
        {
            LOGGER.log(Level.WARNING, "Old username or user are not provided!");
            throw new DaoException("Old username or user are not specified!");
        }
        if (newUsername == null || newUsername.isEmpty())
        {
            LOGGER.log(Level.WARNING, "New username is not provided!");
            throw new DaoException("New username is not provided!");
        }
        try (Connection connection = Database.getInstance().getConnection();
                        PreparedStatement prStatement = connection.prepareStatement(RENAME_USER_SQL))
        {
            prStatement.setString(1, user.getUsername());
            prStatement.setString(2, newUsername);
            prStatement.execute();
        }
        catch (SQLException e)
        {
            LOGGER.log(Level.WARNING, "Error occured during user rename operation!", e);
            throw new DaoException("Failed to rename selected user!");
        }
    }


    @Override
    public boolean userExists(String username) throws DaoException
    {
        if (username == null || username.isEmpty())
        {
            LOGGER.log(Level.INFO, "Username of user is not given!");
            return false;
        }
        ResultSet resultSet = null;
        try (Connection connection = Database.getInstance().getConnection();
                        PreparedStatement prStatement = connection.prepareStatement(FIND_USER_SQL))
        {
            prStatement.setString(1, username);
            resultSet = prStatement.executeQuery();
            resultSet.next();
            return username.equals(resultSet.getString(USERNAME_COLUMN));
        }
        catch (SQLException e)
        {
            LOGGER.log(Level.WARNING, "Failed to find user!", e.getMessage());
            throw new DaoException("Failed to find user!");
        }
        finally
        {
            if (resultSet != null)
            {
                try
                {
                    resultSet.close();
                }
                catch (SQLException e)
                {
                    LOGGER.log(Level.INFO, "Resource result set is not closed!");
                }
            }
        }
    }


    @Override
    public List<ChatEntity> getHistory(String recipient) throws DaoException
    {
        if (recipient == null)
        {
            LOGGER.log(Level.INFO, "Recipient can not be null!");
            return Collections.emptyList();
        }
        List<ChatEntity> chats = new ArrayList<ChatEntity>();
        ResultSet resultSet = null;
        try (Connection connection = Database.getInstance().getConnection();
                        PreparedStatement prStatement = connection.prepareStatement(GET_HISTORY_SQL);)
        {
            prStatement.setString(1, recipient);
            prStatement.setString(2, recipient);
            resultSet = prStatement.executeQuery();
            while (resultSet.next())
            {
                ChatEntity chatEntity = new ChatEntity();
                chatEntity.setSender(resultSet.getString(SENDER_COLUMN));
                chatEntity.setMessage(resultSet.getString(MESSAGE_COLUMN));
                chatEntity.setRecipient(resultSet.getString(RECIPIENT_COLUMN));
                chats.add(chatEntity);
            }
            return chats;
        }
        catch (SQLException e)
        {
            LOGGER.log(Level.WARNING, "Failed to load history for given user!", e);
            throw new DaoException("Failed to load required history!");
        }
        finally
        {
            if (resultSet != null)
            {
                try
                {
                    resultSet.close();
                }
                catch (SQLException e)
                {
                    LOGGER.log(Level.INFO, "Resource result set is not closed!", e.getCause());
                }
            }
        }
    }
}
