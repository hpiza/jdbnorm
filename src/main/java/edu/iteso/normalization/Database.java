package edu.iteso.normalization;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Database implements Iterable<Table> {
    private final Map<String, Table> nameTableMap = new HashMap<>();

    private final List<Table> tableList = new LinkedList<>();

    public Database(Table ... tables) { addAll(tables); }

    @Override
    public Iterator<Table> iterator() {
        return this.tableList.iterator();
    }

    public void add(Table table, boolean insertFirst) {
        if(nameTableMap.containsKey(table.getName())) return;
        nameTableMap.put(table.getName(), table);
        if(insertFirst) tableList.add(0, table);
        else tableList.add(table);
    }

    public void addAll(Table ... tables) {
        for(Table t: tables) add(t, false);
    }

    public void add(Database db) {
        for(Table t: db) add(t, false);
    }

    public int size() {
        return nameTableMap.size();
    }

    public boolean containsTable(String tableName) {
        return this.nameTableMap.containsKey(tableName);
    }

    public Table getTable(String tableName) {
        if(!this.nameTableMap.containsKey(tableName)) throw new RuntimeException(String.format("Table %s does not exist", tableName));
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

    public static void toCsvFiles(Database database, String folderName) {
        try {
            Path folderPath = Paths.get(folderName);
            if(!Files.exists(folderPath)) Files.createDirectory(folderPath);
            for(Table table: database) Table.toCsvFile(folderPath, table);
        } catch(IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
