package com.samleighton.sethomestwo.utils;

import com.samleighton.sethomestwo.SetHomesTwo;
import com.samleighton.sethomestwo.enums.DebugLevel;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigUtil {
    private static final FileConfiguration config = SetHomesTwo.getPlugin(SetHomesTwo.class).getConfig();
    private static final DebugLevel debugLevel = DebugLevel.valueOf(config.getString("debugLevel", DebugLevel.ERROR.name()).toUpperCase());

    public static DebugLevel getDebugLevel() {
        return debugLevel;
    }

    public static FileConfiguration getConfig() {
        return config;
    }
}
