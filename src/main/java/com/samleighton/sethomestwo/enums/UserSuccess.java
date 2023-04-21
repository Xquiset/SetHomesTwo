package com.samleighton.sethomestwo.enums;

public enum UserSuccess {
    HOME_CREATED("%s has been created successfully."),
    HOME_DELETED("%s has been deleted successfully."),
    TELEPORTED("Teleported to %s"),
    DIMENSION_ADDED_TO_BLACKLIST("%s has been added to the blacklist"),
    DIMENSION_REMOVED_FROM_BLACKLIST("%s has been removed from the blacklist");

    private final String value;

    UserSuccess(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
