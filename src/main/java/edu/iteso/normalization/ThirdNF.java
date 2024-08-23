package edu.iteso.normalization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThirdNF extends SecondNF {

    public Database normalize(Table inputTable) {
        if(isNormalized(inputTable).isNormalized) return new Database(inputTable);
        Database db2FN = super.normalize(inputTable);
        Database dbToReturn = new Database();
        for(Table tb2FN: db2FN) {
            Database db3FN = normalize1(tb2FN);
            if(db3FN.size() == 1) {
                dbToReturn.add(tb2FN, false);
            } else {
                for (Table tb3FN : db3FN) {
                    Database db = normalize(tb3FN);
                    dbToReturn.add(db);
                }
            }
        }
        return dbToReturn;
    }

    public Database normalize1(Table inputTable) {
        if(isNormalized(inputTable).isNormalized) return new Database(inputTable);
        Database db2FN = NormalizerFactory.getSecondNF().normalize(inputTable);
        Database db3FN = new Database();
        for(Table table2FN: db2FN) {
            Key pk = table2FN.getPrimaryKey();
            boolean[] removeField = new boolean[table2FN.columns()];
            boolean foundDependency = false;
            for(int f = 0; f < table2FN.columns(); f ++) {
                if(pk.contains(table2FN.getFieldName(f))) continue;
                for(int g = f + 1; g < table2FN.columns(); g ++) {
                    if(pk.contains(table2FN.getFieldName(g))) continue;
                    boolean fDefinesG = getDependencyCalculator().isDependent(table2FN, f, g);
                    boolean gDefinesF = getDependencyCalculator().isDependent(table2FN, g, f);
                    if(!fDefinesG && !gDefinesF) continue;
                    foundDependency = true;
                    // Si f define a g no importa que g también define a f, f gana por estar a la izquierda
                    int key = f, notKey = g;
                    if(!fDefinesG) {
                        key = g;
                        notKey = f;
                    }
                    removeField[notKey] = true;
//				Crear la tabla que registre la dependencia encontrada a una no-clave
//				O en su caso, obtener la tabla (ya creada) y añadir la nueva dependencia a la no-clave
                    String keyField = table2FN.getFieldName(key);
                    String notKeyField = table2FN.getFieldName(notKey);
                    Key newKey = new Key(keyField);
                    Table table3FN = db3FN.findTableByKey(newKey);
                    if(table3FN == null) {
                        table3FN = new Table("Catalog_" + keyField);
                        table3FN.addField(keyField);
                        table3FN.addField(notKeyField);
                        table3FN.setPrimaryKey(newKey);
                        for(Row row: table2FN) {
                            Row row1 = new Row();
                            row1.add(row.get(key));
                            row1.add(row.get(notKey));
                            table3FN.addRow(row1);
                        }
                        table2FN.addForeignKey(newKey, table3FN.getName());
                        db3FN.add(table3FN, true);
                    } else {
                        table3FN.addField(notKeyField);
                        Map<String, String> keyToNotKey = new HashMap<>();
                        for(Row row: table2FN) {
                            String keyValue = row.get(key);
                            String notKeyValue = row.get(notKey);
                            keyToNotKey.put(keyValue, notKeyValue);
                        }
                        for(Row row: table3FN) {
                            String keyValue = row.get(0);
                            String notKeyValue = keyToNotKey.get(keyValue);
                            row.add(notKeyValue);
                        }
                    }
                }
            }

//		Eliminar las columnas de la tabla original que dependieron de otra columna no clave
            if(foundDependency) {
                Table table3FN = new Table(table2FN.getName());
                for(int f = 0; f < table2FN.columns(); f ++) {
                    if(removeField[f]) continue;
                    String field = table2FN.getFieldName(f);
                    table3FN.addField(field);
                }
                for(int r = 0; r < table2FN.rows(); r ++) {
                    Row row = table2FN.getRow(r);
                    Row row1 = new Row();
                    for(int f = 0; f < row.size(); f ++) {
                        if(removeField[f]) continue;
                        row1.add(row.get(f));
                    }
                    table3FN.addRow(row1);
                }
                for(Key fkey: table2FN.getForeignKeys()) {
                    int f = table2FN.getFieldIndex(fkey.toString());
                    if(!removeField[f]) table3FN.addForeignKey(fkey, table2FN.getForeignTableName(fkey));
                }
                db3FN.add(table3FN, false);
                table3FN.setPrimaryKey(table2FN.getPrimaryKey());
            } else {
                db3FN.add(table2FN, false);
            }
        }
        return db3FN;
    }

    @Override
    public NormalizerResult isNormalized(Table table) {
        if(!super.isNormalized(table).isNormalized) {
            return new NormalizerResult(false, List.of("Table is not in Second Normal Form"));
        }
        boolean isNormalized = true;
        List<String> errorList = new ArrayList<>();
        for(int col1 = 0; col1 < table.columns(); col1 ++) {
            String field1 = table.getFieldName(col1);
            if(table.getPrimaryKey().contains(field1)) continue;
            for(int col2 = 0; col2 < table.columns(); col2 ++) {
                if(col1 == col2) continue;
                String field2 = table.getFieldName(col2);
                if(table.getPrimaryKey().contains(field2)) continue;
                if(getDependencyCalculator().isDependent(table, col1, col2)) {
                    isNormalized = false;
                    errorList.add(String.format("Field %s is defined by non-key field %s", field2, field1));
                }
            }
        }
        return new NormalizerResult(isNormalized, errorList);
    }
}