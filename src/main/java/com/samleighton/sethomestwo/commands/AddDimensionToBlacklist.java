package com.samleighton.sethomestwo.commands;

import com.samleighton.sethomestwo.dao.BlacklistDao;
import com.samleighton.sethomestwo.dao.Dao;
import com.samleighton.sethomestwo.enums.DebugLevel;
import com.samleighton.sethomestwo.enums.UserError;
import com.samleighton.sethomestwo.enums.UserInfo;
import com.samleighton.sethomestwo.enums.UserSuccess;
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

public class AddDimensionToBlacklist implements CommandExecutor {

    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(UserError.PLAYERS_ONLY.getValue());
            return false;
        }

        Player player = (Player) commandSender;

        // Permission guard
        if(!player.hasPermission("sh2.add-to-blacklist")){
            ChatUtils.invalidPermissions(player);
            return true;
        }

        // Args length guard
        if (args.length < 1) {
            ChatUtils.incorrectNumArguments(player);
            ChatUtils.sendInfo(player, UserInfo.ADD_TO_BLACKLIST_USAGE.getValue());
            return true;
        }

        Dao<String> blacklistDao = new BlacklistDao();
        List<String> blacklistedDimensions = blacklistDao.getAll();

        for (String dimension : args) {
            if (!ServerUtil.getValidDimensions().contains(dimension)) {
                ChatUtils.sendError(player, String.format(UserError.INVALID_DIMENSION.getValue(), dimension));
                continue;
            }

            if(blacklistedDimensions.contains(dimension)) {
                ChatUtils.sendError(player, String.format(UserError.DIMENSION_ALREADY_BLACKLISTED.getValue(), dimension));
                continue;
            }

            boolean success = blacklistDao.save(dimension);
            // Successful addition of blacklist guard
            if (!success && ConfigUtil.getDebugLevel().equals(DebugLevel.INFO)) {
                Bukkit.getLogger().info(String.format("Failed to add dimension to blacklist. %s", dimension));
            }

            ChatUtils.sendSuccess(player, String.format(UserSuccess.DIMENSION_ADDED_TO_BLACKLIST.getValue(), dimension));
        }

        return true;
    }
}
