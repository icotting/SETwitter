/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unl.raikes.se.twitter.service.test;

import static org.junit.Assert.fail;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.EJBTransactionRequiredException;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.RollbackException;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import edu.unl.raikes.se.twitter.entity.UserProfile;
import edu.unl.raikes.se.twitter.service.UserService;
import edu.unl.raikes.se.twitter.service.UserServiceBean;

/**
 *
 * @author iancottingham
 */
@RunWith(Arquillian.class)
public class UserServiceTests {

    private static long PROFILE_ID = -1;

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap
                .create(WebArchive.class)
                .addClass(UserServiceBean.class)
                .addClass(UserService.class)
                .addPackage(UserProfile.class.getPackage())
                .addAsResource("test-persistence.xml",
                        "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @PersistenceContext
    EntityManager em;

    @Inject
    UserTransaction utx;

    @EJB
    UserService service;

    @Test
    public void testUserCreation() throws Exception {
        utx.begin();
        UserProfile profile = new UserProfile();
        profile.setEmail("test@user.com");
        profile.setLocation("Any Town, US");
        profile.setName("Test User");
        profile.setPassword("foobar");
        profile.setRole("user");
        profile.setUserName("testuser");

        service.createUserProfile(profile);
        utx.commit();

        List<UserProfile> results = em
                .createQuery(
                        "select u from UserProfile as u "
                        + "where u.userName = :uname")
                .setParameter("uname", profile.getUserName()).getResultList();

        Assert.assertEquals(1, results.size());
        UserProfile test = results.get(0);
        PROFILE_ID = test.getId();

        Assert.assertEquals(profile.getUserName(), test.getUserName());
        Assert.assertEquals(profile.getEmail(), test.getEmail());
        Assert.assertEquals(profile.getLocation(), test.getLocation());
        Assert.assertEquals(profile.getRole(), test.getRole());
        Assert.assertEquals(profile.getPassword(), test.getPassword());

        utx.begin();
        profile = new UserProfile();
        profile.setEmail("bill@microsoft.com");
        profile.setLocation("Seattle, WA");
        profile.setName("Bill Gates");
        profile.setPassword("windows");
        profile.setRole("user");
        profile.setUserName("billgates");
        service.createUserProfile(profile);

        profile = new UserProfile();
        profile.setEmail("steve@apple.com");
        profile.setLocation("Palo Alto, CA");
        profile.setName("Steve Jobs");
        profile.setPassword("mac");
        profile.setRole("user");
        profile.setUserName("stevejobs");
        service.createUserProfile(profile);

        profile = new UserProfile();
        profile.setEmail("bill@sun.com");
        profile.setLocation("San Francisco, CA");
        profile.setName("Bill Joy");
        profile.setPassword("cpm");
        profile.setRole("user");
        profile.setUserName("billjoy");
        service.createUserProfile(profile);

        profile = new UserProfile();
        profile.setEmail("woz@apple.com");
        profile.setLocation("Cupertino, CA");
        profile.setName("Steve Wozniak");
        profile.setPassword("iigs");
        profile.setRole("user");
        profile.setUserName("woz");
        service.createUserProfile(profile);
        utx.commit();

        Assert.assertEquals(5, em.createQuery("select u from UserProfile as u")
                .getResultList().size());
    }

    @Test
    public void testTransactionRequirement() throws Exception {
        try {
            UserProfile profile = new UserProfile();
            profile.setEmail("test@user.com");
            profile.setLocation("Any Town, US");
            profile.setName("Test User");
            profile.setPassword("foobar");
            profile.setRole("user");
            profile.setUserName("testuser");
            service.createUserProfile(profile);
            fail("Allowed a profile to be created without a transaction");
        } catch (EJBTransactionRequiredException te) {
        }
    }

    @Test
    public void testDuplicateEmail() throws Exception {
        try {
            utx.begin();
            UserProfile profile = new UserProfile();
            profile.setEmail("bill@microsoft.com");
            profile.setLocation("New York, NY");
            profile.setName("Fake Bill Gates");
            profile.setPassword("dos");
            profile.setRole("user");
            profile.setUserName("fakebillgates");
            service.createUserProfile(profile);
            utx.commit();
            Assert.fail("The service allowed a duplicate email");
        } catch (RollbackException re) {

        }
    }

    @Test
    public void testDuplicateUserName() throws Exception {
        try {
            utx.begin();
            UserProfile profile = new UserProfile();
            profile.setEmail("fakestevejobs@gmail.com");
            profile.setLocation("Sunny Vale, CA");
            profile.setName("Fake Steve Jobs");
            profile.setPassword("apple");
            profile.setRole("user");
            profile.setUserName("stevejobs");
            service.createUserProfile(profile);
            utx.commit();
            Assert.fail("The service allowed a duplicate username");
        } catch (RollbackException re) {

        }
    }

    @Test
    public void testNullPasswordUserCreation() throws Exception {
        utx.begin();
        UserProfile profile = new UserProfile();
        profile.setEmail("linus@redhat.com");
        profile.setLocation("Finland");
        profile.setName("Linus Torvalds");
        profile.setPassword(null);
        profile.setRole("user");
        profile.setUserName("linus");
        try {
            service.createUserProfile(profile);
            Assert.fail("Allowed a user to be created with a null password");
        } catch (Throwable t) {

        }
        utx.rollback();
    }

    @Test
    public void testEmailValidation() throws Exception {
        UserProfile profile = em.find(UserProfile.class, PROFILE_ID);

        Assert.assertFalse(service.isValidEmail(profile.getEmail()));
        Assert.assertTrue(service.isValidEmail("ian@unl.edu"));
        Assert.assertFalse(service.isValidEmail(null));
        Assert.assertFalse(service.isValidEmail("value@"));
    }

    @Test
    public void testValidUserName() throws Exception {
        UserProfile profile = em.find(UserProfile.class, PROFILE_ID);

        Assert.assertTrue(service.isValidUserName("iancottingham"));
        Assert.assertFalse(service.isValidUserName(profile.getUserName()));
        Assert.assertFalse(service.isValidUserName(null));
        Assert.assertFalse(service.isValidUserName(""));
    }

    @Test
    public void testUserLookup() {
        UserProfile profile = em.find(UserProfile.class, PROFILE_ID);
        UserProfile test = service.findUserById(PROFILE_ID);
        Assert.assertEquals(profile, test);

        test = service.findUserByName(profile.getUserName());
        Assert.assertEquals(profile, test);
    }
}
