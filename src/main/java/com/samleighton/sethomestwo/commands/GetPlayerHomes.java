package com.samleighton.sethomestwo.commands;

import com.samleighton.sethomestwo.SetHomesTwo;
import com.samleighton.sethomestwo.dao.Dao;
import com.samleighton.sethomestwo.dao.HomesDao;
import com.samleighton.sethomestwo.enums.DebugLevel;
import com.samleighton.sethomestwo.enums.UserError;
import com.samleighton.sethomestwo.enums.UserInfo;
import com.samleighton.sethomestwo.gui.HomesGui;
import com.samleighton.sethomestwo.models.Home;
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
import java.util.UUID;

public class GetPlayerHomes implements CommandExecutor {

    private final SetHomesTwo plugin;

    public GetPlayerHomes(SetHomesTwo plugin){
        this.plugin = plugin;
    }

    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] args) {
        // Player instance guard
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(UserError.PLAYERS_ONLY.getValue());
            return true;
        }

        Player requester = (Player) commandSender;

        // Permission guard
        if(!requester.hasPermission("sh2.get-player-homes")){
            ChatUtils.invalidPermissions(requester);
            return true;
        }

        // Args length guard
        if (args.length != 1) {
            ChatUtils.incorrectNumArguments(requester);
            ChatUtils.sendError(requester, UserInfo.GET_PLAYER_HOMES_USAGE.getValue());
            return true;
        }

        String uuidString = ServerUtil.getPlayerUUID(args[0]);

        // Add a check for if player is online/exists
        if (uuidString == null) {
            ChatUtils.sendError(requester, UserError.PLAYER_NOT_ONLINE.getValue());
            return true;
        }

        Dao<Home> homesDao = new HomesDao(true);
        List<Home> playersHomes = homesDao.getAll(UUID.fromString(uuidString));

        Player player = Bukkit.getPlayer(UUID.fromString(uuidString));
        if(player == null) return true;

        HomesGui homesGui = new HomesGui(requester, "Homes of " + player.getDisplayName());
        homesGui.showHomes(playersHomes, requester);

        plugin.getServer().getPluginManager().registerEvents(homesGui, plugin);

        if (ConfigUtil.getDebugLevel().equals(DebugLevel.INFO))
            Bukkit.getLogger().info(String.format("%s is viewing homes of player %s", requester.getDisplayName(), player.getDisplayName()));

        return true;
    }
}
