package com.samleighton.sethomestwo.connections;

import com.samleighton.sethomestwo.SetHomesTwo;
import com.samleighton.sethomestwo.models.Home;
import com.samleighton.sethomestwo.utils.DatabaseUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HomesConnection {

    private final Connection db;

    public HomesConnection() {
        db = SetHomesTwo.getPlugin(SetHomesTwo.class).getConnectionManager().getConnection("homes");
    }

    /**
     * Create the homes table if it does not exist.
     */
    public void init() {
        if (db == null) return;

        String createSQL = "create table if not exists players_homes (\n" +
                "id integer PRIMARY KEY, \n" +
                "player_uuid TEXT NOT NULL, \n" +
                "material TEXT NOT NULL, \n" +
                "world TEXT NOT NULL, \n" +
                "name TEXT NOT NULL, \n" +
                "description TEXT, \n" +
                "x real NOT NULL, \n" +
                "y real NOT NULL, \n" +
                "z real NOT NULL, \n" +
                "pitch real NOT NULL, \n" +
                "yaw real NOT NULL " +
                ");";
        DatabaseUtil.execute(db, createSQL);
    }


    /**
     * Create a new home and add it to the database.
     *
     * @param playerUUID,     The uuid of the player this home belongs to.
     * @param playerLocation, The location of the home
     * @param name,           The players desired name of the home.
     * @param description,    The players description of the home.
     * @return boolean
     */
    public boolean createNewHome(String playerUUID, String material, Location playerLocation, String name, String description) {
        material = material == null ? Material.WHITE_WOOL.name() : material;
        Home home = new Home(playerUUID, material, playerLocation, name, description);
        String sql = "insert into players_homes (player_uuid, world, material, name, description, x, y, z, pitch, yaw) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        return DatabaseUtil.execute(
                db,
                sql,
                home.getUUIDBelongingTo(),
                home.getWorld(),
                home.getMaterial(),
                home.getName(),
                home.getDescription(),
                home.getX(),
                home.getY(),
                home.getZ(),
                home.getPitch(),
                home.getYaw()
        );
    }

    public List<Home> getPlayersHomes(String playerUUID) {
        List<Home> playerHomes = new ArrayList<>();

        String sql = "select * from players_homes where player_uuid = ?";
        ResultSet rs = DatabaseUtil.fetch(db, sql, playerUUID);

        if (rs == null) return playerHomes;

        try {
            while (rs.next()) {
                Location homeLocation = new Location(
                        Bukkit.getWorld(UUID.fromString(rs.getString("world"))),
                        rs.getDouble("x"),
                        rs.getDouble("y"),
                        rs.getDouble("z"),
                        rs.getFloat("yaw"),
                        rs.getFloat("pitch")
                );
                Home home = new Home(
                        rs.getString("player_uuid"),
                        rs.getString("material"),
                        homeLocation,
                        rs.getString("name"),
                        rs.getString("description")
                );
                playerHomes.add(home);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return playerHomes;
    }

    public boolean deleteHome(String playerUUID, String homeName){
        String sql = "delete from players_homes where player_uuid = ? and name = ?";
        return DatabaseUtil.execute(db, sql, playerUUID, homeName);
    }
}
