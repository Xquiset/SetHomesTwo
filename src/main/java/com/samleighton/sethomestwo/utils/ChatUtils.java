package com.samleighton.sethomestwo.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatUtils {
    private static final String prefix = ChatColor.GOLD + "[SH2]" + ChatColor.RESET + " ";
    public static void sendInfo(Player player, String msg){
        player.sendMessage(prefix + msg);
    }

    public static void sendError(Player player, String msg){
        player.sendMessage(prefix + ChatColor.RED + msg);
    }

    public static void sendSuccess(Player player, String msg) {
        player.sendMessage(prefix + ChatColor.GREEN + msg);
    }

    public static void notEnoughArguments(Player player){
        sendError(player, "Incorrect number of arguments supplied to the command.");
    }
}
