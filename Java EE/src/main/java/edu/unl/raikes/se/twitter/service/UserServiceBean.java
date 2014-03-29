/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unl.raikes.se.twitter.service;

import edu.unl.raikes.se.twitter.entity.UserProfile;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author iancottingham
 */
@Stateless
@Local(UserService.class)
public class UserServiceBean implements UserService {

    @PersistenceContext(unitName = "SETwitter_JavaPU")
    private EntityManager entityManager;

    @Override
    public UserProfile findUserByName(String userName) {
        try {
            /* this logic assumes that there is only one match that 
             * can be found. See consistency checking in creation methods
             */
            return ((UserProfile) entityManager.createQuery("select "
                    + "u from UserProfile as u where u.userName = :uname")
                    .setParameter("uname", userName)
                    .getResultList().get(0));
        } catch (IndexOutOfBoundsException ioe) {
            throw new RuntimeException(String.format("No profile found for "
                    + "user %s", userName));
        }
    }

    @Override
    public UserProfile findUserById(long id) {
        return entityManager.find(UserProfile.class, id);
    }

    @Override
    /* Ensure that a transaction is active before this service method is
     * invoked
     */
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public void createUserProfile(UserProfile profile) {
    	assert profile.getPassword() != null;
    	
        entityManager.persist(profile);
    }

    @Override
    public boolean isValidEmail(String email) {
        
        if ( email == null ) { return false; }
        /* if the provided email causes an address exception, it isn't a
         * valid email address.
         */
        try {
            new InternetAddress(email);
        } catch (AddressException ae) {
            return false;
        }

        /* Make sure the email address isn't in the db already. This would be 
         * a good candidate for a straigt SQL query. If the result list is
         * empty, then the email address has not been used.
         */
        return entityManager.createQuery("select u.email from UserProfile as u where "
                + "u.email = :email").setParameter("email", email)
                .getResultList().isEmpty();
    }

    @Override
    public boolean isValidUserName(String userName) {
        /* Make sure the email address isn't in the db already. This would be 
         * a good candidate for a straigt SQL query. If the result list is
         * empty, then the email address has not been used.
         */
        if ( userName == null || userName.isEmpty() ) { return false; }
        
        return entityManager.createQuery("select u.userName from UserProfile as u where "
                + "u.userName = :uname").setParameter("uname", userName)
                .getResultList().isEmpty();
    }

}
