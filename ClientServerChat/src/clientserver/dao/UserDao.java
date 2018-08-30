/*
 * ClientException.java
 *
 * created at 2017-09-20 by Petya Petrova p.petrova <p.petrova@seeburger.com>
 *
 * Copyright (c) SEEBURGER AG, Germany. All Rights Reserved.
 */
package clientserver.dao;


import java.util.List;

import clientserver.entities.ChatEntity;
import clientserver.entities.UserEntity;


/**
 * DAO layer to manage actions with {@link UserEntity}
 * <p>
 * created at Sep 18, 2017 by @author Petya Petrova p.petrova
 */
public interface UserDao
{
    /**
     * Saves given {@link UserEntity} in the Database.
     *
     * @param user of type {@link UserEntity}
     * @throws DaoException in case of failure
     */
    public void saveUser(UserEntity user) throws DaoException;


    /**
     * Deletes given {@link UserEntity} from Database.
     *
     * @param user of type {@link UserEntity}
     * @throws DaoException in case of failure
     */
    public void deleteUser(String username) throws DaoException;


    /**
     * Deletes all {@link UserEntity}s in the Database.
     *
     * @throws DaoException in case of failure
     */
    public void deleteAllUsers() throws DaoException;


    /**
     * Retrieves all {@link UserEntity}s from the Database.
     *
     * @throws DaoException in case of failure
     */
    public List<String> getAllUsers() throws DaoException;


    /**
     * Change the username of the given {@link UserEntity}.
     *
     * @param user is given {@link UserEntity}
     * @param newUsername which will be set
     * @throws DaoException in case of failure.
     */
    public void renameUser(UserEntity user, String newUsername) throws DaoException;


    /**
     * Search for user by given username.
     *
     * @param username of type {@link String}
     * @return true if user exist
     * @throws DaoException in case of failure
     */
    public boolean userExists(String username) throws DaoException;


    /**
     * Gets the messages which are addressed to the given recipient or are coming from the recipient.
     *
     * @param recipient
     * @return
     * @throws DaoException in case of failure
     */
    public List<ChatEntity> getHistory(String recipient) throws DaoException;
}
