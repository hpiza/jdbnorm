package edu.iteso.database;

import edu.iteso.normalization.Key;
import edu.iteso.normalization.Row;
import edu.iteso.normalization.Table;

public class MysqlScript extends DbScript {

    public String createDatabase(String dbName) {
        return String.format("DROP DATABASE IF EXISTS %s;\nCREATE DATABASE %s;\nUSE %s;", dbName, dbName, dbName);
    }

    public String createTable(String name, String field0, Datatype datatype0, int size0) {
        String type = switch (datatype0) {
            case Integer -> "INTEGER";
            case Real -> "DECIMAL";
            default -> String.format("VARCHAR(%d)", size0);
        };
        return String.format("CREATE TABLE %s(%s %s);", name, field0, type);
    }

    @Override
    public boolean fieldsAreDeclared() {
        return true;
    }

    @Override
    public String addPrimaryKey(String tableName, Key primaryKey) {
        return String.format("ALTER TABLE %s ADD PRIMARY KEY(%s);", tableName, primaryKey.toString());
    }

    @Override
    public String addFields(String tableName, String fieldName, Datatype datatype, int fieldSize) {
        String typeName = switch (datatype) {
            case Integer -> "INTEGER";
            case Real -> "DECIMAL";
            default -> String.format("VARCHAR(%d)", fieldSize);
        };
        return String.format("ALTER TABLE %s ADD COLUMN %s %s;", tableName, fieldName, typeName);
    }

    @Override
    public String addForeignKey(String tableName, Key foreignKey, String foreignTableName) {
        return String.format("ALTER TABLE %s ADD FOREIGN KEY(%s) REFERENCES %s(%s);", tableName, foreignKey, foreignTableName, foreignKey);
    }

    @Override
    public String insertData(Table table, Row row, boolean[] textTypeFields) {
        StringBuilder valuesBuilder = new StringBuilder();
        for(int i = 0; i < row.size(); i ++) {
            String value = row.get(i);
            if(textTypeFields[i]) valuesBuilder.append(String.format("\"%s\", ", value));
            else valuesBuilder.append(String.format("%s, ", value));
        }
        String values = valuesBuilder.toString();
        values = values.substring(0, values.length() - 2);
        return String.format("INSERT INTO %s VALUES(%s);", table.getName(), values);
    }
}
