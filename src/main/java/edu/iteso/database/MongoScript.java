package edu.iteso.database;

import edu.iteso.normalization.Key;
import edu.iteso.normalization.Row;
import edu.iteso.normalization.Table;

public class MongoScript extends DbScript {

    @Override
    public String createDatabase(String dbName) {
        return String.format("db.createCollection(%s)", dbName);
    }

    @Override
    public String createTable(String name, String field0, Datatype datatype0, int size0) {
        return "";
    }

    @Override
    public boolean fieldsAreDeclared() {
        return false;
    }

    @Override
    public String insertData(Table table, Row row, boolean[] textTypeFields) {
        StringBuilder str = new StringBuilder(String.format("db.%s.insertOne({", table.getName()));
        for(int i = 0; i < row.size(); i ++) {
            str.append(String.format("\"%s\": \"%s\"", table.getFieldName(i), row.get(i)));
            if(i < row.size() - 1) str.append(", ");
        }
        return str + "})";
    }

    @Override
    public String addPrimaryKey(String tableName, Key primaryKey) {
        return "";
    }

    @Override
    public String addFields(String tableName, String fieldName, Datatype datatype, int fieldSize) {
        return "";
    }

    @Override
    public String addForeignKey(String tableName, Key foreignKey, String foreignTableName) {
        return "";
    }
}
