package com.samleighton.sethomestwo.commands;

import com.samleighton.sethomestwo.connections.BlacklistConnection;
import com.samleighton.sethomestwo.enums.UserError;
import com.samleighton.sethomestwo.utils.ChatUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GetBlacklistedDimensions implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(UserError.PLAYERS_ONLY.getValue());
            return false;
        }

        Player player = (Player) commandSender;

        // Permission guard
        if(!player.hasPermission("sh2.get-blacklisted-dimensions")){
            ChatUtils.invalidPermissions(player);
            return false;
        }

        // Args length guard
        if (args.length > 0) {
            ChatUtils.incorrectNumArguments(player);
            ChatUtils.sendInfo(player, UserError.GET_BLACKLIST_USAGE.getValue());
            return false;
        }

        BlacklistConnection blacklistConnection = new BlacklistConnection();
        List<String> blacklistedDimensions = blacklistConnection.getBlacklistedDimensions();

        if (blacklistedDimensions.size() > 0) {
            String blacklist = "Blacklisted Dimensions: " + blacklistedDimensions;
            ChatUtils.sendInfo(player, blacklist);
        } else {
            ChatUtils.sendInfo(player, "No dimensions are blacklisted");
        }
        return false;
    }
}
