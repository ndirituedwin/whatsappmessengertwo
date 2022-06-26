package com.example.whatsappclone.Friends;

public class Contacts {
    public String username, status,image;

    public Contacts() {
    }

    public Contacts(String username, String status, String image) {
        this.username = username;
        this.status = status;
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
