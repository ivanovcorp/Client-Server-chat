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
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import clientserver.dao.ChatDao;
import clientserver.dao.DaoException;
import clientserver.database.Database;
import clientserver.entities.ChatEntity;


/**
 * Database DAO layer to ensure DB access and DB transactions for {@link ChatEntity}.
 * <p>
 * created at Sep 18, 2017 by @author Petya Petrova p.petrova
 */
public class ChatDaoDB implements ChatDao
{

    private static final String SAVE_MSG_SQL = "INSERT INTO chronology.chat (sender, recipient, message) VALUES( ?, ?, ?)";
    private static final String DELETE_MSG_SQL = "DELETE FROM chronology.chat WHERE sender=? AND recipient=?";
    private static final Logger LOGGER = Logger.getLogger(ChatDaoDB.class.getName());
    private static ChatDaoDB chatDaoDB;


    private ChatDaoDB()
    {}


    public static synchronized ChatDaoDB getInstance()
    {
        if (chatDaoDB == null)
        {
            chatDaoDB = new ChatDaoDB();
        }
        return chatDaoDB;
    }


    @Override
    public void saveMsg(ChatEntity chat) throws DaoException
    {
        if (chat == null || chat.getSender() == null)
        {
            LOGGER.log(Level.WARNING, "Chat message is not properly defined!");
            throw new DaoException("Message is not properly defined!");
        }

        try (Connection connection = Database.getInstance().getConnection();
                        PreparedStatement prStatement = connection.prepareStatement(SAVE_MSG_SQL);)
        {
            prStatement.setString(1, chat.getSender());
            prStatement.setString(2, chat.getRecipient());
            prStatement.setString(3, chat.getMessage());
            prStatement.execute();
        }
        catch (SQLException e)
        {
            LOGGER.log(Level.WARNING, "Failed to save message in Database!", e);
            throw new DaoException("Failed to save message!");
        }
    }


    @Override
    public void deleteMsg(ChatEntity chat) throws DaoException
    {
        try (Connection connection = Database.getInstance().getConnection();
                        PreparedStatement prStatement = connection.prepareStatement(DELETE_MSG_SQL);)
        {
            prStatement.setString(1, chat.getSender());
            prStatement.setString(2, chat.getRecipient());
            prStatement.execute();
        }
        catch (SQLException e)
        {
            LOGGER.log(Level.WARNING, "Failed to delete message from Database!Caused by: ", e);
            throw new DaoException("Failed to delete message!");
        }
    }
}
