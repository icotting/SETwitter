/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unl.raikes.se.twitter.entity;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

/**
 *
 * @author iancottingham
 */
@Entity
public class UserProfile implements Serializable, Comparable<UserProfile> {

    private long id;
    private String name;
    private String location;
    private String email;
    private String userName;
    private String password;
    private String role;

    private Collection<Feed> feeds;
    private Collection<Feed> subscriptions;

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Column(unique = true)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public Collection<Feed> getFeeds() {
        return feeds;
    }

    public void setFeeds(Collection<Feed> feeds) {
        this.feeds = feeds;
    }

    @ManyToMany(targetEntity = Feed.class, fetch = FetchType.EAGER)
    public Collection<Feed> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Collection<Feed> subscriptions) {
        this.subscriptions = subscriptions;
    }

    @Column(unique = true)
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    /* Method overrides for equality and comparison operations */
    @Override
    public int compareTo(UserProfile o) {
        return Long.compare(this.getId(), o.getId());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UserProfile) {
            return ((UserProfile) obj).getId() == this.id;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.email.hashCode();
    }
}
