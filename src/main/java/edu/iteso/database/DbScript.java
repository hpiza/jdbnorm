package edu.iteso.database;

import edu.iteso.normalization.Database;
import edu.iteso.normalization.Key;
import edu.iteso.normalization.Row;
import edu.iteso.normalization.Table;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public abstract class DbScript {

    public void createDatabase(Database db, String dbName, String scriptFileName) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(scriptFileName));
        bw.append(createDatabase(dbName));
        bw.newLine();
        bw.newLine();
        for(Table table: db) {
            table.discoverDatatypes();
            String ct = createTable(table.getName(), table.getFieldName(0), table.getFieldDatatype(0), table.getFieldSize(0));
            if(!ct.equals("")) {
                bw.append(createTable(table.getName(), table.getFieldName(0), table.getFieldDatatype(0), table.getFieldSize(0)));
                bw.newLine();
            }
            if(fieldsAreDeclared()) {
                for(int c = 1; c < table.columns(); c ++) {
                    String fieldName = table.getFieldName(c);
                    Datatype datatype = table.getFieldDatatype(c);
                    int size = table.getFieldSize(c);
                    bw.append(addFields(table.getName(), fieldName, datatype, size));
                    bw.newLine();
                }
                bw.append(addPrimaryKey(table.getName(), table.getPrimaryKey()));
                bw.newLine();
                for(Key foreignKey: table.getForeignKeys()) {
                    String foreignTableName = table.getForeignTableName(foreignKey);
                    bw.append(addForeignKey(table.getName(), foreignKey, foreignTableName));
                    bw.newLine();
                }
                bw.newLine();
            }
        }
        for(Table table: db) {
            boolean[] textTypeFields = new boolean[table.columns()];
            for(int c = 0; c < table.columns(); c ++) textTypeFields[c] = table.getFieldDatatype(c) == Datatype.Text;
            for(Row row: table) {
                bw.append(insertData(table, row, textTypeFields));
                bw.newLine();
            }
            bw.newLine();
        }
        bw.close();
    }
    public abstract String createDatabase(String dbName);

    public abstract String createTable(String name, String field0, Datatype datatype0, int size0);

    public abstract boolean fieldsAreDeclared();

    public abstract String addPrimaryKey(String tableName, Key primaryKey);

    public abstract String addFields(String tableName, String fieldName, Datatype datatype, int fieldSize);

    public abstract String addForeignKey(String tableName, Key foreignKey, String foreignTableName);

    public abstract String insertData(Table table, Row row, boolean[] textTypeFields);
}
