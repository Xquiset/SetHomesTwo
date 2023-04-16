package com.samleighton.sethomestwo.enums;

public enum UserError {

    INVALID_MATERIAL("The material you entered is not valid, please try a different one."),
    INVALID_HOME_ITEM("This home item does not belong to you."),
    CREATE_HOME_USAGE("Usage: /create-home [name] [display_material | d | default] [description]"),
    DELETE_HOME_USAGE("Usage: /delete-home [name]"),
    ALREADY_TELEPORTING("You cannot teleport while already teleporting."),
    MOVED_WHILE_TELEPORTING("Teleport has been cancelled because you have moved."),
    NO_HOMES("You have not created any homes yet. Use /create-home."),
    PLAYERS_ONLY("Only players may execute this command.");
    private final String value;

    UserError(String message) {
        this.value = message;
    }

    public String getValue() {
        return value;
    }
}
