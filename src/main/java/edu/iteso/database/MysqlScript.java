package edu.iteso.database;

import edu.iteso.normalization.Database;
import edu.iteso.normalization.Key;
import edu.iteso.normalization.Row;
import edu.iteso.normalization.Table;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class MysqlScript implements DbScript {

    @Override
    public void createDatabase(Database db, String name) {
        try {
            FileWriter fw = new FileWriter(name + ".script");
            BufferedWriter bw = new BufferedWriter(fw);
            bw.append(String.format("CREATE DATABASE IF NOT EXISTS %s;", name));
            bw.newLine();
            bw.append(String.format("USE %s;", name));
            bw.newLine();
            bw.newLine();
            for (Table table : db) createTable(table, bw);
            bw.newLine();
            for (Table table : db) {
                populateTable(table, bw);
                bw.newLine();
            }
            bw.close();
        } catch(IOException ex) {
        }
    }

    private void createTable(Table table, BufferedWriter bw) throws IOException {
        table.discoverDatatypes();
        bw.append(String.format("CREATE TABLE IF NOT EXISTS %s(", table.getName()));
        for (int i = 0; i < table.columns(); i++) {
            String field = table.getFieldName(i);
            switch(table.getFieldDatatype(i)) {
                case Integer: bw.append(String.format("%s INTEGER, ", field)); break;
                case Real: bw.append(String.format("%s DECIMAL, ", field)); break;
                default: bw.append(String.format("%s VARCHAR(%d), ", field, table.getFieldSize(i)));
            }
        }
        String primaryKey = "PRIMARY KEY(";
        for(String field: table.getPrimaryKey()) primaryKey += field + ", ";
        primaryKey = primaryKey.substring(0, primaryKey.length() - 2) + ")";
        bw.append(primaryKey);
        for(Key foreignKey: table.getForeignKeys()) {
            String tableName = table.getForeignTableName(foreignKey);
            bw.append(String.format(", FOREIGN KEY (%s)", foreignKey.toString()));
            bw.append(String.format(" REFERENCES %s(%s)", tableName, foreignKey.toString()));
        }
        bw.append(");");
        bw.newLine();
    }

    private void populateTable(Table table, BufferedWriter bw) throws IOException {
        String fields = "";
        for (int i = 0; i < table.columns(); i++) {
            String field = table.getFieldName(i);
            fields += field + ", ";
        }
        fields = fields.substring(0, fields.length() - 2);
        for(Row row: table) {
            bw.append(String.format("INSERT INTO %s(%s) VALUES(", table.getName(), fields));
            String values = "";
            for(int i = 0; i < row.size(); i ++) {
                String value = row.get(i);
                if(table.getFieldDatatype(i) == Datatype.Text) values += String.format("\"%s\", ", value);
                else values += String.format("%s, ", value);
            }
            values = values.substring(0, values.length() - 2) + ");";
            bw.append(values);
            bw.newLine();
        }
    }
}
