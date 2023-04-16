package com.samleighton.sethomestwo.connections;

import java.sql.Connection;

public abstract class AbstractConnection {

    private final Connection db;

    public AbstractConnection(Connection db) {
        this.db = db;
    }

    public abstract void init();

    protected Connection conn() {
        return this.db;
    }
}
