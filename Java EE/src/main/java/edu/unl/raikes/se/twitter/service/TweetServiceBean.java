/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unl.raikes.se.twitter.service;

import edu.unl.raikes.se.twitter.entity.Feed;
import edu.unl.raikes.se.twitter.entity.Tweet;
import edu.unl.raikes.se.twitter.entity.UserProfile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author iancottingham
 */
@Stateless
@Local(TweetService.class)
public class TweetServiceBean implements TweetService {

    @PersistenceContext(unitName = "SETwitter_JavaPU")
    private EntityManager entityManager;

    @Override
    public Collection<Feed> getUserSubscriptionOptions(long userId) {

        /* use the entity manager to create and execute a query for all
         * non-user owned feeds that have not already been subscribed to
         */
        UserProfile user = entityManager.find(UserProfile.class, userId);
        assert user != null;

        return entityManager.createQuery("select f from "
                + "Feed as f where f.owner.id != :id and :user not member of "
                + "f.subscribers").setParameter("id",
                        userId).setParameter("user", user).getResultList();
    }

    @Override
    /* Ensure that a transaction is active before this service method is
     * invoked
     */
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public void addFeedForUser(long userId, Feed feed) {
        entityManager.joinTransaction();

        assert feed.getName() != null;
        assert feed.getName().isEmpty() == false;
        
        UserProfile user = entityManager.find(UserProfile.class, userId);
        assert user != null;

        feed.setOwner(user);

        if (user.getFeeds() == null) {
            user.setFeeds(new ArrayList<Feed>());
        }
        user.getFeeds().add(feed);

        /* because the relationship cascades on ALL, merge will cause 
         * feed to be persisted
         */
        this.entityManager.merge(user); // save the updated state of the user
        this.entityManager.flush(); // synchronize the entity manager and db state
    }

    @Override
    /* Ensure that a transaction is active before this service method is
     * invoked
     */
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public void subscribeToFeed(long userId, String feedName, long ownerId) {
        entityManager.joinTransaction();

        UserProfile owner = entityManager.find(UserProfile.class, ownerId);
        assert owner != null;

        // find all feeds that match the owner and feed name
        List<Feed> matches = entityManager.createQuery("select f from Feed as "
                + "f where f.name = :query and f.owner = :owner").setParameter("query",
                        feedName).setParameter("owner", owner).getResultList();

        // if one or more feeds are found, add the first to the user subscriptions
        if (matches.size() > 0) {
            UserProfile user = entityManager.find(UserProfile.class, userId);

            if (user.getSubscriptions() == null) {
                user.setSubscriptions(new ArrayList<Feed>());
            }

            /* if there is more than one match, that is an error condidtion 
             * that should properly be checked when creating a feed
             */
            user.getSubscriptions().add(matches.get(0));
            entityManager.merge(user); // save the updated state of the user
            entityManager.flush(); // synchronize the entity manager and db state
        }
    }

    @Override
    public Collection<Tweet> getUserTweets(long userId) {
        ArrayList<Tweet> tweets = new ArrayList<>();
        UserProfile user = entityManager.find(UserProfile.class, userId);
        assert user != null;

        for (Feed f : user.getFeeds()) {
            tweets.addAll(f.getTweets());
        }

        return tweets;
    }

    @Override
    public Collection<Tweet> getFeedTweets(long feedId) {
        return entityManager.find(Feed.class, feedId).getTweets();
    }

    @Override
    public Collection<Tweet> getSubscriptionTweets(long userId) {
        ArrayList<Tweet> tweets = new ArrayList<>();
        UserProfile user = entityManager.find(UserProfile.class, userId);
        assert user != null;

        for (Feed f : user.getSubscriptions()) {
            tweets.addAll(f.getTweets());
        }

        return tweets;
    }

    @Override
    public Collection<Tweet> searchTweets(String query) {

        if ( query != null && query.isEmpty()) { 
            return new ArrayList<>();
        }
        /* query the db using the entity manager to match any tweet whose
         * content contains the wildcard query string
         */
        return entityManager.createQuery("select t from Tweet "
                + "as t where t.content like :query").setParameter("query",
                        String.format("%%%s%%", query)).getResultList();
    }

    @Override
    /* Ensure that a transaction is active before this service method is
     * invoked
     */
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public void postTweet(long feedId, Tweet tweet) {
        entityManager.joinTransaction();

        Feed feed = entityManager.find(Feed.class, feedId);
        assert feed != null;

        if (feed == null) {
            throw new RuntimeException(String.format("Feed %d is an "
                    + "invalid id", feedId));
        } else {
            tweet.setBelongsTo(feed);

            if (feed.getTweets() == null) {
                feed.setTweets(new ArrayList<Tweet>());
            }
            feed.getTweets().add(tweet);

            /* because the relationship cascades on ALL, merge will cause 
             * tweet to be persisted
             */
            entityManager.merge(feed); // save the updated state of the feed
            entityManager.flush();
        }
    }
}
