package com.samleighton.sethomestwo.connections;

import com.samleighton.sethomestwo.SetHomesTwo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ConnectionManager {
    private final Map<String, Connection> activeConnections;

    public ConnectionManager() {
        this.activeConnections = new HashMap<>();
    }

    public Connection getConnection(String key) {
        return this.activeConnections.get(key);
    }

    public void addConnection(String key, Connection connection) {
        this.activeConnections.put(key, connection);
    }

    public boolean createConnection(String key, String dbName) {
        SetHomesTwo plugin = SetHomesTwo.getPlugin(SetHomesTwo.class);
        String dbURL = "jdbc:sqlite:" + plugin.getDataFolder().getAbsolutePath() + "/database/" + dbName + ".db";

        try {
            Connection connection = DriverManager.getConnection(dbURL);
            this.addConnection(key, connection);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Close all active connections
     */
    public void closeConnections() {
        for (Connection conn : this.activeConnections.values()) {
            if (conn == null) continue;

            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
