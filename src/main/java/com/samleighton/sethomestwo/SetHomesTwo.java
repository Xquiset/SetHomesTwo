package com.samleighton.sethomestwo;

import com.samleighton.sethomestwo.commands.*;
import com.samleighton.sethomestwo.connections.ConnectionManager;
import com.samleighton.sethomestwo.enums.DebugLevel;
import com.samleighton.sethomestwo.events.PlayerJoin;
import com.samleighton.sethomestwo.events.PlayerLeave;
import com.samleighton.sethomestwo.events.PlayerMoveWhileTeleporting;
import com.samleighton.sethomestwo.events.RightClickHomeItem;
import com.samleighton.sethomestwo.gui.HomesGui;
import com.samleighton.sethomestwo.tabcompleters.DimensionTabCompleter;
import com.samleighton.sethomestwo.tabcompleters.HomesTabCompleter;
import com.samleighton.sethomestwo.tabcompleters.MaterialsTabCompleter;
import com.samleighton.sethomestwo.tabcompleters.RemoveDimensionTabCompleter;
import com.samleighton.sethomestwo.utils.ConfigUtil;
import com.samleighton.sethomestwo.utils.DatabaseUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class SetHomesTwo extends JavaPlugin {
    private final ConnectionManager connectionManager = new ConnectionManager();
    private final Map<UUID, HomesGui> homesGuiMap = new HashMap<>();

    @Override
    public void onEnable() {
        // Create the directories for the plugin
        createDirectories();

        // Create config
        initConfig();

        // Plugin startup logic
        registerCommands();
        registerEventListeners();

        // Load online players into gui map just in case this was a reload
        for(Player player : Bukkit.getOnlinePlayers()){
            homesGuiMap.put(player.getUniqueId(), new HomesGui(player));
        }

        // Init database connections
        boolean success = connectionManager.createConnection("homes", "homes");
        if (success) {
            if (ConfigUtil.getDebugLevel().equals(DebugLevel.INFO))
                Bukkit.getLogger().info("Homes database connection was successful.");

            boolean createdTables = DatabaseUtil.initTables(connectionManager.getConnection("homes"));
            if (createdTables && ConfigUtil.getDebugLevel().equals(DebugLevel.INFO))
                Bukkit.getLogger().info("Table initialization was successfully executed.");

        } else {
            Bukkit.getLogger().severe("Could not create database connection!");
        }
    }

    @Override
    public void onDisable() {
        // Close database connections
        connectionManager.closeConnections();

        if (ConfigUtil.getDebugLevel().equals(DebugLevel.INFO))
            Bukkit.getLogger().info("All database connections closed.");
    }

    /**
     * Initialize the default values for the config
     */
    public void initConfig() {
        File outputConfig = new File(getDataFolder(), "config.yml");
        if (outputConfig.exists()) return;

        try {
            if (!outputConfig.createNewFile()) return;
        } catch (IOException e) {
            Bukkit.getLogger().info("Could not create output config file for initializing default config.yml!");
        }

        InputStream is = this.getResource("default-config.yml");
        if (is == null) return;

        try {
            byte[] buffer = new byte[is.available()];
            int bytes = is.read(buffer);

            if (bytes > 0 && ConfigUtil.getDebugLevel().equals(DebugLevel.INFO))
                Bukkit.getLogger().info("Writing default config file...");

            Files.write(outputConfig.toPath(), buffer);
        } catch (IOException e) {
            Bukkit.getLogger().info("There was an issue copying data to config.yml");
        }
    }

    /**
     * Register all commands for the plugin.
     */
    public void registerCommands() {
        Objects.requireNonNull(this.getCommand("give-homes-item")).setExecutor(new GiveHomesItem());

        PluginCommand listHomes = Objects.requireNonNull(this.getCommand("list-homes"));
        listHomes.setExecutor(new ListHomes());

        PluginCommand goHome = Objects.requireNonNull(this.getCommand("go-home"));
        goHome.setExecutor(new GoHome());
        goHome.setTabCompleter(new HomesTabCompleter());

        PluginCommand createHome = Objects.requireNonNull(this.getCommand("create-home"));
        createHome.setExecutor(new CreateHome());
        createHome.setTabCompleter(new MaterialsTabCompleter());

        PluginCommand deleteHome = Objects.requireNonNull(this.getCommand("delete-home"));
        deleteHome.setExecutor(new DeleteHome());
        deleteHome.setTabCompleter(new HomesTabCompleter());

        PluginCommand addToBlacklist = Objects.requireNonNull(this.getCommand("add-to-blacklist"));
        addToBlacklist.setExecutor(new AddDimensionToBlacklist());
        addToBlacklist.setTabCompleter(new DimensionTabCompleter());

        PluginCommand removeFromBlacklist = Objects.requireNonNull(this.getCommand("remove-from-blacklist"));
        removeFromBlacklist.setExecutor(new RemoveDimensionFromBlacklist());
        removeFromBlacklist.setTabCompleter(new RemoveDimensionTabCompleter());

        PluginCommand getBlacklistedDimensions = Objects.requireNonNull(this.getCommand("get-blacklisted-dimensions"));
        getBlacklistedDimensions.setExecutor(new GetBlacklistedDimensions());

        PluginCommand getPlayerHomes = Objects.requireNonNull(this.getCommand("get-player-homes"));
        getPlayerHomes.setExecutor(new GetPlayerHomes(this));

        PluginCommand setMaxHomes = Objects.requireNonNull(this.getCommand("set-max-homes"));
        setMaxHomes.setExecutor(new SetMaxHomes(this));
    }

    /**
     * Register all event listeners for the plugin
     */
    public void registerEventListeners() {
        getServer().getPluginManager().registerEvents(new PlayerJoin(this), this);
        getServer().getPluginManager().registerEvents(new PlayerLeave(this), this);
        getServer().getPluginManager().registerEvents(new RightClickHomeItem(this), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveWhileTeleporting(), this);
    }

    /**
     * Creates the directories necessary for plugin functionality
     */
    public void createDirectories() {
        // Create the plugin directory
        if (!getDataFolder().exists()) {
            boolean success = getDataFolder().mkdir();
            if (!success)
                Bukkit.getLogger().severe("Could not create plugin folder.");
        }

        File databasesDir = new File(getDataFolder().getAbsolutePath() + "/database");
        if (!databasesDir.exists()) {
            boolean success = databasesDir.mkdir();
            if (!success)
                Bukkit.getLogger().severe("Could not create database folder.");
        }
    }

    /**
     * Retrieves the plugin's connection manager.
     *
     * @return ConnectionManager
     */
    public ConnectionManager getConnectionManager() {
        return this.connectionManager;
    }

    public Map<UUID, HomesGui> getHomesGuiMap() {
        return this.homesGuiMap;
    }
}
