package com.joepap.geodataextractor.service.local;

import lombok.experimental.UtilityClass;

@UtilityClass
public class KeyStorage {

    private static String API_KEY = "3ed80c0174c3c3bdf5df64a2eaeea899";

    public static void set(String restApiKey) {
        API_KEY = restApiKey;
    }

    public static String get() {
        return API_KEY;
    }
}
