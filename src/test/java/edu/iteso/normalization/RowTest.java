package edu.iteso.normalization;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RowTest {
    Row row1, row2;
    String[] values;
    @BeforeEach
    void setUp() {
        row1 = new Row();
        values = new String[] {"v1", "v2", "v3"};
        row2 = new Row(values);
    }

    @Test
    void testRow() {
        assertEquals(row1.size(), 0);
        assertEquals(row2.size(), values.length);
    }

    @Test
    void testGet() {
        int i = 1;
        String v = row2.get(i);
        assertEquals(v, values[i]);
    }

    @Test
    void testAdd() {
        int s = row2.size();
        String v1 = "v4";
        row2.add(v1);
        assertEquals(row2.size(), s + 1);
        String v2 = row2.get(s);
        assertEquals(v1, v2);
    }

    @Test
     void testAddFirst() {
        int s = row2.size();
        String v1 = "v0";
        row2.addFirst(v1);
        assertEquals(row2.size(), s + 1);
        String v2 = row2.get(0);
        assertEquals(v1, v2);
    }

    @Test
    void testSet() {
        row2.set(1, "v4");
        assertEquals(row2.get(1), "v4");
    }

    @Test
    void testToString() {
        List<String> list = new ArrayList<>();
        for(int i = 0; i < row2.size(); i ++) list.add(row2.get(i));
        assertEquals(row2.toString(), list.toString());
    }

}
