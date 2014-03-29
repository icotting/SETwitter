/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unl.raikes.se.twitter.entity;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author iancottingham
 */
@Entity
public class Feed implements Serializable, Comparable<Feed> {

    private long id;
    private String name;
    private Collection<Tweet> tweets;
    private Collection<UserProfile> subscribers;
    private UserProfile owner;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(mappedBy = "belongsTo", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public Collection<Tweet> getTweets() {
        return tweets;
    }

    public void setTweets(Collection<Tweet> tweets) {
        this.tweets = tweets;
    }

    @ManyToMany(mappedBy = "subscriptions", targetEntity = UserProfile.class)
    public Collection<UserProfile> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(Collection<UserProfile> subscribers) {
        this.subscribers = subscribers;
    }

    @ManyToOne
    public UserProfile getOwner() {
        return owner;
    }

    public void setOwner(UserProfile owner) {
        this.owner = owner;
    }

    /* Method overrides for equality and comparison operations */
    @Override
    public int compareTo(Feed o) {
        return Long.compare(this.getId(), o.getId());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Feed) {
            return ((Feed) obj).getId() == this.id;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return String.format("%s-%s", 
                this.owner.getUserName(), this.name).hashCode();
    }
}
