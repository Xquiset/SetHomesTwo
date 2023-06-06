package com.samleighton.sethomestwo.commands;

import com.samleighton.sethomestwo.connections.BlacklistConnection;
import com.samleighton.sethomestwo.connections.HomesConnection;
import com.samleighton.sethomestwo.enums.UserError;
import com.samleighton.sethomestwo.enums.UserSuccess;
import com.samleighton.sethomestwo.utils.ChatUtils;
import com.samleighton.sethomestwo.utils.ConfigUtil;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CreateHome implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] args) {
        // Ensure command executor is a player
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(UserError.PLAYERS_ONLY.getValue());
            return false;
        }

        Player player = (Player) commandSender;
        Location playerLocation = player.getLocation();
        HomesConnection homesConnection = new HomesConnection();

        // Permission guard
        if(!player.hasPermission("sh2.create-home")){
            ChatUtils.invalidPermissions(player);
            return false;
        }

        // Guard to ensure we have minimum number of args
        if (args.length < 1) {
            ChatUtils.incorrectNumArguments(player);
            ChatUtils.sendInfo(player, UserError.CREATE_HOME_USAGE.getValue());
            return false;
        }

        // Guard to check if player has exceeded the max number of homes
        if (isMaxHomesReached(player, homesConnection)){
            String errorMessage = ConfigUtil.getConfig().getString("maxHomesReached", UserError.MAX_HOMES.getValue());
            ChatUtils.sendError(player, errorMessage);
            return false;
        }

        // Grab list of blacklisted dimensions, dimension player is in, and dimensions map
        BlacklistConnection blacklistConnection = new BlacklistConnection();
        List<String> blacklistedDimensions = blacklistConnection.getBlacklistedDimensions();
        String playerDimension = player.getWorld().getEnvironment().toString();
        Map<String, String> dimensionsMap = blacklistConnection.getDimensionsMap();

        // Check if player is in a blacklisted dimension before creating home
        if (blacklistedDimensions.contains(dimensionsMap.get(playerDimension))) {
            String errorMessage = ConfigUtil.getConfig().getString("dimensionBlacklisted", UserError.DIMENSION_IS_BLACKLISTED.getValue());
            ChatUtils.sendError(player, errorMessage);
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
        boolean created = homesConnection.createNewHome(player.getUniqueId().toString(), material, playerLocation, homeName, description, player.getWorld().getEnvironment().toString());

        if (!created) {
            Bukkit.getLogger().severe(String.format("Failed to create home for player %s in the database.", player.getUniqueId()));
            ChatUtils.sendError(player, "There was an issue creating your home.");
            return false;
        }

        String message = ConfigUtil.getConfig().getString("homeCreated", UserSuccess.HOME_CREATED.getValue());
        ChatUtils.sendSuccess(player, String.format(message, homeName));
        return false;
    }

    public boolean isMaxHomesReached(Player player, HomesConnection homesConnection){
        boolean isMaxHomesEnabled = ConfigUtil.getConfig().getBoolean("maxHomeEnabled", false);
        if (!isMaxHomesEnabled) return false;

        String maxHomesType = ConfigUtil.getConfig().getString("maxHomesType", "singular");
        int maxHomesAllowed = -1;

        switch (maxHomesType){
            case "singular":
                maxHomesAllowed = ConfigUtil.getConfig().getInt("maxHomes", -1);
                break;
            case "groups":
                ConfigurationSection maxHomesSection = ConfigUtil.getConfig().getConfigurationSection("maxHomes");
                Map<String, Integer> maxHomesMap = new HashMap<>();
                for(String key : Objects.requireNonNull(maxHomesSection).getKeys(false)){
                    maxHomesMap.put(key, ConfigUtil.getConfig().getInt("maxHomes."+key));
                }

                if (!(maxHomesMap.size() > 0)) break;

                LuckPerms lpApi = LuckPermsProvider.get();
                User user = lpApi.getUserManager().getUser(player.getUniqueId());
                String primaryGroup = Objects.requireNonNull(user).getPrimaryGroup();
                if (!maxHomesMap.containsKey(primaryGroup)) break;

                maxHomesAllowed = maxHomesMap.get(primaryGroup);
                break;
        }

        if (maxHomesAllowed == -1) return false;

        int playersHomeCount = homesConnection.getPlayersHomeCount(player.getUniqueId().toString());
        return playersHomeCount >= maxHomesAllowed;
    }
}
