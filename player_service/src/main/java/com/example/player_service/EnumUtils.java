package com.example.player_service;

public class EnumUtils {

    public static <T> void throwUnknownValue(T value) throws IllegalArgumentException {
        throw new IllegalArgumentException("UNKNOWN ENUM VALUE: "+value);
    }
}
