

package com.example.student.userproject;

import org.parceler.Parcel;

@Parcel
class PhotographInfo {

    private String name;
    private String phone;
    private String address;
    private String camera_info;
    private String imageUri;
    private String avatarUri;
    private String title;
    private String uid;

    public PhotographInfo() {
    }

    public PhotographInfo(String name, String phone, String address, String camera_info, String imageUri, String avatarUri, String title, String uid) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.camera_info = camera_info;
        this.imageUri = imageUri;
        this.avatarUri = avatarUri;
        this.title = title;
        this.uid = uid;
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

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCamera_info() {
        return camera_info;
    }

    public void setCamera_info(String camera_info) {
        this.camera_info = camera_info;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getAvatarUri() {
        return avatarUri;
    }

    public void setAvatarUri(String avatarUri) {
        this.avatarUri = avatarUri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
