/*
 * ClientException.java
 *
 * created at 2017-09-20 by Petya Petrova p.petrova <p.petrova@seeburger.com>
 *
 * Copyright (c) SEEBURGER AG, Germany. All Rights Reserved.
 */
package clientserver.database;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.tools.Server;


/**
 * Represents database source for the application ClientServer.
 * <p>
 * created at Oct 5, 2017 by @author Petya Petrova p.petrova@seeburger.com p.petrova
 */
public final class Database
{
    private static final String DB_CONNECTION = "jdbc:h2:~/test";
    private static final String DB_PORT = "8082";
    private static final String DB_USER = "admin";
    private static final String PASSWORD = "";
    private static final String START_DB_MSG = "Starting DB at port: ";
    private static final String SETUP_SCHEMA_MSG = "Setting up schema...";
    private static final String CREATE_SCHEMA_SQL = "CREATE SCHEMA IF NOT EXISTS chronology;";
    private static final String CREATE_TABLE_USERS_SQL = "CREATE TABLE IF NOT EXISTS chronology.users (user_id INT UNSIGNED NOT NULL AUTO_INCREMENT, username VARCHAR(50) NOT NULL UNIQUE, status VARCHAR(50), PRIMARY KEY(user_id));";
    private static final String CREATE_TABLES_CHAT_SQL = "CREATE TABLE IF NOT EXISTS chronology.chat (chat_id INT UNSIGNED NOT NULL AUTO_INCREMENT, sender VARCHAR(50), recipient VARCHAR(50), message VARCHAR(100));";
    private static final JdbcConnectionPool CONNECTION_POOL = JdbcConnectionPool.create(DB_CONNECTION, DB_USER, PASSWORD);
    private static final Logger LOGGER = Logger.getLogger(Database.class.getName());
    private static Database database;


    private Database() throws DatabaseException
    {
        startDB();
        setUpSchema();
    }


    /**
     * Gets the only instance of {@link Database}.
     *
     * @return the database instance.
     * @throws DatabaseException in case of failure
     */
    public static synchronized Database getInstance() throws DatabaseException
    {
        if (database == null)
        {
            database = new Database();
        }
        return database;
    }


    /**
     * Creates database schema "chronology".
     *
     * @throws DatabaseException in case of error.
     */
    private void setUpSchema() throws DatabaseException
    {
        LOGGER.log(Level.INFO, SETUP_SCHEMA_MSG);
        try (Connection connection = getConnection();
                        PreparedStatement prStatement = connection.prepareStatement(CREATE_SCHEMA_SQL + CREATE_TABLE_USERS_SQL
                                                                                    + CREATE_TABLES_CHAT_SQL);)
        {
            prStatement.execute();
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Error occured during starting of application!Please, come back later!", e);
        }
    }


    /**
     * Establish connection to Database.
     *
     * @return created {@link Connection}
     * @throws DatabaseException if connection can't be established.
     */
    public final Connection getConnection() throws DatabaseException
    {
        try
        {
            Connection connection = CONNECTION_POOL.getConnection();
            connection.setAutoCommit(true);
            return connection;
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Failed to obtain connection!", e);
        }
    }


    private void startDB() throws DatabaseException
    {
        LOGGER.log(Level.INFO, START_DB_MSG + DB_PORT);
        try
        {
            Server.createWebServer("-webAllowOthers", "-webPort", DB_PORT).start();
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Failed to start Database!", e);
        }
    }
}
