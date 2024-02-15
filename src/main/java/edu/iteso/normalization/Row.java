package edu.iteso.normalization;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Row implements Iterable<String> {
    private ArrayList<String> values;
    public Row() {
        this.values = new ArrayList<>();
    }

    public Row(String[] valuesArray) {
        this.values = new ArrayList<>(valuesArray.length);
        for(String v : valuesArray) values.add(v);
    }

    public Row(List<String> values) {
        this.values = new ArrayList<>(values);
    }

    public void add(String v) {
        this.values.add(v);
    }

    public void addFirst(String v) {
        this.values.add(0, v);
    }

    public Row clone() {
        return new Row(this.values);
    }

    public boolean equals(Object o) {
        if(!(o instanceof Row)) return false;
        Row r = (Row) o;
        return this.values.equals(r.values);
    }

    public int hashCode() {
        return this.values.toString().hashCode();
    }

    public String get(int index) {
        if(index < 0 || index >= this.values.size()) return null;
        return values.get(index);
    }

    public void set(int index, String v) {
        if(index < 0 || index >= this.values.size()) return;
        this.values.set(index, v);
    }

    public int size() {
        return this.values.size();
    }

    public String toString() {
        return this.values.toString();
    }

    public String[] toArray(int rowIndex) {
        String[] stringArray = new String[this.values.size() + 1];
        stringArray[0] = rowIndex + "";
        for(int i = 0; i < this.values.size(); i ++) stringArray[i + 1] = this.values.get(i);
        return stringArray;
    }

    @Override
    public Iterator<String> iterator() {
        return this.values.iterator();
    }
}
