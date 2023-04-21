package com.samleighton.sethomestwo.commands;

import com.samleighton.sethomestwo.connections.BlacklistConnection;
import com.samleighton.sethomestwo.enums.DebugLevel;
import com.samleighton.sethomestwo.enums.UserError;
import com.samleighton.sethomestwo.enums.UserSuccess;
import com.samleighton.sethomestwo.utils.ChatUtils;
import com.samleighton.sethomestwo.utils.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddDimensionToBlacklist implements CommandExecutor {

    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(UserError.PLAYERS_ONLY.getValue());
            return false;
        }

        Player player = (Player) commandSender;

        // Args length guard
        if (args.length < 1) {
            ChatUtils.notEnoughArguments(player);
            ChatUtils.sendInfo(player, UserError.ADD_TO_BLACKLIST_USAGE.getValue());
            return false;
        }

        BlacklistConnection blacklistConnection = new BlacklistConnection();
        List<String> blacklistedDimensions = new ArrayList<>();

        for (String dimension : args) {
            if (!blacklistConnection.getValidDimensions().contains(dimension)) {
                ChatUtils.sendError(player, UserError.INVALID_DIMENSION.getValue());
                continue;
            }

            blacklistedDimensions.add(dimension);
        }

        boolean success = blacklistConnection.addToBlacklistTable(blacklistedDimensions);
        // Guard for successful addition of blacklist
        if (!success && ConfigUtil.getDebugLevel().equals(DebugLevel.INFO)) {
            Bukkit.getLogger().info(String.format("Failed to add dimensions to blacklist. %s", Arrays.toString(blacklistedDimensions.toArray())));
            ChatUtils.sendError(player, "There was an issue adding dimension to the blacklist.");
            return false;
        }
        String successMessage = ConfigUtil.getConfig().getString("dimensionAddedToBlacklist", UserSuccess.DIMENSION_ADDED_TO_BLACKLIST.getValue());
        StringBuilder stringBuilder = new StringBuilder();

        // Build comma sep list of added dimensions
        for(String dimension : blacklistedDimensions) {
            stringBuilder.append(dimension).append(", ");
        }

        String dimensionList = stringBuilder.substring(0, stringBuilder.length() - 2);
        ChatUtils.sendSuccess(player, String.format(successMessage, dimensionList));
        return false;
    }
}
