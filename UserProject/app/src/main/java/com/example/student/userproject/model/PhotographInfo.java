

package com.example.student.userproject.model;

import org.parceler.Parcel;

@Parcel
public class PhotographInfo {

    private String name;
    private String phone;
    private String address;
    private String camera_info;
    private String imageUri;
    private String avatarUri;
    private String title;
    private String uid;
    private double latitude;
    private double longitude;

    public PhotographInfo() {
    }

    public PhotographInfo(String name, String phone, String address, String camera_info,
                          String imageUri, String avatarUri, String title, String uid,
                          double latitude, double longitude) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.camera_info = camera_info;
        this.imageUri = imageUri;
        this.avatarUri = avatarUri;
        this.title = title;
        this.uid = uid;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }


    public String getAddress() {
        return address;
    }


    public String getCamera_info() {
        return camera_info;
    }


    public String getImageUri() {
        return imageUri;
    }


    public String getAvatarUri() {
        return avatarUri;
    }


    public String getTitle() {
        return title;
    }


    public String getUid() {
        return uid;
    }


    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

}