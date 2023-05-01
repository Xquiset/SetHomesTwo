package com.samleighton.sethomestwo.commands;

import com.samleighton.sethomestwo.connections.HomesConnection;
import com.samleighton.sethomestwo.enums.UserError;
import com.samleighton.sethomestwo.utils.ChatUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GetPlayerHomes implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(UserError.PLAYERS_ONLY.getValue());
            return false;
        }

        Player player = (Player) commandSender;

        // Args length guard
        if (args.length != 1) {
            ChatUtils.incorrectNumArguments(player);
            ChatUtils.sendError(player, UserError.GET_PLAYER_HOMES_USAGE.getValue());
            return false;
        }

        HomesConnection homesConnection = new HomesConnection();
        // Add a check for if player is online/exists
        if (homesConnection.getPlayerUUID(args[0]) != null) {
            List<String> getPlayersHomes = homesConnection.getPlayerHomesCommand(args[0]);
            String message = args[0] + "'s saved homes: " + getPlayersHomes.toString();
            ChatUtils.sendInfo(player, message);
        } else {
            ChatUtils.sendError(player, UserError.PLAYER_NOT_ONLINE.getValue());
            return false;
        }
        return false;
    }
}
