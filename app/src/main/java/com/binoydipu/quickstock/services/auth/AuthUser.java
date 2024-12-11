package com.binoydipu.quickstock.services.auth;

public class AuthUser {
    String userId, userName, staffId, userEmail, mobileNo;
    boolean isEmailVerified;

    public AuthUser() {}

    public AuthUser(String userId, String userName, String staffId, String userEmail, String mobileNo, boolean isEmailVerified) {
        this.userId = userId;
        this.userName = userName;
        this.staffId = staffId;
        this.userEmail = userEmail;
        this.mobileNo = mobileNo;
        this.isEmailVerified = isEmailVerified;
    }

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

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        isEmailVerified = emailVerified;
    }
}
