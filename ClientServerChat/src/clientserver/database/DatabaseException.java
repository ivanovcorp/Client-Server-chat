/*
 * ClientException.java
 *
 * created at 2017-09-20 by Petya Petrova p.petrova <p.petrova@seeburger.com>
 *
 * Copyright (c) SEEBURGER AG, Germany. All Rights Reserved.
 */
package clientserver.database;


import java.sql.SQLException;


/**
 * Represents exception, which is thrown during transactions with Database.
 * <p>
 * created at Sep 21, 2017 by @author Petya Petrova p.petrova@seeburger.com p.petrova
 */
@SuppressWarnings("serial")
public class DatabaseException extends SQLException
{
    public DatabaseException(String message)
    {
        super(message);
    }


    public DatabaseException(String message, Throwable t)
    {
        super(message, t);
    }
}
