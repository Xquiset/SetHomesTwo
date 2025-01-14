package com.samleighton.sethomestwo.commands;

import com.samleighton.sethomestwo.dao.Dao;
import com.samleighton.sethomestwo.dao.HomesDao;
import com.samleighton.sethomestwo.enums.UserError;
import com.samleighton.sethomestwo.models.Home;
import com.samleighton.sethomestwo.utils.ChatUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class GoHome implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        // Guard for player being console command sender
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(UserError.PLAYERS_ONLY.getValue());
            return true;
        }

        Player player = (Player) commandSender;

        // Args length guard
        if(args.length != 1){
            ChatUtils.incorrectNumArguments(player);
            return true;
        }

        // Permission guard
        if(!player.hasPermission("sh2.go-home")) {
            ChatUtils.invalidPermissions(player);
            return true;
        }

        // Get players home dao instance
        String desiredHomeName = args[0];
        Dao<Home> homesDao = new HomesDao();
        ArrayList<Home> playerHomes = (ArrayList<Home>) homesDao.getAll(player.getUniqueId());
        Home homeToTeleportTo = null;

        // Check for and obtain home instance if it exists
        for(Home home : playerHomes) {
            if(home.getName().equalsIgnoreCase(desiredHomeName)){
                homeToTeleportTo = home;
            }
        }

        // Home does not exist guard
        if(homeToTeleportTo == null){
            ChatUtils.sendError(player, UserError.HOME_DOES_NOT_EXIST.getValue());
            return true;
        }

        // Teleport player to home
        homeToTeleportTo.teleport(player);
        return true;
    }
}
