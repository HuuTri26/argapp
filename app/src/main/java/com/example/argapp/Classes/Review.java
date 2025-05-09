package com.example.argapp.Classes;

import java.util.HashMap;
import java.util.Map;

public class Review {
    private String userId;
    private String userName;
    private String userImage;
    private float rating;
    private String comment;
    private long timestamp;

    public Review() {}

    public Review(String userId, String userName, String userImage, float rating, String comment, long timestamp) {
        this.userId = userId;
        this.userName = userName;
        this.userImage = userImage;
        this.rating = rating;
        this.comment = comment;
        this.timestamp = timestamp;
    }

    // Convert đối tượng sang Map để lưu trữ trên Firebase
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("userName", userName);
        result.put("userImage", userImage);
        result.put("rating", rating);
        result.put("comment", comment);
        result.put("timestamp", timestamp);
        return result;
    }

    // Getters and setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
