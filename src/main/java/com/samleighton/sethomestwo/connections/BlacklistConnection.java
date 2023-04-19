package com.samleighton.sethomestwo.connections;

import com.samleighton.sethomestwo.SetHomesTwo;
import com.samleighton.sethomestwo.utils.DatabaseUtil;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class BlacklistConnection extends AbstractConnection {
    private final String tableName = "blacklist";
    private final List<String> validDimensions = new ArrayList<String>() {
        {
            Bukkit.getWorlds().forEach(world -> add(world.getName().toLowerCase()));
        }
    };

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

    /**
     * Retrieve a list of the servers valid dimensions.
     *
     * @return List
     */
    public List<String> getValidDimensions() {
        return this.validDimensions;
    }
}
