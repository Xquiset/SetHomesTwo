package com.samleighton.sethomestwo;

import com.samleighton.sethomestwo.commands.CreateHome;
import com.samleighton.sethomestwo.commands.DeleteHome;
import com.samleighton.sethomestwo.commands.GiveHomesItem;
import com.samleighton.sethomestwo.connections.ConnectionManager;
import com.samleighton.sethomestwo.connections.HomesConnection;
import com.samleighton.sethomestwo.connections.TeleportationAttemptsConnection;
import com.samleighton.sethomestwo.events.PlayerMoveWhileTeleporting;
import com.samleighton.sethomestwo.events.RightClickHomeItem;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public final class SetHomesTwo extends JavaPlugin {
    private final ConnectionManager connectionManager = new ConnectionManager();

    @Override
    public void onEnable() {
        // Plugin startup logic
        registerCommands();
        registerEventListeners();

        // Create the directories for the plugin
        createDirectories();

        // Init database connections
        boolean success = connectionManager.createConnection("homes", "homes");
        if (success){
            Bukkit.getLogger().info("Homes database connection was successful.");
            new HomesConnection().init();
            new TeleportationAttemptsConnection().init();
        }
    }

    @Override
    public void onDisable() {
        // Close database connections
        connectionManager.closeConnections();
        Bukkit.getLogger().info("Connections closed...");
    }

    /**
     * Register all commands for the plugin.
     */
    public void registerCommands() {
        Objects.requireNonNull(this.getCommand("give-homes-item")).setExecutor(new GiveHomesItem());
        Objects.requireNonNull(this.getCommand("create-home")).setExecutor(new CreateHome());
        Objects.requireNonNull(this.getCommand("delete-home")).setExecutor(new DeleteHome());
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
            if(!success)
                Bukkit.getLogger().warning("Could not create plugin folder.");
        }

        File databasesDir = new File(getDataFolder().getAbsolutePath() + "/database");
        if (!databasesDir.exists()) {
            boolean success = databasesDir.mkdir();
            if(!success)
                Bukkit.getLogger().warning("Could not create databases folder.");
        }
    }

    /**
     * Retrieves the plugin's connection manager.
     * @return ConnectionManager
     */
    public ConnectionManager getConnectionManager() {
        return this.connectionManager;
    }
}
