/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unl.raikes.se.twitter.service;

import edu.unl.raikes.se.twitter.entity.UserProfile;

/**
 *
 * @author iancottingham
 */
public interface UserService {

    /* Returns the user profile matching the argument user name */
    public UserProfile findUserByName(String userName);

    /* Returns the user profile for a given ID */
    public UserProfile findUserById(long id);

    /* Persists a new user profile into the backing data store */
    public void createUserProfile(UserProfile profile);

    /* Determines if the provided email address is valid and 
     * unique in the system (i.e. hasn't already been used)
     */
    public boolean isValidEmail(String email);

    /* Determines if the provided user name is unique in the system */
    public boolean isValidUserName(String userName);

}
