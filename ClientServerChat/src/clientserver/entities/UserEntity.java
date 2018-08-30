/*
 * ClientException.java
 *
 * created at 2017-09-20 by Petya Petrova p.petrova <p.petrova@seeburger.com>
 *
 * Copyright (c) SEEBURGER AG, Germany. All Rights Reserved.
 */
package clientserver.entities;


/**
 * Represents user entity in the client-server application.
 * <p>
 * created at Sep 11, 2017 by @author Petya Petrova p.petrova
 */
public class UserEntity
{
    private String username;
    private UserStatus status;
    private Integer id;


    public String getUsername()
    {
        return username;
    }


    public void setUsername(String username)
    {
        this.username = username;
    }


    public Integer getId()
    {
        return id;
    }


    public UserStatus getStatus()
    {
        return status;
    }


    public void setStatus(UserStatus status)
    {
        this.status = status;
    }

    /**
     * Represents current status of the user.
     * <p>
     * created at Sep 20, 2017 by @author Petya Petrova p.petrova@seeburger.com p.petrova
     */
    public enum UserStatus
    {
        ONLINE, OFFLINE;
    }
}
