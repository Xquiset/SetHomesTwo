package com.samleighton.sethomestwo.commands;

import com.samleighton.sethomestwo.enums.UserError;
import com.samleighton.sethomestwo.items.HomeItem;
import com.samleighton.sethomestwo.utils.ChatUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class GiveHomesItem implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(UserError.PLAYERS_ONLY.getValue());
            return true;
        }

        // Retrieve the player from the command
        Player player = (Player) commandSender;
        Inventory playerInv = player.getInventory();

        // Permission guard
        if(!player.hasPermission("sh2.give-homes-item")){
            ChatUtils.invalidPermissions(player);
            return true;
        }

        // Create the compass homes item.
        HomeItem homeItem = new HomeItem(player);
        playerInv.addItem(homeItem);

        return true;
    }
}
