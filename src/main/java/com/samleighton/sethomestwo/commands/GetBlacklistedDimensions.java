package com.samleighton.sethomestwo.commands;

import com.samleighton.sethomestwo.dao.BlacklistDao;
import com.samleighton.sethomestwo.dao.Dao;
import com.samleighton.sethomestwo.enums.UserError;
import com.samleighton.sethomestwo.enums.UserInfo;
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
            return true;
        }

        Player player = (Player) commandSender;

        // Permission guard
        if(!player.hasPermission("sh2.get-blacklisted-dimensions")){
            ChatUtils.invalidPermissions(player);
            return true;
        }

        // Args length guard
        if (args.length > 0) {
            ChatUtils.incorrectNumArguments(player);
            ChatUtils.sendInfo(player, UserInfo.GET_BLACKLIST_USAGE.getValue());
            return true;
        }

        Dao<String> blacklistDao = new BlacklistDao();
        List<String> blacklistedDimensions = blacklistDao.getAll();

        if (blacklistedDimensions.isEmpty()) {
            ChatUtils.sendInfo(player, UserInfo.NO_BLACKLISTED_DIMENSIONS.getValue());
            return true;
        }

        String blacklist = "Blacklisted Dimensions: " + blacklistedDimensions;
        ChatUtils.sendInfo(player, blacklist);
        return true;
    }
}
