/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unl.raikes.se.twitter.bean;

import edu.unl.raikes.se.twitter.entity.Feed;
import edu.unl.raikes.se.twitter.entity.UserProfile;
import edu.unl.raikes.se.twitter.service.UserService;
import edu.unl.raikes.se.twitter.service.TweetService;
import java.io.Serializable;
import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
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
public class FeedBean implements Serializable {

    @Resource
    private UserTransaction tx;

    @Inject
    private UserService userService;

    @Inject
    private TweetService tweetService;

    @Inject
    private UserBean userBean;

    private long selectedFeed;
    private String newFeedName;
    private String subscribeTo;

    @PostConstruct
    public void init() {
        HttpServletRequest req = (HttpServletRequest) FacesContext
                .getCurrentInstance().getExternalContext().getRequest();

        /* if the request includes a GET parameter for feed, the state of 
         * the feed bean should include that ID as the currently selected 
         * feed
         */
        if (req.getParameter("feed") == null) {
            selectedFeed = -1;
        } else {
            selectedFeed = Long.parseLong(req.getParameter("feed"));
        }
    }

    public long getSelectedFeed() {
        return selectedFeed;
    }

    public void setSelectedFeed(long selectedFeed) {
        this.selectedFeed = selectedFeed;
    }

    public String getNewFeedName() {
        return newFeedName;
    }

    public void setNewFeedName(String newFeedName) {
        this.newFeedName = newFeedName;
    }

    public Collection<Feed> getFeeds() {
        /* using the UserService, load the profile of the user associated
         * with the request and retun the feeds collection
         */
        return this.userService.findUserById(userBean.getUserId())
                .getFeeds();
    }

    public String getSubscribeTo() {
        return subscribeTo;
    }

    public void setSubscribeTo(String subscribeTo) {
        this.subscribeTo = subscribeTo;
    }

    public Collection<Feed> getSubscriptions() {
        /* using the UserService, load the profile of the user associated
         * with the request and retun the subscriptions collection
         */
        return this.userService.findUserById(userBean.getUserId())
                .getSubscriptions();
    }

    public String getSubscriptionString() {
        /* get the possible subscription feeds for this user from the 
         * tweet service
         */
        Collection<Feed> feeds = tweetService
                .getUserSubscriptionOptions(this.userBean.getUserId());

        /* build a json string as the local datasource for the typeahead.js
         * input that will be rendered in the view. See:
         * http://twitter.github.io/typeahead.js/
         */
        StringBuilder sb = new StringBuilder("[");
        for (Feed f : feeds) {
            /* always use string.format for efficient concatenation of strings! */
            sb.append(String.format("{value: \"%s by @%s\", tokens: [\"%s\",\"%s\",\"%s\"]},", f.getName(),
                    f.getOwner().getUserName(),
                    f.getOwner().getName().split(" ")[0],
                    f.getOwner().getName().split(" ")[1],
                    f.getName()));
        }
        /* replace the last , with the closing JSON bracket */
        sb.replace(sb.length() - 1, sb.length(), "]");
        return sb.toString();
    }

    public void addFeed() {
        try {
            // begin a transaction and use the service to create the feed
            tx.begin();
            Feed feed = new Feed();
            feed.setName(this.newFeedName);

            /* persist the new feed to the db and ensure that the proper
             * relationships with the user are set
             */
            this.tweetService.addFeedForUser(this.userBean.getUserId(), feed);

            // commit the transaction to persist to the backing data store
            tx.commit();

            /* all state variables should be cleared for when the view state is 
             * rendered for the response
             */
            this.newFeedName = null;

        } catch (NotSupportedException | SystemException | RollbackException |
                HeuristicMixedException | HeuristicRollbackException |
                SecurityException | IllegalStateException e) {
            throw new RuntimeException("could not create the feed", e);
        }
    }

    public void subscribeToFeed() {

        try {
            // begin a transaction and use the service to create the feed
            tx.begin();
            
            // split up the value string to get the feed name
            String[] parts = this.subscribeTo.split("by");
            
            // get the user profile that owns the feed from the twitter username in the value string
            UserProfile owner = userService.findUserByName(parts[1].trim().substring(1));
            assert owner != null; // ensure that there is a matching owner

            /* create a new relationship between the feed and the authenticated user */
            this.tweetService.subscribeToFeed(this.userBean.getUserId(),
                    parts[0].trim(), owner.getId());

            // commit the transaction to persist to the backing data store
            tx.commit();
        } catch (NotSupportedException | SystemException | RollbackException |
                HeuristicMixedException | HeuristicRollbackException |
                SecurityException | IllegalStateException e) {
            throw new RuntimeException(e);
        }

        /* all state variables should be cleared for when the view state is 
         * rendered for the response
         */
        this.subscribeTo = null;
    }
}
