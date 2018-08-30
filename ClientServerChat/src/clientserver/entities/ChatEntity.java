/*
 * ClientException.java
 *
 * created at 2017-09-20 by Petya Petrova p.petrova <p.petrova@seeburger.com>
 *
 * Copyright (c) SEEBURGER AG, Germany. All Rights Reserved.
 */
package clientserver.entities;


/**
 * Represents chat entity in the client-server application.
 * <p>
 * created at Sep 11, 2017 by @author Petya Petrova p.petrova
 */
public class ChatEntity
{
    private Integer id;
    private String sender;
    private String recipient;
    private String message;


    public Integer getId()
    {
        return id;
    }


    public String getSender()
    {
        return sender;
    }


    public void setSender(String sender)
    {
        this.sender = sender;
    }


    public String getRecipient()
    {
        return recipient;
    }


    public void setRecipient(String recipient)
    {
        this.recipient = recipient;
    }


    public String getMessage()
    {
        return message;
    }


    public void setMessage(String message)
    {
        this.message = message;
    }
}
