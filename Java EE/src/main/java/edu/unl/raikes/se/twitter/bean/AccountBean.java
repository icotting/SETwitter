/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unl.raikes.se.twitter.bean;

import edu.unl.raikes.se.twitter.entity.UserProfile;
import edu.unl.raikes.se.twitter.service.UserService;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

/**
 *
 * @author iancottingham
 */
@Named
/* a new instance of this object will be created for each 
 * new request / response lifecycle
 */
@RequestScoped
public class AccountBean {

    @Resource
    private UserTransaction tx;

    @Inject
    private UserService userService;

    /* this fields will be bound to view elements */
    private String name;
    private String emailAddress;
    private String userName;
    private String location;
    private String password;
    private String reenterPassword;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getReenterPassword() {
        return reenterPassword;
    }

    public void setReenterPassword(String reenterPassword) {
        this.reenterPassword = reenterPassword;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    /* This bean action will be invoked as a new request. Each of the fields 
     * will be set for the request prior to this method being invoked. 
     */
    public String createAccount() {

        boolean error = false;
        /* if the email address is not valid, add an error message to the 
         * faces context to be rendered as part of the view state
         */
        if (userService.isValidEmail(this.emailAddress) == false) {
            FacesMessage msg
                    = new FacesMessage("Invalid email address",
                            "The address is invalid or has already been used.");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);

            FacesContext.getCurrentInstance().addMessage("newAccount:email", msg);
            error = true;
        }

        /* if the username is not valid, add an error message to the 
         * faces context to be rendered as part of the view state
         */
        if (userService.isValidUserName(this.userName) == false) {
            FacesMessage msg
                    = new FacesMessage("Invalid username",
                            "The username is already in use");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);

            FacesContext.getCurrentInstance().addMessage("newAccount:uname", msg);
            error = true;
        }

        /* if the password is empty or does not match, add an error message to 
         * the faces context to be rendered as part of the view state
         */
        if (this.password == null || this.password.isEmpty()) {
            FacesMessage msg
                    = new FacesMessage("Password is empty",
                            "A password must be provided");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);

            FacesContext.getCurrentInstance().addMessage("newAccount:pass", msg);
            error = true;
        } else if (this.password.equals(this.reenterPassword) == false) {
            FacesMessage msg
                    = new FacesMessage("Passwords do not match",
                            "The provided passwords do not match");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);

            FacesContext.getCurrentInstance().addMessage("newAccount:pass", msg);
            error = true;
        }

        if (!error) {
            /* create a new user profile */
            UserProfile profile = new UserProfile();
            profile.setEmail(this.emailAddress);
            profile.setLocation(this.location);
            profile.setName(this.name);
            profile.setRole("user");
            profile.setUserName(this.userName);

            // SHA encrypt the password
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                digest.update(this.password.getBytes());
                BigInteger hash = new BigInteger(1, digest.digest());
                profile.setPassword(hash.toString(16));
            } catch (NoSuchAlgorithmException nse) {
                throw new RuntimeException(nse);
            }

            try {
                // begin a transaction and use the service to create the user
                tx.begin();
                this.userService.createUserProfile(profile);

                // commit the transaction to write the user to the db
                tx.commit();
            } catch (IllegalStateException | SecurityException |
                    HeuristicMixedException | HeuristicRollbackException |
                    NotSupportedException | RollbackException |
                    SystemException e) {
                throw new RuntimeException(e);
            }

            try {
                /* get the request and use the HttpServletRequest API to log
                 * in as the user that was just created. This will use the 
                 * container provided JAAS realm to authenticate the user 
                 * with the credentials provided for creating the user 
                 */
                ((HttpServletRequest) FacesContext.getCurrentInstance()
                        .getExternalContext().getRequest())
                        .login(this.userName, this.password);

                /* with an authenticated user principal on the request, the
                 * main page can be accessed
                 */
                return "index?redirect=true";
            } catch (ServletException se) {
                throw new RuntimeException(se);
            }

        } else {
            return "Login.xhtml";
        }
    }
}
