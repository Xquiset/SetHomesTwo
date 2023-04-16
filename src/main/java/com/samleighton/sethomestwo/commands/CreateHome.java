package com.samleighton.sethomestwo.commands;

import com.samleighton.sethomestwo.connections.HomesConnection;
import com.samleighton.sethomestwo.enums.UserError;
import com.samleighton.sethomestwo.enums.UserSuccess;
import com.samleighton.sethomestwo.utils.ChatUtils;
import com.samleighton.sethomestwo.utils.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class CreateHome implements CommandExecutor {

    public CreateHome() {
        super();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] args) {
        // Ensure command executor is a player
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(UserError.PLAYERS_ONLY.getValue());
            return false;
        }

        Player player = (Player) commandSender;
        Location playerLocation = player.getLocation();

        // Guard to ensure we have minimum number of args
        if (args.length < 1) {
            ChatUtils.notEnoughArguments(player);
            ChatUtils.sendInfo(player, UserError.CREATE_HOME_USAGE.getValue());
            return false;
        }

        // Extract parameters from command arguments
        String homeName = args[0];

        String material = "";
        if (args.length > 1)
            material = args[1];

        // Guard to ensure material entered is a valid material
        boolean isMaterialBlankOrDefault = material.equalsIgnoreCase("d") || material.equalsIgnoreCase("default") || material.equalsIgnoreCase("");
        Material mat = isMaterialBlankOrDefault ? Material.WHITE_WOOL : Material.matchMaterial(material);
        if (mat == null) {
            String errorMessage = ConfigUtil.getConfig().getString("invalidHomeItem", UserError.INVALID_MATERIAL.getValue());
            ChatUtils.sendError(player, errorMessage);
            return false;
        }
        if (!mat.isItem()) {
            String errorMessage = ConfigUtil.getConfig().getString("invalidHomeItem", UserError.INVALID_MATERIAL.getValue());
            ChatUtils.sendError(player, errorMessage);
            return false;
        }

        material = mat.name();
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
            Bukkit.getLogger().severe(String.format("Failed to create home for player %s in the database.", player.getUniqueId()));
            ChatUtils.sendError(player, "There was an issue creating your home.");
            return false;
        }

        String message = ConfigUtil.getConfig().getString("homeCreated", UserSuccess.HOME_CREATED.getValue());
        ChatUtils.sendSuccess(player, String.format(message, homeName));
        return false;
    }
}
