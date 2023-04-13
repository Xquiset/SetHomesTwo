package com.samleighton.sethomestwo.commands;

import com.samleighton.sethomestwo.connections.HomesConnection;
import com.samleighton.sethomestwo.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class CreateHome implements CommandExecutor {

    public CreateHome() {
        super();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) return false;

        Player player = (Player) commandSender;
        Location playerLocation = player.getLocation();

        // Args length guard
        if (args.length < 1) {
            ChatUtils.notEnoughArguments(player);
            ChatUtils.sendInfo(player, "Usage: /create-home [name] [description]");
            return false;
        }

        // Extract parameters from command arguments
        String homeName = args[0];

        String material = null;
        if(args.length > 1)
            material = args[1];

        String description = null;
        StringBuilder stringBuilder = new StringBuilder();

        // Build description from leftover arguments
        if (args.length > 2) {
            String[] remainingArgs = Arrays.copyOfRange(args, 2, args.length);
            for (int i = 0; i < remainingArgs.length; i++) {
                String arg = remainingArgs[i];
                if (i == remainingArgs.length - 1) {
                    stringBuilder.append(arg);
                } else {
                    stringBuilder.append(arg).append(" ");
                }
            }

            description = stringBuilder.toString();
        }

        // Create the home
        HomesConnection homesConnection = new HomesConnection();
        boolean created = homesConnection.createNewHome(player.getUniqueId().toString(), material, playerLocation, homeName, description);

        if (!created) {
            Bukkit.getLogger().warning("Could not create home for player " + player.getUniqueId());
        }

        ChatUtils.sendSuccess(player, homeName + " has been created successfully.");
        return false;
    }
}
