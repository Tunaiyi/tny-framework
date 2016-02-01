package com.tny.game.suite.cache;

public class ShardTable {

    private int sid;

    private String table;

    public static ShardTable get(int sid) {
        return new ShardTable(sid, "");
    }

    public static ShardTable get(int sid, String table) {
        return new ShardTable(sid, table);
    }

    private ShardTable(int sid, String table) {
        this.sid = sid;
        this.table = table;
    }

    public int getSid() {
        return this.sid;
    }

    public String getTable() {
        return this.table;
    }

}
