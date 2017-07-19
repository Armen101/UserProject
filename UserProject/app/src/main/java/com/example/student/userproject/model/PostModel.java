package com.example.student.userproject.model;


import java.util.Date;

public class PostModel {
    private String postId;
    private String avatarUri;
    private String title;
    private String uid;
    private Date time;
    private String name;
    private long likesCount;

    public PostModel() {

    }

    public PostModel(String postId, String avatarUri, String title, String uid, Date time, String name, long likesCount) {
        this.postId = postId;
        this.avatarUri = avatarUri;
        this.title = title;
        this.uid = uid;
        this.time = time;
        this.name = name;
        this.likesCount = likesCount;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public long getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(long likesCount) {
        this.likesCount = likesCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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


}


