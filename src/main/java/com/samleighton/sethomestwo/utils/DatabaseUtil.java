package com.samleighton.sethomestwo.utils;

import org.bukkit.Bukkit;

import java.sql.*;

public class DatabaseUtil {

    /**
     * Execute a query on the database.
     *
     * @param connection, The connection to execute this query on.
     * @param sql,        The query to execute.
     * @param params,     Parameters to bind to the statement.
     * @return boolean
     */
    public static boolean execute(Connection connection, String sql, Object... params) {
        try {
            PreparedStatement statement = connection.prepareStatement(sql);

            // Set optional params and execute
            setParams(statement, params);
            statement.execute();
            return true;
        } catch (SQLException e) {
            Bukkit.getLogger().warning("Could not execute sql statement.");
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Fetch a set of results from the database.
     *
     * @param connection, The database connection to fetch from
     * @param sql,        The sql string used for querying
     * @param params,     Optional sql parameters
     * @return ResultSet | null
     */
    public static ResultSet fetch(Connection connection, String sql, Object... params) {
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            setParams(statement, params);

            return statement.executeQuery();
        } catch (SQLException e) {
            Bukkit.getLogger().warning("Could not execute sql statement.");
        }

        return null;
    }

    /**
     * Attempts to bind values with their respective placeholders in a prepared sql statement.
     *
     * @param statement, The statement to bind with
     * @param params,    The parameters to bind
     */
    public static void setParams(PreparedStatement statement, Object... params) {
        if (params.length == 0) return;

        try {
            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                int paramIndex = i + 1;

                if (param == null) {
                    statement.setNull(paramIndex, Types.NULL);
                }

                if (param instanceof Integer) {
                    statement.setInt(paramIndex, (int) param);
                }

                if (param instanceof String) {
                    statement.setString(paramIndex, (String) param);
                }

                if (param instanceof Double) {
                    statement.setDouble(paramIndex, (double) param);
                }

                if (param instanceof Float) {
                    statement.setFloat(paramIndex, (float) param);
                }
            }

            Bukkit.getLogger().info("STMT: " + statement.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
