package edu.iteso.normalization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
public class NormalizeUtils {

    public static boolean defines(int i1, int i2, Table table) {
        // if(table.isMarked(i1)) return false;
        HashMap<String, String> valueMap = new HashMap<>();
        for(Row row: table) {
            String value1 = row.get(i1);
            String value2 = row.get(i2);
            if (valueMap.containsKey(value1)) {
                String v2 = valueMap.get(value1);
                if (!value2.equals(v2))
                    return false;
            } else {
                valueMap.put(value1, value2);
            }
        }
        return true;
    }
    public static boolean defines(Set<Integer> iSet, int i2, Table table) {
        HashMap<String, String> valueMap = new HashMap<>();
        for(Row row: table) {
            String valuesi = "";
            for (Integer i1 : iSet) valuesi += row.get(i1) + "\n";
            String value2 = row.get(i2);
            if (valueMap.containsKey(valuesi)) {
                String v2 = valueMap.get(valuesi);
                if (!value2.equals(v2))
                    return false;
            } else {
                valueMap.put(valuesi, value2);
            }
        }
        return true;
    }

    /*
    private static Table to1FN(Table table) {
        Table table1FN = new Table(table.getName() + "_1FN");
//		Añadir a la nueva tabla todos los campos con título de la tabla origen
//		y registrar el inicio y el fin de cada atributo multivaluado
        int first = -1;
        int end = -1;
        ArrayList<Pair<Integer, Integer>> multiValuedAtt = new ArrayList<Pair<Integer, Integer>>();
        for(int f = 0; f < table.columns(); f ++) {
            if(table.isFieldUntitled(f)) {
                if(first < 0) {
                    first = f - 1;
                    end = -1;
                }
            }
            else {
                if(first >= 0) {
                    end = f - 1;
                    multiValuedAtt.add(new IntPair(first, end));
                    first = -1;
                }
                table1FN.addField(table.getFieldName(f));
            }
        }
        if(first >= 0 && end < 0) multiValuedAtt.add(new IntPair(first, table.columns() - 1));

        for(Row currentRow: table) {
            if(multiValuedAtt.isEmpty()) {
                table1FN.addRow(currentRow.clone());
                continue;
            }
            Row newRow = new Row();
            for(int i = 0; i < table.columns(); i ++) {
                if(!table.isFieldUntitled(i)) newRow.add(currentRow.get(i));
            }
            table1FN.addRow(newRow);
            int firstIndex = table1FN.rows() - 1;
            int targetCol = multiValuedAtt.get(0).getFirst();
            for(int mv = 0; mv < multiValuedAtt.size(); mv ++) {
                int startMV = multiValuedAtt.get(mv).getFirst();
                int endMV   = multiValuedAtt.get(mv).getSecond();
                if(mv > 0) targetCol += startMV - multiValuedAtt.get(mv - 1).getSecond();
                int lastIndex = table1FN.rows() - 1;
                for(int i = startMV + 1; i <= endMV; i ++) {
                    String value = currentRow.get(i);
                    if(value == null || value.equals("")) continue;
                    for(int j = firstIndex; j <= lastIndex; j ++) {
                        Row veryCurrentRow = table1FN.get(j);
                        Row veryNewRow = new Row();
                        for(int k = 0; k < table1FN.columns(); k ++) {
                            if(k == targetCol) veryNewRow.add(value);
                            else veryNewRow.add(veryCurrentRow.get(k));
                        }
                        table1FN.addRow(veryNewRow);
                    }
                }
            }
        }

        findPrimaryKey(table1FN);
        return table1FN;
    }*/

    private static void addDependency(Key rootKey, String field, Map<Key, List<String>> dependencies, Map<Key, List<String>> dependenciesToPK) {
        List<String> list = dependenciesToPK.get(rootKey);
        if(list == null) list = new ArrayList<>();
        list.add(field);
        dependenciesToPK.put(rootKey, list);
        for(Entry<Key, List<String>> d: dependencies.entrySet()) {
            Key key = d.getKey();
            if(key.size() == 1 && key.contains(field)) {
                for(String f : d.getValue()) addDependency(rootKey, f, dependencies, dependenciesToPK);
            }
        }
    }

    /*
    private static Set<Table> to3FN(Set<Table> db2FN) {
        Set<Table> db3FN = new HashSet<>();
        for(Table table: db2FN) {
            Set<Table> db = to3FN(table);
            for(Table t: db) db3FN.add(t);
        }
        return db3FN;
    }
    private static Set<Table> to3FN(Table table2FN) {
        Set<Table> db3FN = new HashSet<>();
        Key pk = table2FN.getPrimaryKey();
        boolean[] removeField = new boolean[table2FN.columns()];
        boolean foundDependency = false;
        for(int f = 0; f < table2FN.columns(); f ++) {
            if(pk.contains(table2FN.getFieldName(f))) continue;
            for(int g = f + 1; g < table2FN.columns(); g ++) {
                if(pk.contains(table2FN.getFieldName(g))) continue;
                boolean fg = defines(f, g, table2FN);
                boolean gf = defines(g, f, table2FN);
                if(fg == gf) continue;

                foundDependency = true;
                int key    = fg? f: g;
                int notKey = fg? g: f;
                removeField[notKey] = true;

//				Crear la tabla que registre la dependencia encontrada a una no-clave
//				O en su caso, obtener la tabla (ya creada) y añadir la nueva dependencia a la no-clave
                String keyField = table2FN.getFieldName(key);
                String notKeyField = table2FN.getFieldName(notKey);
                Key newKey = new Key(keyField);

                Table table3FN = findTable(db3FN, newKey);
                if(table3FN == null) {
                    table3FN = new Table("" + keyField);
                    table3FN.addField(keyField);
                    table3FN.addField(notKeyField);
                    table3FN.setPrimaryKey(newKey);
                    for(Row row: table2FN) {
                        Row row1 = new Row();
                        row1.add(row.get(key));
                        row1.add(row.get(notKey));
                        table3FN.addRow(row1);
                    }
                    db3FN.add(table3FN);
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
                Row row = table2FN.get(r);
                Row row1 = new Row();
                for(int f = 0; f < row.size(); f ++) {
                    if(removeField[f]) continue;
                    row1.add(row.get(f));
                }
                table3FN.addRow(row1);
            }
            db3FN.add(table3FN);
            table3FN.setPrimaryKey(table2FN.getPrimaryKey());
        } else {
            db3FN.add(table2FN);
        }
        return db3FN;
    }*/

}
