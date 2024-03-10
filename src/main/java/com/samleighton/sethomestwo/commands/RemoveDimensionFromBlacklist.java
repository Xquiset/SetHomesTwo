package com.samleighton.sethomestwo.commands;

import com.samleighton.sethomestwo.dao.BlacklistDao;
import com.samleighton.sethomestwo.dao.Dao;
import com.samleighton.sethomestwo.enums.*;
import com.samleighton.sethomestwo.utils.ChatUtils;
import com.samleighton.sethomestwo.utils.ConfigUtil;
import com.samleighton.sethomestwo.utils.ServerUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RemoveDimensionFromBlacklist implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(UserError.PLAYERS_ONLY.getValue());
            return false;
        }

        Player player = (Player) commandSender;

        // Permission guard
        if(!player.hasPermission("sh2.remove-from-blacklist")){
            ChatUtils.invalidPermissions(player);
            return false;
        }

        // Args length guard
        if (args.length < 1) {
            ChatUtils.incorrectNumArguments(player);
            ChatUtils.sendInfo(player, UserInfo.REMOVE_FROM_BLACKLIST_USAGE.getValue());
            return false;
        }

        Dao<String> blacklistDao = new BlacklistDao();
        List<String> blacklistedDimensions = blacklistDao.getAll();

        for (String dimension : args) {
            if (!ServerUtil.getValidDimensions().contains(dimension)) {
                ChatUtils.sendError(player, String.format(UserError.INVALID_DIMENSION.getValue(), dimension));
                continue;
            }

            if(!blacklistedDimensions.contains(dimension)) {
                ChatUtils.sendError(player, String.format(UserError.DIMENSION_IS_NOT_BLACKLISTED.getValue(), dimension));
                continue;
            }

            boolean success = blacklistDao.delete(dimension);
            // Guard for successful addition of blacklist
            if (!success && ConfigUtil.getDebugLevel().equals(DebugLevel.ERROR)) {
                Bukkit.getLogger().info(String.format("Failed to remove dimension from blacklist. %s", dimension));
                ChatUtils.sendError(player, PluginError.REMOVE_DIMENSION_FAILED.getValue());
            }

            ChatUtils.sendSuccess(player, String.format(
                    ConfigUtil.getConfig().getString("dimensionRemovedFromBlacklist", UserSuccess.DIMENSION_REMOVED_FROM_BLACKLIST.getValue()),
                    dimension
            ));
        }

        return false;
    }
}
