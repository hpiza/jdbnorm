package edu.iteso.normalization;

import java.util.*;

public class Database implements Iterable<Table> {

    private final Map<String, Table> nameTableMap = new HashMap<>();

    public Database(Table ... tables) { addAll(tables); }

    @Override
    public Iterator<Table> iterator() {
        return this.nameTableMap.values().iterator();
    }

    public void add(Table table) {
        if(nameTableMap.containsKey(table.getName())) return;
        nameTableMap.put(table.getName(), table);
    }

    public void addAll(Table ... tables) {
        for(Table t: tables) add(t);
    }

    public void add(Database db) {
        for(Table t: db) add(t);
    }

    public int size() {
        return nameTableMap.size();
    }

    public boolean containsTable(String tableName) {
        return this.nameTableMap.containsKey(tableName);
    }

    public Table getTable(String tableName) {
        return this.nameTableMap.get(tableName);
    }

    public Table findTableByKey(Key key) {
        for(Table table: this.nameTableMap.values()) {
            if(table.getPrimaryKey().equals(key))
                return table;
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(Table t: this.nameTableMap.values()) sb.append(t.toString());
        return sb.toString();
    }

}
