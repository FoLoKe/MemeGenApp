package com.foloke.memgenactivity.Entities;


import java.util.Set;

public class Meme {


    public int id;

    private byte[] image;

    private Set<User> likes;

    private Set<User> dislikes;

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

    public Set<User> getLikes() {
        return likes;
    }

    public void setLikes(Set<User> likes) {
        this.likes = likes;
    }

    public Set<User> getDislikes() {
        return dislikes;
    }

    public void setDislikes(Set<User> dislikes) {
        this.dislikes = dislikes;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
