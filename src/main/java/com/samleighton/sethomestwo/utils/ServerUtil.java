package com.samleighton.sethomestwo.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerUtil {
    private final static List<String> validDimensions = new ArrayList<>() {
        {
            Bukkit.getWorlds().forEach(world -> add(world.getName().toLowerCase()));
        }
    };

    // Mapping the environment grabbed from player to our valid dimension list
    private final static Map<String, String> dimensionsMap = new HashMap<>() {{
        put("NORMAL", validDimensions.get(0));
        put("NETHER", validDimensions.get(1));
        put("THE_END", validDimensions.get(2));
    }};

    /**
     * Retrieve a list of the server's valid dimensions.
     *
     * @return List
     */
    public static List<String> getValidDimensions() {
        return validDimensions;
    }

    /**
     * Retrieve dimensions mapping.
     *
     * @return Map<String, String>
     */
    public static Map<String, String> getDimensionsMap() {
        return dimensionsMap;
    }

    public static String getPlayerUUID(String playerName){
        for(Player player : Bukkit.getOnlinePlayers()) {
            String name = player.getDisplayName();
            if(name.equals(playerName)) {
                return player.getUniqueId().toString();
            }
        }

        return null;
    }
}
