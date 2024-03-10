package com.samleighton.sethomestwo.dao;

import com.samleighton.sethomestwo.utils.DatabaseUtil;
import org.bukkit.Bukkit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BlacklistDao extends SQLiteDao implements Dao<String> {
    private final String TABLE_NAME = "blacklist";

    public BlacklistDao() {
        super();
    }

    @Override
    public List<String> getAll(Object... keys) {
        List<String> blacklistedDimensions = new ArrayList<>();

        String sql = "select * from %s";
        ResultSet rs = DatabaseUtil.fetch(this.conn, String.format(sql, TABLE_NAME));

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

    @Override
    public String get(Object... keys) {
        String dimKey = null;
        for (Object key : keys) {
            if (key instanceof String) dimKey = (String) key;
        }

        // Key conversion guard
        if (dimKey == null) return null;

        String sql = "select * from %s where dimension_name = ?";
        ResultSet rs = DatabaseUtil.fetch(this.conn, String.format(sql, TABLE_NAME), dimKey);

        if (rs == null) {
            Bukkit.getLogger().severe("There was an error attempting to fetch blacklisted dimension entry.");
            return null;
        }

        String dimension = null;
        try {
            while (rs.next()) {
                dimension = rs.getString("dimension_name");
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("There was an error attempting to fetch blacklisted dimension entry.");
        }

        return dimension;
    }

    @Override
    public boolean save(Object object) {
        // Blacklist key guard
        if (!(object instanceof String)) return false;

        String sql = "insert into %s (dimension_name) values (?)";
        return DatabaseUtil.execute(this.conn, String.format(sql, TABLE_NAME), object);
    }

    @Override
    public boolean delete(Object object) {
        // Blacklist key guard
        if (!(object instanceof String)) return false;

        String sql = "delete from %s where dimension_name = ?";
        return DatabaseUtil.execute(this.conn, String.format(sql, TABLE_NAME), object);
    }
}
