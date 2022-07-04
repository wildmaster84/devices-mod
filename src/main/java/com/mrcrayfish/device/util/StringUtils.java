package com.mrcrayfish.device.util;

public class StringUtils {
    public static boolean isNullOrEmpty(String text) {
        return text == null || text.isEmpty();
    }

    public static boolean isNotNullOrEmpty(String text) {
        return !isNullOrEmpty(text);
    }
}
