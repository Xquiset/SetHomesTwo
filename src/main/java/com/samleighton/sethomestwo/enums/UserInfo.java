package com.samleighton.sethomestwo.enums;

public enum UserInfo {
    NO_MAX_HOMES("There is no max number of homes."),
    NO_BLACKLISTED_DIMENSIONS("No dimensions are blacklisted");

    private final String value;

    UserInfo(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
