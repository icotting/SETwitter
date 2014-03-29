/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unl.raikes.se.twitter.bean;

import edu.unl.raikes.se.twitter.service.UserService;
import java.security.Principal;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author iancottingham
 */
@Named
/* a new instance of this object will be created for each 
 * new request / response lifecycle
 */
@RequestScoped
public class UserBean {

    @Inject
    private UserService userService;

    private long userId;

    @PostConstruct
    public void init() {
        /* obtain the authenticated user principal from the request */
        Principal principal = ((HttpServletRequest) FacesContext.
                getCurrentInstance().getExternalContext().getRequest())
                .getUserPrincipal();
        if (principal != null) {
            /* using the name value, find the user profile associated with the 
             * authenticated user and set the ID. This id will then be available
             * to other beans to lookup the user profile as need. NOTE: the 
             * user ID is stored as part of the request scope rather than the 
             * profile to ensure that the user profile is loaded by 
             * whichever entity manager is in scope at the time the object is 
             * needed.  This prevents the state of the object from being invalid
             * on an AJAX (or other) request. 
             */
            this.userId = userService.findUserByName(principal.getName()).getId();
        }
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String logoff() {
        try {
            /* remove the authenticated user principal from the request, and 
             * redirect to the home page.
             */
            ((HttpServletRequest) FacesContext.getCurrentInstance()
                    .getExternalContext().getRequest()).logout();
        } catch (ServletException se) {
            throw new RuntimeException(se);
        }
        return "index.xhtml?faces-redirect=true";
    }
}
