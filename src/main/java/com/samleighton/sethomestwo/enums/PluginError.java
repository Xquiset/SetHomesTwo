package com.samleighton.sethomestwo.enums;

public enum PluginError {


    REMOVE_DIMENSION_FAILED("There was an issue removing dimension from blacklist."),
    ADD_DIMENSION_FAILED("There was an issue adding dimension to the blacklist."),
    MAX_HOMES_UPDATE_FAILED("Max homes updated failed.");

    private final String value;
    PluginError(String message) { this.value = message; }

    public String getValue() { return this.value; }
}
