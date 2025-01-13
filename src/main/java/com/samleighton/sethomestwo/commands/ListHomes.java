package com.samleighton.sethomestwo.commands;

import com.samleighton.sethomestwo.enums.UserError;
import com.samleighton.sethomestwo.utils.ChatUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ListHomes implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)){
            ChatUtils.sendError(sender, UserError.PLAYERS_ONLY.getValue());
            return false;
        }

        return false;
    }
}
