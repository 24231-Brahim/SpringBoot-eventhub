package com.eventhub.event_booking_backend.util;

public final class AppConstants {

    private AppConstants() {}

    public static final String API_VERSION = "/api/v1";
    public static final String AUTH_PATH = API_VERSION + "/auth";
    public static final String PUBLIC_PATH = API_VERSION + "/public";
    public static final String USER_PATH = API_VERSION + "/user";
    public static final String ORGANIZER_PATH = API_VERSION + "/organizer";
    public static final String ADMIN_PATH = API_VERSION + "/admin";

    public static final String DEFAULT_PAGE_SIZE = "20";
    public static final int MAX_DESCRIPTION_LENGTH = 2000;
}
