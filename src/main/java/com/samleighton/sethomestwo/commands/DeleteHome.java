package com.samleighton.sethomestwo.commands;

import com.samleighton.sethomestwo.connections.HomesConnection;
import com.samleighton.sethomestwo.enums.UserError;
import com.samleighton.sethomestwo.enums.UserSuccess;
import com.samleighton.sethomestwo.models.Home;
import com.samleighton.sethomestwo.utils.ChatUtils;
import com.samleighton.sethomestwo.utils.ConfigUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DeleteHome implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(UserError.PLAYERS_ONLY.getValue());
            return false;
        }

        Player player = (Player) commandSender;

        // Args length guard
        if (args.length != 1) {
            ChatUtils.notEnoughArguments(player);
            ChatUtils.sendInfo(player, UserError.DELETE_HOME_USAGE.getValue());
            return false;
        }

        String homeName = args[0];
        String playerUUID = player.getUniqueId().toString();

        HomesConnection homesConnection = new HomesConnection();
        boolean success = homesConnection.deleteHome(playerUUID, homeName);

        // Guard for successful home deletion
        if (!success) {
            ChatUtils.sendError(player, String.format("You do not have a home by the name %s", homeName));
            return false;
        }

        String successMessage = ConfigUtil.getConfig().getString("homeDeleted", UserSuccess.HOME_DELETED.getValue());
        ChatUtils.sendSuccess(player, String.format(successMessage, homeName));
        return false;
    }
}
