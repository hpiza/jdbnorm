package edu.iteso.normalization;

import java.util.HashMap;
import java.util.Set;

public class StandardDependencyCalculator implements DependencyCalculator {

    private static StandardDependencyCalculator instance;

    private StandardDependencyCalculator() {
    }

    public static StandardDependencyCalculator getInstance() {
        if(instance == null) instance = new StandardDependencyCalculator();
        return instance;
    }

    @Override
    public boolean isDependent(Table table, int key, int notKey) {
        if(key >= table.columns() || notKey >= table.columns()) return false;
        HashMap<String, String> valueMap = new HashMap<>();
        for(Row row: table) {
            String keyValue = row.get(key);
            String notKeyValue = row.get(notKey);
            if (valueMap.containsKey(keyValue)) {
                String nkv = valueMap.get(keyValue);
                if (!notKeyValue.equals(nkv))
                    return false;
            } else {
                valueMap.put(keyValue, notKeyValue);
            }
        }
        return valueMap.size() > 0;
    }

    @Override
    public boolean isDependent(Table table, Set<Integer> compositeKey, int notKey) {
        if(notKey >= table.columns()) return false;
        HashMap<String, String> valueMap = new HashMap<>();
        for (Row row : table) {
            String keyValues = "";
            for (Integer i1 : compositeKey) keyValues += row.get(i1) + "\n";
            String notKeyValue = row.get(notKey);
            if (valueMap.containsKey(keyValues)) {
                String v2 = valueMap.get(keyValues);
                if (!notKeyValue.equals(v2))
                    return false;
            } else {
                valueMap.put(keyValues, notKeyValue);
            }
        }
        return valueMap.size() > 0;
    }
}
