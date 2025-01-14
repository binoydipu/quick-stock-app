package com.binoydipu.quickstock.services.cloud;

public class CloudStorageConstants {
    public static final String USER_COLLECTION = "users";
    public static final String FIELD_UID = "userId";
    public static final String FIELD_USER_NAME = "userName";
    public static final String FIELD_STAFF_ID = "staffId";
    public static final String FIELD_USER_EMAIL = "userEmail";
    public static final String FIELD_USER_MOBILE = "mobileNo";
    public static final String FIELD_USER_EMAIL_VERIFIED = "emailVerified";

    public static final String ITEM_COLLECTION = "items";

    public static final String ON_ITEM_EXISTS = "alreadyExists";
    public static final String ON_ITEM_ADDED = "itemAdded";
    public static final String ON_ITEM_CHECK_FAILURE = "checkFailed";

    CloudStorageConstants() {}
}
