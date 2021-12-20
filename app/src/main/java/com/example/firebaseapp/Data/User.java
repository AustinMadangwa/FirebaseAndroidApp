package com.example.firebaseapp.Data;

public class User {
    private String title;
    private String author;
    private String date;
    private String time;
    private String body;
    private String imageUrl;

    public User(){ }

    public User(String title, String author, String date, String time, String body, String imageUrl) {
        this.title = title;
        this.author = author;
        this.date = date;
        this.time = time;
        this.body = body;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
