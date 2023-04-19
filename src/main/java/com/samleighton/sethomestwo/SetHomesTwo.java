package com.samleighton.sethomestwo;

import com.samleighton.sethomestwo.commands.AddDimensionToBlacklist;
import com.samleighton.sethomestwo.commands.CreateHome;
import com.samleighton.sethomestwo.commands.DeleteHome;
import com.samleighton.sethomestwo.commands.GiveHomesItem;
import com.samleighton.sethomestwo.connections.BlacklistConnection;
import com.samleighton.sethomestwo.connections.ConnectionManager;
import com.samleighton.sethomestwo.connections.HomesConnection;
import com.samleighton.sethomestwo.connections.TeleportationAttemptsConnection;
import com.samleighton.sethomestwo.enums.DebugLevel;
import com.samleighton.sethomestwo.events.PlayerMoveWhileTeleporting;
import com.samleighton.sethomestwo.events.RightClickHomeItem;
import com.samleighton.sethomestwo.tabcompleters.DimensionTabCompleter;
import com.samleighton.sethomestwo.tabcompleters.HomesTabCompleter;
import com.samleighton.sethomestwo.tabcompleters.MaterialsTabCompleter;
import com.samleighton.sethomestwo.utils.ConfigUtil;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public final class SetHomesTwo extends JavaPlugin {
    private final ConnectionManager connectionManager = new ConnectionManager();

    @Override
    public void onEnable() {
        // Create the directories for the plugin
        createDirectories();

        // Create config
        initConfig();

        // Plugin startup logic
        registerCommands();
        registerEventListeners();

        // Init database connections
        boolean success = connectionManager.createConnection("homes", "homes");
        if (success) {
            if (ConfigUtil.getDebugLevel().equals(DebugLevel.INFO))
                Bukkit.getLogger().info("Homes database connection was successful.");

            new HomesConnection().init();
            new TeleportationAttemptsConnection().init();
            new BlacklistConnection().init();
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

        try (InputStream defaultConfig = this.getResource("default-config.yml")) {
            if (outputConfig.exists()) return;
            if (!outputConfig.createNewFile()) return;

            try (FileWriter fileWriter = new FileWriter(outputConfig)) {
                assert defaultConfig != null;
                IOUtils.copy(defaultConfig, fileWriter);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            Bukkit.getLogger().severe("There was an issue creating the default config file.");
            e.printStackTrace();
        }
    }

    /**
     * Register all commands for the plugin.
     */
    public void registerCommands() {
        Objects.requireNonNull(this.getCommand("give-homes-item")).setExecutor(new GiveHomesItem());

        PluginCommand createHome = Objects.requireNonNull(this.getCommand("create-home"));
        createHome.setExecutor(new CreateHome());
        createHome.setTabCompleter(new MaterialsTabCompleter());

        PluginCommand deleteHome = Objects.requireNonNull(this.getCommand("delete-home"));
        deleteHome.setExecutor(new DeleteHome());
        deleteHome.setTabCompleter(new HomesTabCompleter());

        PluginCommand addBlacklist = Objects.requireNonNull(this.getCommand("add-to-blacklist"));
        addBlacklist.setExecutor(new AddDimensionToBlacklist());
        addBlacklist.setTabCompleter(new DimensionTabCompleter());
    }

    /**
     * Register all event listeners for the plugin
     */
    public void registerEventListeners() {
        getServer().getPluginManager().registerEvents(new RightClickHomeItem(), this);
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
}
