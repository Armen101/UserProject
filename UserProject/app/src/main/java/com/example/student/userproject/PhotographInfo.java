

package com.example.student.userproject;

public class PhotographInfo {

    String name;
    String phone;

    String avatarUri;

    public PhotographInfo() {
    }

    public PhotographInfo(String name, String phone, String avatarUri) {
        this.name = name;
        this.phone = phone;
        this.avatarUri = avatarUri;
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
