package com.samleighton.sethomestwo.enums;

public enum UserError {

    INVALID_MATERIAL("The material you entered is not valid, please try a different one."),
    INVALID_HOME_ITEM("This home item does not belong to you."),
    CREATE_HOME_USAGE("Usage: /create-home [name] [display_material | d | default] [description]"),
    DELETE_HOME_USAGE("Usage: /delete-home [name]"),
    ADD_TO_BLACKLIST_USAGE("Usage: /add-to-blacklist [dimension names]"),
    REMOVE_FROM_BLACKLIST_USAGE("Usage: /remove-from-blacklist [dimension names]"),
    GET_BLACKLIST_USAGE("Usage: /get-blacklisted-dimensions"),
    GET_PLAYER_HOMES_USAGE("Usage: /get-player-homes [playerName]"),
    SET_MAX_HOMES_SINGULAR("Max Homes Type is singular. Usage: /set-max-homes [max number of homes]"),
    SET_MAX_HOMES_GROUPS("Max Homes Type is groups. Usage: /set-max-homes [group name] [max number of homes]"),
    PLAYER_NOT_ONLINE("The player you supplied is either not online or does not exist."),
    ADD_DIMENSION_FAILED("There was an issue adding dimension to the blacklist."),
    REMOVE_DIMENSION_FAILED("There was an issue removing dimension from blacklist."),
    INVALID_DIMENSION("Dimension entered is invalid. Please enter a valid dimension (nether, overworld, end)."),
    DIMENSION_IS_BLACKLISTED("You cannot set home in this dimension because it is blacklisted."),
    TELEPORT_IS_BLACKLISTED("You cannot teleport to this home because dimension is blacklisted."),
    ALREADY_TELEPORTING("You cannot teleport while already teleporting."),
    MOVED_WHILE_TELEPORTING("Teleport has been cancelled because you have moved."),
    NO_HOMES("You have not created any homes yet. Use /create-home."),
    MAX_HOMES("You have reached the maximum number of homes allowed."),
    PLAYERS_ONLY("Only players may execute this command."),
    MAX_HOMES_UPDATE_FAILED("Max homes updated failed."),
    GROUP_DOES_NOT_EXIST("Group does not exist. Use get-max-homes-groups to see all groups.");
    private final String value;

    UserError(String message) {
        this.value = message;
    }

    public String getValue() {
        return value;
    }
}
