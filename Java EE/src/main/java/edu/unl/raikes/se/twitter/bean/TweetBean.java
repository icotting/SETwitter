/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unl.raikes.se.twitter.bean;

import edu.unl.raikes.se.twitter.entity.Tweet;
import edu.unl.raikes.se.twitter.service.TweetService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
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
public class TweetBean {

    @Resource
    private UserTransaction tx;

    @Inject
    private UserBean userBean;

    @Inject
    private FeedBean feedBean;

    @Inject
    private TweetService tweetService;

    private long tweetToFeed;
    private String tweetContent;
    private String searchQuery;
    private Collection<Tweet> searchResults;

    public long getTweetToFeed() {
        return tweetToFeed;
    }

    public void setTweetToFeed(long tweetToFeed) {
        this.tweetToFeed = tweetToFeed;
    }

    public String getTweetContent() {
        return tweetContent;
    }

    public void setTweetContent(String tweetContent) {
        this.tweetContent = tweetContent;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public Collection<Tweet> getTweets() {
        // if the request has a selected feed, get that ID
        long selectedFeed = this.feedBean.getSelectedFeed();

        ArrayList<Tweet> ret = new ArrayList<>();

        if (this.searchResults != null) {
            /* if the request is part of a search result query, return search 
             * result tweets
             */
            ret.addAll(this.searchResults);
        } else if (selectedFeed != -1) {
            /* if there is a selected feed, return only tweets for that feed */
            ret.addAll(this.tweetService.getFeedTweets(selectedFeed));
        } else {
            /* otherwise return all of the current user's tweets as well as 
             * and tweet contained in any feed to which the user is subscribed
             */
            ret.addAll(tweetService.getUserTweets(this.userBean.getUserId()));
            ret.addAll(tweetService.getSubscriptionTweets(this.userBean.getUserId()));
        }

        /* sort the list of tweets by date, note the use of a anonymous inner
         * class to do the comparison, this allows me to easily sort by other
         * properties 
         */
        Collections.sort(ret, new Comparator<Tweet>() {

            @Override
            public int compare(Tweet o1, Tweet o2) {
                return o2.getPostDate().compareTo(o1.getPostDate());
            }

        });
        return ret;
    }

    public void postTweet() {
        try {
            // begin a transaction and use the service to create the feed
            tx.begin();

            /* create a new tweet and set values passed in the request */
            Tweet t = new Tweet();
            t.setContent(this.tweetContent);
            t.setPostDate(new Date(System.currentTimeMillis()));

            /* use the TweetService to persist the new tweet and associate it
             * with the appropriate feed
             */
            tweetService.postTweet(this.tweetToFeed, t);

            // commit the transaction to persist to the backing data store
            tx.commit();

            /* all state variables should be cleared for when the view state is 
             * rendered for the response
             */
            this.tweetContent = null;

        } catch (NotSupportedException | SystemException | RollbackException |
                HeuristicMixedException | HeuristicRollbackException |
                SecurityException | IllegalStateException e) {
            throw new RuntimeException("could not post the tweet", e);
        }
    }

    public String search() {
        /* use the TweetService to set the search result state and rerender 
         * the view with that state set
         */
        this.searchResults = tweetService.searchTweets(this.searchQuery);
        return "";
    }
}
