/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.unl.raikes.se.twitter.service;

import edu.unl.raikes.se.twitter.entity.Feed;
import edu.unl.raikes.se.twitter.entity.Tweet;

import java.util.Collection;

/**
 * @author iancottingham
 */
public interface TweetService {

    /* Return a collection of feeds that a user could subscribe to. This 
     * method returns all feeds not owned or subscribed to by the user
     */
    public Collection<Feed> getUserSubscriptionOptions(long userId);

    /* Add a the argument feed to the users feed list */
    public void addFeedForUser(long userId, Feed feed);

    /* Subscribe a user to a specific feed */
    public void subscribeToFeed(long userId, String feedName, long ownerId);

    /* Returns a collection of all tweets from all feeds owned by 
     * the argument user
     */
    public Collection<Tweet> getUserTweets(long userId);

    /* Returns a collection of all tweets from the argument feed */
    public Collection<Tweet> getFeedTweets(long feedId);

    /* Returns a collection of all tweets from all feeds subscribed to by
     * the argument user
     */
    public Collection<Tweet> getSubscriptionTweets(long userId);

    /* Returns a collection of all tweets whose content matches the 
     * argument query
     */
    public Collection<Tweet> searchTweets(String query);

    /* Add the argument tweet to teh argument feed */
    public void postTweet(long feedId, Tweet tweet);

}
