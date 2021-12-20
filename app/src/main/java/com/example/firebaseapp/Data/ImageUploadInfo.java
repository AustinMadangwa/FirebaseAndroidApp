package com.example.firebaseapp.Data;

public class ImageUploadInfo {
    private String imageName, imageURL;

    public ImageUploadInfo() {
    }

    public ImageUploadInfo(String imageName, String imageURL) {
        this.imageName = imageName;
        this.imageURL = imageURL;
    }

    public String getImageName() {
        return imageName;
    }

    public String getImageURL() {
        return imageURL;
    }
}
