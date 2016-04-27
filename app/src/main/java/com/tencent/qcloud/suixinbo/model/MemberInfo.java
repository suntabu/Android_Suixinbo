package com.tencent.qcloud.suixinbo.model;

public class MemberInfo {

    private String userId = "";
    private String userName;
    private String headImagePath = "";
    public String identifier = "";
    public String name;
    public boolean isSpeaking = false;
    public boolean isVideoIn = false;
    public boolean isShareSrc = false;
    public boolean isShareMovie = false;
    public boolean hasGetInfo = false;
    public boolean isHost = false;

    public MemberInfo() {
    }

    public MemberInfo(String Id) {
        userId = Id;
    }

    public MemberInfo(String Id, String name, String path) {
        userId = Id;
        userName = name;
        headImagePath = path;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public void setUserName(String name) {
        userName = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setHeadImagePath(String path) {
        headImagePath = path;
    }

    public String getHeadImagePath() {
        return headImagePath;
    }

    public boolean isHost() {
        return isHost;
    }

    public void setIsHost(boolean isHost) {
        this.isHost = isHost;
    }


    @Override
    public String toString() {
        return "MemberInfo identifier = " + identifier + ", isSpeaking = " + isSpeaking
                + ", isVideoIn = " + isVideoIn + ", isShareSrc = " + isShareSrc
                + ", isShareMovie = " + isShareMovie + ", hasGetInfo = "
                + hasGetInfo + ", name = " + name;
    }
}