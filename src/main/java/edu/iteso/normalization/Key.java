package edu.iteso.normalization;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Key implements Iterable<String> {
    private final Set<String> fields;
    private final int hashCode;
    private final String toString;
    private static Key emptyKey = null;

    public static Key emptyKey() {
        if(emptyKey == null) emptyKey = new Key();
        return emptyKey;
    }

    private Key() {
        this.fields = new HashSet<>();
        this.hashCode = 0;
        this.toString = "";
    }

    public Key(String field) {
        this.fields = new HashSet<>();
        this.fields.add(field);
        this.hashCode = field.hashCode();
        this.toString = field;
    }

    public Key(Set<String> fields) {
        this.fields = new HashSet<>(fields);
        this.hashCode = this.fields.hashCode();
        String str = this.fields.toString();
        this.toString = str.substring(1, str.length() - 1);
    }

    public Key(String... fields) {
        this(Set.of(fields));
    }

    public static Key fromFieldIndices(Set<Integer> fieldIndices, Table table) {
        Set<String> fields = new HashSet<>();
        for(int i: fieldIndices) fields.add(table.getFieldName(i));
        return new Key(fields);
    }
    public boolean contains(String value) {
        return this.fields.contains(value);
    }
    public boolean equals(Object o) {
        if(!(o instanceof Key key)) return false;
        return this.fields.equals(key.fields);
    }
    public int hashCode() {
        return this.hashCode;
    }
    public boolean isSubsetOf(Key key) {
        if(this.size() > key.size()) return false;
        for(String f : this.fields) {
            if(!key.contains(f)) return false;
        }
        return true;
    }
    public int size() {
        return this.fields.size();
    }
    public String toString() {
        return this.toString;
    }
    public Iterator<String> iterator() {
        return this.fields.iterator();
    }

}
