package edu.iteso.normalization;

import java.util.*;

public class SecondNF extends FirstNF {

    public Database normalize(Table inputTable) {
        if(isNormalized(inputTable).isNormalized) return new Database(inputTable);

        Database db1FN = super.normalize(inputTable);
        Iterator<Table> iterator = db1FN.iterator();
        Table table1FN = iterator.next();

        Map<Key, List<String>> dependencies = table1FN.getDependencies();
        Set<Key> keysFound = new HashSet<>();
        Key primaryKey = table1FN.getPrimaryKey();

        Map<Key, List<String>> dependenciesToPK = new HashMap<>();
        for(Map.Entry<Key, List<String>> d: dependencies.entrySet()) {
            Key key = d.getKey();
            if(key.isSubsetOf(primaryKey)) {
                List<String> value = d.getValue();
                if(value.isEmpty()) dependenciesToPK.put(key, value);
                else for(String f : value) addDependency(key, f, dependencies, dependenciesToPK);
            }
        }
        Map<Key, Table> tableMap = new HashMap<>();
        Database database = new Database();
        for(Map.Entry<Key, List<String>> d: dependenciesToPK.entrySet()) {
            Key key = d.getKey();
            List<String> value = d.getValue();
            if(value.isEmpty()) {
                String title = key.toString();
                if(title.startsWith("<")) {
                    int last = key.toString().length() - 1;
                    title = key.toString().substring(1, last).replace(", ", "_");
                }
                Table table = new Table(title);
                for(String f: key) table.addField(f);
                table.setPrimaryKey(key);
                keysFound.add(key);
                database.add(table, false);
                tableMap.put(key, table);
            }
            else for(String field: value) {
                if(keysFound.contains(key)) {
                    Table table = tableMap.get(key);
                    table.addField(field);
                } else {
                    String keyName = key.toString();
                    String title;
                    if(key.size() == 1) {
                        title = "Catalog_" + keyName;
                    } else {
                        int last = keyName.length() - 1;
                        char c = Character.toUpperCase(keyName.charAt(1));
                        title = c + keyName.substring(2, last).replace(", ", "_");
                    }
                    Table table = new Table(title);
                    for(String f: key) table.addField(f);
                    table.setPrimaryKey(key);
                    table.addField(field);
                    keysFound.add(key);

                    tag: for(Table t: database) {
                        for(int c = 0; c < t.columns(); c ++) {
                            String f = t.getFieldName(c);
                            if(f.equals(title)) {
                                t.addForeignKey(new Key(f), table.getName());
                                break tag;
                            }
                        }
                    }

                    database.add(table, true);
                    tableMap.put(key, table);
                }
            }
        }
        for(Table table: database) {
            for(Row row1: table1FN) {
                Row row2 = new Row();
                for(int f = 0; f < table.columns(); f ++) {
                    String fieldName = table.getFieldName(f);
                    int f1 = table1FN.getFieldIndex(fieldName);
                    row2.add(row1.get(f1));
                }
                table.addRow(row2);
            }
        }
        return database;
    }

    @Override
    public NormalizerResult isNormalized(Table table) {
        if(!super.isNormalized(table).isNormalized) {
            return new NormalizerResult(false, List.of("Table is not in First Normal Form"));
        }
        Key key = table.getPrimaryKey();
        if(key.size() == 1) return NormalizerResult.NORMALIZED;
        boolean isNormalized = true;
        List<String> errorList = new ArrayList<>();
        for(String fieldInKey: key) {
            int index = table.getFieldIndex(fieldInKey);
            for(int c = 0; c < table.columns(); c ++) {
                if(index == c) continue;
                if(getDependencyCalculator().isDependent(table, index, c)) {
                    isNormalized = false;
                    errorList.add(String.format("Field %s is defined by one field in the key (%s)", table.getFieldName(c), fieldInKey));
                }
            }
        }
        return new NormalizerResult(isNormalized, errorList);
    }

    private static void addDependency(Key rootKey, String field, Map<Key, List<String>> dependencies, Map<Key, List<String>> dependenciesToPK) {
        List<String> list = dependenciesToPK.get(rootKey);
        if(list == null) list = new ArrayList<>();
        else if(list.contains(field)) return;
        list.add(field);
        dependenciesToPK.put(rootKey, list);
        for(Map.Entry<Key, List<String>> d: dependencies.entrySet()) {
            Key key = d.getKey();
            if(key.size() == 1 && key.contains(field)) {
                for(String f : d.getValue()) addDependency(rootKey, f, dependencies, dependenciesToPK);
            }
        }
    }
}
