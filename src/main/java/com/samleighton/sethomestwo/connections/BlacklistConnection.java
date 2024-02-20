package com.samleighton.sethomestwo.connections;

import com.samleighton.sethomestwo.SetHomesTwo;
import com.samleighton.sethomestwo.utils.DatabaseUtil;
import org.bukkit.Bukkit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlacklistConnection extends AbstractConnection {
    private final String tableName = "blacklist";
    private final List<String> validDimensions = new ArrayList<>() {
        {
            Bukkit.getWorlds().forEach(world -> add(world.getName().toLowerCase()));
        }
    };

    // Mapping the environment grabbed from player to our valid dimension list
    private final Map<String, String> dimensionsMap = new HashMap<>() {{
        put("NORMAL", validDimensions.get(0));
        put("NETHER", validDimensions.get(1));
        put("THE_END", validDimensions.get(2));
    }};

    public BlacklistConnection() {
        super(SetHomesTwo.getPlugin(SetHomesTwo.class).getConnectionManager().getConnection("homes"));
    }

    /**
     * Create the blacklist table if it does not exist
     */
    @Override
    public void init() {
        if (this.conn() == null) return;

        // Create blacklist table
        String createBlacklistSQL = "create table if not exists %s (\n" +
                "id integer PRIMARY KEY, \n" +
                "dimension_name TEXT NOT NULL \n" +
                ");";
        DatabaseUtil.execute(this.conn(), String.format(createBlacklistSQL, tableName));
    }

    /**
     * Add blacklist to the database
     *
     * @param dimensionNames, List of the dimensions that are to be blacklisted
     * @return boolean
     */
    public boolean addToBlacklistTable(List<String> dimensionNames) {
        StringBuilder stringBuilder = new StringBuilder();
        String sql = "insert into %s (dimension_name) VALUES ";

        for (int i = 0; i < dimensionNames.size(); i++) {
            stringBuilder.append("(?),");
        }
        sql += stringBuilder.substring(0, stringBuilder.length() - 1) + ";";

        return DatabaseUtil.execute(
                this.conn(),
                String.format(sql, tableName),
                dimensionNames
        );
    }

    public boolean removeFromBacklistTable(List<String> dimensionNames) {
        StringBuilder stringBuilder = new StringBuilder();
        String sql = "delete from %s where dimension_name IN (";

        for (int i = 0; i < dimensionNames.size(); i++) {
            stringBuilder.append("(?),");
        }
        sql += stringBuilder.substring(0, stringBuilder.length() - 1) + ");";

        return DatabaseUtil.execute(
                this.conn(),
                String.format(sql, tableName),
                dimensionNames
        );
    }

    /**
     * Retrieve a list of the dimensions that are blacklisted.
     *
     * @return List<String>
     */
    public List<String> getBlacklistedDimensions() {
        List<String> blacklistedDimensions = new ArrayList<>();

        String sql = "select * from %s";
        ResultSet rs = DatabaseUtil.fetch(this.conn(), String.format(sql, tableName));

        if (rs == null) return blacklistedDimensions;

        try {
            while (rs.next()) {
                blacklistedDimensions.add(rs.getString("dimension_name"));
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("There was an issue retrieving blacklisted dimensions");
        }

        return blacklistedDimensions;
    }

    /**
     * Retrieve a list of the server's valid dimensions.
     *
     * @return List
     */
    public List<String> getValidDimensions() {
        return this.validDimensions;
    }

    /**
     * Retrieve dimensions mapping.
     *
     * @return Map<String, String>
     */
    public Map<String, String> getDimensionsMap() {
        return this.dimensionsMap;
    }
}
