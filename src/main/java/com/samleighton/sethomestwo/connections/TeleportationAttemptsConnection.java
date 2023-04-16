package com.samleighton.sethomestwo.connections;

import com.samleighton.sethomestwo.SetHomesTwo;
import com.samleighton.sethomestwo.models.TeleportAttempt;
import com.samleighton.sethomestwo.utils.DatabaseUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

public class TeleportationAttemptsConnection extends AbstractConnection {

    private final String tableName = "player_teleport_attempts";

    public TeleportationAttemptsConnection() {
        super(SetHomesTwo.getPlugin(SetHomesTwo.class).getConnectionManager().getConnection("homes"));
    }

    @Override
    public void init() {
        if (this.conn() == null) return;

        String createSQL = "create table if not exists %s (\n" +
                "id integer PRIMARY KEY, \n" +
                "player_uuid TEXT NOT NULL UNIQUE, \n" +
                "world TEXT NOT NULL, \n" +
                "x real NOT NULL, \n" +
                "y real NOT NULL, \n" +
                "z real NOT NULL \n" +
                ");";
        DatabaseUtil.execute(this.conn(), String.format(createSQL, tableName));
    }

    /**
     * Create a new attempt in the table.
     *
     * @param teleportAttempt, The teleport attempt to create
     * @return boolean
     */
    public boolean createAttempt(TeleportAttempt teleportAttempt) {
        String sql = "insert into %s (player_uuid, world, x, y, z) VALUES (?, ?, ?, ?, ?);";
        return DatabaseUtil.execute(
                this.conn(),
                String.format(sql, tableName),
                teleportAttempt.getPlayer().getUniqueId().toString(),
                Objects.requireNonNull(teleportAttempt.getLocation().getWorld()).getUID().toString(),
                teleportAttempt.getLocation().getX(),
                teleportAttempt.getLocation().getY(),
                teleportAttempt.getLocation().getZ()
        );
    }

    /**
     * Retrieve an attempt for a player.
     *
     * @param player, The player to retrieve the attempt for
     * @return Timestamp[], first element is the started at time, second element is the last moved time
     */
    public TeleportAttempt getLastAttempt(Player player) {
        String sql = String.format("select * from %s where player_uuid = ?", tableName);
        ResultSet rs = DatabaseUtil.fetch(this.conn(), sql, player.getUniqueId().toString());

        // Guard to check if received a result set
        if (rs == null) return null;

        try {
            // Guard to check if the result set has rows
            if (!rs.next()) {
                return null;
            }

            // Create the teleport attempt obj
            TeleportAttempt ta = new TeleportAttempt(
                    player,
                    new Location(
                            Bukkit.getWorld(UUID.fromString(rs.getString("world"))),
                            rs.getDouble("x"),
                            rs.getDouble("y"),
                            rs.getDouble("z")
                    )
            );

            // Check if player can still teleport
            Location teleportStart = ta.getLocation();
            Location currLocation = player.getLocation();

            // Guard to check if player has moved
            if(teleportStart.getX() != currLocation.getX() || teleportStart.getY() != currLocation.getY() || teleportStart.getZ() != currLocation.getZ())
                ta.setCanTeleport(false);

            return ta;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Delete an attempt from the table.
     *
     * @param player, The player to delete the attempt for.
     * @return boolean
     */
    public boolean removeAttempt(Player player) {
        return DatabaseUtil.execute(this.conn(), String.format("delete from %s where player_uuid = ?", tableName), player.getUniqueId().toString());
    }
}
