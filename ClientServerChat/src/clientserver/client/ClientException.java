/*
 * ClientException.java
 *
 * created at 2017-09-20 by Petya Petrova p.petrova <p.petrova@seeburger.com>
 *
 * Copyright (c) SEEBURGER AG, Germany. All Rights Reserved.
 */
package clientserver.client;


/**
 * Represents exception which occurs during {@link ChatClient} creation and manipulation.
 * <p>
 * created at Sep 21, 2017 by @author Petya Petrova p.petrova@seeburger.com p.petrova
 */
@SuppressWarnings("serial")
public class ClientException extends RuntimeException
{
    public ClientException(String message)
    {
        super(message);
    }


    public ClientException(String message, Throwable t)
    {
        super(message, t);
    }
}
