package com.samleighton.sethomestwo.tabcompleters;

import com.samleighton.sethomestwo.connections.HomesConnection;
import com.samleighton.sethomestwo.models.Home;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HomesTabCompleter implements TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        final List<String> completions = new ArrayList<>();

        // Guard to check if command sender is player
        if (!(commandSender instanceof Player)) return new ArrayList<>();

        Player player = (Player) commandSender;
        HomesConnection homesConnection = new HomesConnection();
        final List<Home> playerHomes = homesConnection.getPlayersHomes(player.getUniqueId().toString());
        final List<String> homeNames = new ArrayList<>();

        for (Home home : playerHomes) {
            homeNames.add(home.getName());
        }

        StringUtil.copyPartialMatches(args[0], homeNames, completions);
        return completions;
    }
}
