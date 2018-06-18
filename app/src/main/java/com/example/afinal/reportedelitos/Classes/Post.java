package com.example.afinal.reportedelitos.Classes;

public class Post {

    private String postId;
    private String description;
    private String imageUrl;
    private String city;

    public Post() { }

    public Post(String postId, String description, String imageUrl, String city) {
        this.postId = postId;
        this.description = description;
        this.imageUrl = imageUrl;
        this.city = city;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
