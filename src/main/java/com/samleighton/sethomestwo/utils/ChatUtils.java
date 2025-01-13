package com.samleighton.sethomestwo.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ChatUtils {
    private static final String prefix = ChatColor.GOLD + "[SH2]" + ChatColor.RESET + " ";

    public static void sendInfo(CommandSender player, String msg) {
        player.sendMessage(prefix + msg);
    }

    public static void sendError(CommandSender player, String msg) {
        player.sendMessage(prefix + ChatColor.RED + msg);
    }

    public static void sendSuccess(CommandSender player, String msg) {
        player.sendMessage(prefix + ChatColor.GREEN + msg);
    }

    public static void incorrectNumArguments(CommandSender player) {
        sendError(player, "Incorrect number of arguments supplied to the command.");
    }

    public static void invalidPermissions(CommandSender player) {
        sendError(player, "You do not have permission to perform this action.");
    }

    public static void pluginError(CommandSender player){
        sendError(player, "An error was encountered while performing this actions. Please contact server administrators.");
    }
}
