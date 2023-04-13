package com.samleighton.sethomestwo.commands;

import com.samleighton.sethomestwo.connections.HomesConnection;
import com.samleighton.sethomestwo.utils.ChatUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DeleteHome implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] args) {
        if(!(commandSender instanceof Player)) return false;

        Player player = (Player) commandSender;

        // Args length guard
        if(args.length != 1) {
            ChatUtils.notEnoughArguments(player);
            ChatUtils.sendInfo(player, "Usage: /delete-home [name]");
            return false;
        }

        String homeName = args[0];
        String playerUUID = player.getUniqueId().toString();

        HomesConnection homesConnection = new HomesConnection();
        boolean success = homesConnection.deleteHome(playerUUID, homeName);

        if(success) ChatUtils.sendSuccess(player, String.format("%s has been deleted successfully.", homeName));
        return false;
    }
}
