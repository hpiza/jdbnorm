package edu.iteso.normalization;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class StandardDependencyCalculatorTest {

    private static Table table1 = new Table("Table1");
    private static Table table2 = new Table("Table2");

    @BeforeAll
    static void createTables() {
        table1.addFields("F1", "F2", "F3");
        table1.addRow("1", "AB", "ab");
        table1.addRow("2", "AB", "ab");
        table1.addRow("3", "BA", "ab");
        table1.addRow("4", "CD", "cd");
        table1.addRow("5", "DC", "cd");
        table1.addRow("6", "DC", "cd");

        table2.addFields("F1", "F2", "F3");
        table2.addRow("1", "A", "1a");
        table2.addRow("1", "A", "1a");
        table2.addRow("1", "B", "1b");
        table2.addRow("2", "A", "2a");
        table2.addRow("2", "B", "2b");
        table2.addRow("2", "B", "2b");
    }

    @Test
    void testIsDependentNotKeyOnKey() {
        // Given
        StandardDependencyCalculator sdc = StandardDependencyCalculator.getInstance();
        // When
        boolean isDependent01 = sdc.isDependent(table1, 0, 1);
        boolean isDependent02 = sdc.isDependent(table1, 0, 2);
        boolean isDependent03 = sdc.isDependent(table1, 1, 2);
        // Then
        assertTrue(isDependent01);
        assertTrue(isDependent02);
        assertTrue(isDependent03);
    }

    @Test
    void testIsNotDependentKeyOnNotKey() {
        // Given
        StandardDependencyCalculator sdc = StandardDependencyCalculator.getInstance();
        // When
        boolean isDependent01 = sdc.isDependent(table1, 1, 0);
        boolean isDependent02 = sdc.isDependent(table1, 2, 0);
        boolean isDependent03 = sdc.isDependent(table1, 2, 1);
        // Then
        assertFalse(isDependent01);
        assertFalse(isDependent02);
        assertFalse(isDependent03);
    }

    @Test
    void testIsDependentNotKeyOnCompositeKey() {
        // Given
        StandardDependencyCalculator sdc = StandardDependencyCalculator.getInstance();
        // When
        boolean isDependent012 = sdc.isDependent(table2, Set.of(0, 1), 2);
        boolean isDependent01 = sdc.isDependent(table2, 0, 1);
        boolean isDependent02 = sdc.isDependent(table2, 0, 2);
        // Then
        assertTrue(isDependent012);
        assertFalse(isDependent01);
        assertFalse(isDependent02);
    }
}
