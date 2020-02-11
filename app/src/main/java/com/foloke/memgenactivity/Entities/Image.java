package com.foloke.memgenactivity.Entities;


import java.util.Set;

public class Image {


    public int id;

    private byte[] image;

    private int ratingUp;

    private int ratingDown;

    private User user;

    private Set<Tag> tags;

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public int getRatingUp() {
        return ratingUp;
    }

    public void setRatingUp(int ratingUp) {
        this.ratingUp = ratingUp;
    }

    public int getRatingDown() {
        return ratingDown;
    }

    public void setRatingDown(int ratingDown) {
        this.ratingDown = ratingDown;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}