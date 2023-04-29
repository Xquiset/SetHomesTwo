package com.samleighton.sethomestwo.connections;

import com.samleighton.sethomestwo.SetHomesTwo;
import com.samleighton.sethomestwo.models.Home;
import com.samleighton.sethomestwo.utils.DatabaseUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HomesConnection extends AbstractConnection {

    private final String tableName = "players_homes";

    public HomesConnection() {
        super(SetHomesTwo.getPlugin(SetHomesTwo.class).getConnectionManager().getConnection("homes"));
    }

    /**
     * Create the homes table if it does not exist.
     */
    public void init() {
        if (this.conn() == null) return;

        // Create players_homes table
        String createPlayersHomesSQL = "create table if not exists %s (\n" +
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
                "yaw real NOT NULL, \n" +
                "dimension TEXT" +
                ");";
        DatabaseUtil.execute(this.conn(), String.format(createPlayersHomesSQL, tableName));
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
    public boolean createNewHome(String playerUUID, String material, Location playerLocation, String name, String description, String dimension) {
        Home home = new Home(playerUUID, material, playerLocation, name, description, dimension);
        String sql = "insert into %s (player_uuid, world, material, name, description, x, y, z, pitch, yaw, dimension) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        return DatabaseUtil.execute(
                this.conn(),
                String.format(sql, tableName),
                home.getUUIDBelongingTo(),
                home.getWorld(),
                home.getMaterial(),
                home.getName(),
                home.getDescription(),
                home.getX(),
                home.getY(),
                home.getZ(),
                home.getPitch(),
                home.getYaw(),
                home.getDimension()
        );
    }

    /**
     * Retrieve a list of the players current homes from the database.
     *
     * @param playerUUID, The player uuid to retrieve homes for
     * @return List<Home>
     */
    public List<Home> getPlayersHomes(String playerUUID) {
        List<Home> playerHomes = new ArrayList<>();

        String sql = "select * from %s where player_uuid = ?";
        ResultSet rs = DatabaseUtil.fetch(this.conn(), String.format(sql, tableName), playerUUID);

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
                        rs.getString("description"),
                        rs.getString("dimension")
                );
                playerHomes.add(home);
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("There was an issue reading homes for player " + playerUUID);
            e.printStackTrace();
        }

        return playerHomes;
    }

    /**
     * Delete the home from the database.
     *
     * @param playerUUID, The player uuid who owns the home
     * @param homeName,   The home name to delete
     * @return boolean
     */
    public boolean deleteHome(String playerUUID, String homeName) {
        List<Home> homes = getPlayersHomes(playerUUID);
        for (Home home : homes) {
            if (home.getName().equalsIgnoreCase(homeName)) {
                String sql = "delete from %s where player_uuid = ? and name = ?";
                return DatabaseUtil.execute(this.conn(), String.format(sql, tableName), playerUUID, homeName);
            }
        }

        return false;
    }
}
