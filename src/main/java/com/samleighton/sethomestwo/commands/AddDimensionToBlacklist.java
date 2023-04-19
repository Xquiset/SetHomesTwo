package com.samleighton.sethomestwo.commands;

import com.samleighton.sethomestwo.connections.BlacklistConnection;
import com.samleighton.sethomestwo.enums.UserError;
import com.samleighton.sethomestwo.enums.UserSuccess;
import com.samleighton.sethomestwo.utils.ChatUtils;
import com.samleighton.sethomestwo.utils.ConfigUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AddDimensionToBlacklist implements CommandExecutor {
    public List<String> validDimensions = new ArrayList<String>(){
        {
            add("nether");
            add("overworld");
            add("end");
        }
    };
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

        List<String> blacklist = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            if (!validDimensions.contains(args[i])) {
                ChatUtils.sendError(player, UserError.INVALID_DIMENSION.getValue());
                return false;
            }
            blacklist.add(args[i]);
        }

        BlacklistConnection blacklistConnection = new BlacklistConnection();
        boolean success = blacklistConnection.addToBlacklistTable(blacklist);

        // Guard for successful addition of blacklist
        if (!success) {
            ChatUtils.sendError(player, String.format("Failed to add dimensions to blacklist"));
            return false;
        }

        String successMessage = ConfigUtil.getConfig().getString("dimensionAddedToBlacklist", UserSuccess.DIMENSION_ADDED_TO_BLACKLIST.getValue());
        ChatUtils.sendSuccess(player, String.format(successMessage, blacklist));
        return false;
    }
}
