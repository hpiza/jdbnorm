package edu.iteso.normalization;

import java.util.HashMap;
import java.util.Set;
public class NormalizeUtils {

    public static boolean defines(int i1, int i2, Table table) {
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
        for (Row row : table) {
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
}
