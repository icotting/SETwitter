/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unl.raikes.se.twitter.service.test;

import edu.unl.raikes.se.twitter.entity.Feed;
import edu.unl.raikes.se.twitter.entity.Tweet;
import edu.unl.raikes.se.twitter.entity.UserProfile;
import edu.unl.raikes.se.twitter.service.TweetService;
import edu.unl.raikes.se.twitter.service.UserService;
import junit.framework.Assert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.ejb.EJBTransactionRequiredException;

/**
 * @author iancottingham
 */
@RunWith(Arquillian.class)
public class TweetServiceTests {

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap
                .create(WebArchive.class)
                .addPackage(TweetService.class.getPackage())
                .addPackage(Tweet.class.getPackage())
                .addAsResource("test-persistence.xml",
                        "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @PersistenceContext
    EntityManager em;

    @Inject
    UserTransaction utx;

    @EJB
    TweetService service;

    @EJB
    UserService userService;

    @Before
    public void prepTest() throws Exception {
        utx.begin();
        UserProfile profile = new UserProfile();
        profile.setEmail("test@user.com");
        profile.setLocation("Any Town, US");
        profile.setName("Test User");
        profile.setPassword("foobar");
        profile.setRole("user");
        profile.setUserName("testuser");

        ArrayList<Feed> feeds = new ArrayList<>();

        Feed f = new Feed();
        f.setName("Test Feed 1");
        f.setOwner(profile);

        ArrayList<Tweet> tweets = new ArrayList<>();
        Tweet tweet = new Tweet();
        tweet.setBelongsTo(f);
        tweet.setContent("This is a test tweet in feed 1");
        tweet.setPostDate(new Date(System.currentTimeMillis()));

        tweets.add(tweet);

        tweet = new Tweet();
        tweet.setBelongsTo(f);
        tweet.setContent("This is a another test tweet in feed 1");
        tweet.setPostDate(new Date(System.currentTimeMillis()));

        tweets.add(tweet);
        f.setTweets(tweets);

        feeds.add(f);

        f = new Feed();
        f.setName("Test Feed 2");
        f.setOwner(profile);

        tweets = new ArrayList<>();
        tweet = new Tweet();
        tweet.setBelongsTo(f);
        tweet.setContent("One tweet for feed 2");
        tweet.setPostDate(new Date(System.currentTimeMillis()));

        tweets.add(tweet);
        f.setTweets(tweets);
        feeds.add(f);

        f = new Feed();
        f.setName("Test Feed 3");
        f.setOwner(profile);
        feeds.add(f);

        profile.setFeeds(feeds);
        em.persist(profile);

        profile = new UserProfile();
        profile.setEmail("second@user.com");
        profile.setLocation("Second City, US");
        profile.setName("Second User");
        profile.setPassword("foobar");
        profile.setRole("user");
        profile.setUserName("stester");

        f = new Feed();
        f.setName("Subscription test feed");
        f.setOwner(profile);

        tweets = new ArrayList<>();
        tweet = new Tweet();
        tweet.setContent("Tweet to get in a subscription");
        tweet.setPostDate(new Date(System.currentTimeMillis()));
        tweet.setBelongsTo(f);
        tweets.add(tweet);

        tweet = new Tweet();
        tweet.setContent("One more to get in a subscription");
        tweet.setPostDate(new Date(System.currentTimeMillis()));
        tweet.setBelongsTo(f);
        tweets.add(tweet);

        f.setTweets(tweets);

        feeds = new ArrayList<>();
        feeds.add(f);
        profile.setFeeds(feeds);

        em.persist(profile);
        em.flush();

        utx.commit();
    }

    @After
    public void cleanUpFromTest() throws Exception {
        utx.begin();
        List<UserProfile> users = em.createQuery("select u from UserProfile as u").getResultList();
        for (UserProfile u : users) {
            em.remove(u);
        }
        em.flush();
        utx.commit();
    }

    @Test
    public void testFeedCreation() throws Exception {
        UserProfile user = (UserProfile) em.createQuery("select u from UserProfile as u").getResultList().get(0);

        Feed feed = new Feed();
        feed.setName("Test Feed");

        utx.begin();
        service.addFeedForUser(user.getId(), feed);
        utx.commit();

        List<Feed> feeds = em.createQuery("select f from Feed as f").getResultList();
        Assert.assertEquals(5, feeds.size());
        Assert.assertEquals(user, feeds.get(feeds.size() - 1).getOwner());
    }

    @Test
    public void testNoOwnerFeedCreation() throws Exception {
        Feed f = new Feed();
        f.setName("No Owner");
        utx.begin();
        try {
            service.addFeedForUser(-1, f);
            Assert.fail("Allowed a feed to be created for an invalid user");
        } catch (Throwable t) {
        }
        utx.rollback();
    }

    @Test
    public void testNoNameFeedCreation() throws Exception {
        Feed f = new Feed();
        UserProfile user = (UserProfile) em.createQuery("select u from UserProfile as u").getResultList().get(0);

        utx.begin();
        f.setName(null);
        try {
            service.addFeedForUser(user.getId(), f);
            Assert.fail("Allowed a feed to be created with a null name");
        } catch (Throwable t) {
        }
        utx.rollback();

        utx.begin();
        f.setName("");
        try {
            service.addFeedForUser(user.getId(), f);
            Assert.fail("Allowed a feed to be created with an empty name");
        } catch (Throwable t) {
        }
        utx.rollback();
    }

    @Test
    public void testPostNoTransaction() throws Exception {
        try {
            List<Feed> feeds = em.createQuery("select f from Feed as f").getResultList();
            Tweet t = new Tweet();
            t.setContent("No tweet");
            t.setPostDate(new Date(System.currentTimeMillis()));
            service.postTweet(feeds.get(0).getId(), t);
            Assert.fail("Allowed tweet without a transaction");
        } catch (EJBTransactionRequiredException e) {

        }
    }

    @Test
    public void testPost() throws Exception {
        List<Feed> feeds = em.createQuery("select f from Feed as f").getResultList();

        Collections.sort(feeds, new Comparator<Feed>() {

            @Override
            public int compare(Feed o1, Feed o2) {
                return o1.getName().compareTo(o2.getName());
            }

        });

        Assert.assertEquals(4, feeds.size());

        List<Tweet> tweets = em.createQuery("select t from Tweet as t").getResultList();
        Assert.assertEquals(5, tweets.size());

        Feed test = feeds.get(1);
        Assert.assertEquals("Test Feed 1", test.getName());
        Tweet t = new Tweet();
        t.setContent("This is a tweet from a test case");

        utx.begin();
        service.postTweet(test.getId(), t);
        utx.commit();

        List<Tweet> feedTweets = em.createQuery("select t from Tweet as t "
                + "where t.belongsTo = :feed").setParameter("feed",
                        test).getResultList();

        Assert.assertEquals(test.getTweets().size() + 1, feedTweets.size());
        Assert.assertEquals("This is a tweet from a test case",
                feedTweets.get(feedTweets.size() - 1).getContent());

        t = new Tweet();
        t.setContent("A second post from a test case.");
        utx.begin();
        service.postTweet(feeds.get(2).getId(), t);
        utx.commit();

        test = feeds.get(2);
        feedTweets = em.createQuery("select t from Tweet as t "
                + "where t.belongsTo = :feed").setParameter("feed",
                        test).getResultList();
        Assert.assertEquals(test.getTweets().size() + 1, feedTweets.size());
    }

    @Test
    public void testSearchTweets() {
        Collection<Tweet> results = service.searchTweets("tweet");
        Assert.assertEquals(4, results.size());

        results = service.searchTweets("feed 2");
        Assert.assertEquals(1, results.size());

        results = service.searchTweets(null);
        Assert.assertEquals(0, results.size());

        results = service.searchTweets("");
        Assert.assertEquals(0, results.size());
    }

    @Test
    public void testUserTweets() {
        UserProfile profile = userService.findUserByName("testuser");
        Collection<Tweet> tweets = service.getUserTweets(profile.getId());
        Assert.assertEquals(3, tweets.size());

    }

    @Test
    public void testInvalidUserTweets() {
        try {
            service.getUserTweets(-1);
            Assert.fail("Allowed tweet search for invalid user ID");
        } catch (Throwable t) {
        }
    }

    @Test
    public void testFeedTweets() {
        UserProfile profile = userService.findUserByName("testuser");
        Feed test = profile.getFeeds().iterator().next();
        Collection<Tweet> tweets = service.getFeedTweets(test.getId());

        Assert.assertEquals(2, tweets.size());
        Assert.assertEquals("This is a test tweet in feed 1", tweets.iterator().next().getContent());
        Assert.assertEquals(test, tweets.iterator().next().getBelongsTo());
    }

    @Test
    public void testInvalidFeedTweets() {
        try {
            service.getFeedTweets(-1);
            Assert.fail("Allowed tweet search for invalid feed ID");
        } catch (Throwable t) {
        }
    }

    @Test
    public void testSubscriptions() {
        UserProfile profile = userService.findUserByName("testuser");
        Collection<Feed> feeds = service.getUserSubscriptionOptions(profile.getId());
        Assert.assertEquals(1, feeds.size());

        profile = userService.findUserByName("stester");
        feeds = service.getUserSubscriptionOptions(profile.getId());
        Assert.assertEquals(3, feeds.size());
    }

    @Test
    public void testInvalidSubscriptionCheck() {
        try {
            Collection<Feed> feeds = service.getUserSubscriptionOptions(-1);
        } catch (Throwable t) {
        }
    }

    @Test
    public void testSubscription() throws Exception {
        UserProfile profile = userService.findUserByName("testuser");
        Collection<Feed> feeds = service.getUserSubscriptionOptions(profile.getId());

        utx.begin();
        service.subscribeToFeed(profile.getId(),
                feeds.iterator().next().getName(),
                feeds.iterator().next().getOwner().getId());
        utx.commit();

        Collection<Tweet> tweets = service.getSubscriptionTweets(profile.getId());
        Assert.assertEquals(2, tweets.size());
        Assert.assertEquals("Tweet to get in a subscription",
                tweets.iterator().next().getContent());
    }

    @Test
    public void testSubscribeNoTransaction() throws Exception {
        UserProfile profile = userService.findUserByName("stester");
        Collection<Feed> feeds = service.getUserSubscriptionOptions(profile.getId());

        try {
            service.subscribeToFeed(profile.getId(),
                    feeds.iterator().next().getName(),
                    feeds.iterator().next().getOwner().getId());
            Assert.fail("Allowed subscription without a transaction");
        } catch (EJBTransactionRequiredException e) {

        }
    }
}
