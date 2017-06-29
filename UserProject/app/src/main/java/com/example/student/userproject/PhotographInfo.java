

package com.example.student.userproject;

public class PhotographInfo {

    String name;
    String phone;
    String address;
    String cameraInfo;
    String imageUri;
    String avatarUri;


    public PhotographInfo() {
    }

    public PhotographInfo(String name, String phone, String address, String cameraInfo, String imageUri, String avatarUri, String imageName) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.cameraInfo = cameraInfo;
        this.imageUri = imageUri;
        this.avatarUri = avatarUri;
        this.imageName = imageName;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    String imageName;





    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCameraInfo() {
        return cameraInfo;
    }

    public void setCameraInfo(String cameraInfo) {
        this.cameraInfo = cameraInfo;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
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

    public String getAvatarUri() {
        return avatarUri;
    }

    public void setAvatarUri(String avatarUri) {
        this.avatarUri = avatarUri;
    }
}
