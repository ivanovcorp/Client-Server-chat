/*
 * DaoException.java
 *
 * created at 2017-10-04 by Petya Petrova p.petrova <p.petrova@seeburger.com>
 *
 * Copyright (c) SEEBURGER AG, Germany. All Rights Reserved.
 */
package clientserver.dao;


/**
 * Represents exception which occurs during operations with {@link UserDao} and {@link ChatDao}
 * <p>
 * created at Oct 5, 2017 by @author Petya Petrova p.petrova@seeburger.com p.petrova
 */
@SuppressWarnings("serial")
public class DaoException extends Exception
{
    public DaoException(String message)
    {
        super(message);
    }


    public DaoException(String message, Throwable t)
    {
        super(message, t);
    }
}
