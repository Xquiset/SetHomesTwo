package com.samleighton.sethomestwo.tabcompleters;

import com.samleighton.sethomestwo.commands.AddDimensionToBlacklist;
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

public class DimensionTabCompleter implements TabCompleter {
    List<String> validDimensions = new AddDimensionToBlacklist().validDimensions;
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        final List<String> completions = new ArrayList<>();

        StringUtil.copyPartialMatches(args[0], validDimensions, completions);
        return completions;
    }
}
