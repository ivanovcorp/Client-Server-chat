/*
 * ClientException.java
 *
 * created at 2017-09-20 by Petya Petrova p.petrova <p.petrova@seeburger.com>
 *
 * Copyright (c) SEEBURGER AG, Germany. All Rights Reserved.
 */
package clientserver.dao;


import clientserver.entities.ChatEntity;


/**
 * DAO layer to manage message functionalities of {@link ChatEntity}.
 * <p>
 * created at Sep 18, 2017 by @author Petya Petrova p.petrova
 */
public interface ChatDao
{
    /**
     * Saves message in Database.
     *
     * @param chat is given {@link ChatEntity} to save.
     * @throws DaoException in case of failure.
     */
    public void saveMsg(ChatEntity chat) throws DaoException;


    /**
     * Delete message from Database.
     *
     * @param chat is given {@link ChatEntity}
     * @throws DaoException in case of failure.
     */
    public void deleteMsg(ChatEntity chat) throws DaoException;
}
