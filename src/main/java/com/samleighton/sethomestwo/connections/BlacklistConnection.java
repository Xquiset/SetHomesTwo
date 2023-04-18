package com.samleighton.sethomestwo.connections;

import com.samleighton.sethomestwo.SetHomesTwo;
import com.samleighton.sethomestwo.models.Home;
import com.samleighton.sethomestwo.utils.DatabaseUtil;
import org.bukkit.Location;

import java.util.List;

public class BlacklistConnection extends AbstractConnection{
    private final String tableName = "blacklist";

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
                "dimension_id TEXT NOT NULL, \n" +
                ");";
        DatabaseUtil.execute(this.conn(), String.format(createBlacklistSQL, tableName));
    }

    /**
     * Add blacklist to the database
     *
     * @param dimensionNames,     List of the dimensions that are to be blacklisted
     * @return boolean
     */
    public boolean addToBlacklistTable(List<String> dimensionNames) {
        for (int i = 0; i < dimensionNames.size(); i++) {
            String sql = "insert into %s (dimension_name) VALUES (?);";
            return DatabaseUtil.execute(
                    this.conn(),
                    String.format(sql, tableName),
                    dimensionNames.get(i)
            );
        }
        return false;
    }
}
