package edu.iteso.normalization;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

public class SecondNFTest {

    private static Table tableSingleKey = new Table("Table with a single primary key");
    private static Table tableTwoKeys = new Table("Table with two primary keys in 2FN");
    private static Table tablePartialDependencies = new Table("Table with partial dependencies to primary key");

    @BeforeAll
    static void createTables() {
        tableSingleKey.addFields("F1", "F2", "F3");
        tableSingleKey.addRow("1", "AA", "132");
        tableSingleKey.addRow("2", "AA", "123");
        tableSingleKey.addRow("3", "BB", "123");
        tableSingleKey.addRow("4", "BB", "123");
        tableSingleKey.setPrimaryKey(new Key("F1"));

        tableTwoKeys.addFields("F1", "F2", "F3", "F4");
        tableTwoKeys.addRow("1", "AA", "111", "***");
        tableTwoKeys.addRow("1", "BB", "111", "---");
        tableTwoKeys.addRow("2", "AA", "333", "+++");
        tableTwoKeys.addRow("2", "BB", "111", "***");
        tableTwoKeys.addRow("3", "AA", "333", "---");
        tableTwoKeys.setPrimaryKey(new Key("F1", "F2"));

        tablePartialDependencies.addFields("F1", "F2", "F3", "F4", "F5", "F6");
        tablePartialDependencies.setPrimaryKey(new Key("F1", "F2"));
        tablePartialDependencies.addRow("aa", "11", "50348", "111", "aaa", "alpha");
        tablePartialDependencies.addRow("aa", "22", "40647", "222", "aaa", "beta");
        tablePartialDependencies.addRow("aa", "33", "37483", "333", "aaa", "gamma");
        tablePartialDependencies.addRow("bb", "11", "12345", "111", "bbb", "alpha");
        tablePartialDependencies.addRow("bb", "22", "54321", "222", "bbb", "beta");
        tablePartialDependencies.addRow("bb", "33", "50348", "333", "bbb", "gamma");
        tablePartialDependencies.addRow("cc", "11", "12345", "111", "ccc", "alpha");
        tablePartialDependencies.addRow("cc", "22", "91038", "222", "ccc", "beta");
        tablePartialDependencies.addRow("cc", "44", "68059", "444", "ccc", "gamma");
        tablePartialDependencies.addRow("cc", "55", "12345", "555", "ccc", "beta");
    }

    @Test
    void testNormalizeTableWithSingleKeyProducesInputTable() {
        // Given
        Normalizer secondNF = Normalizers.getSecondNF();
        // When
        NormalizerResult result = secondNF.isNormalized(tableSingleKey);
        Database db = secondNF.normalize(tableSingleKey);
        Iterator<Table> iterator = db.iterator();
        // Then
        assertTrue(result.isNormalized);
        assertNotNull(db);
        assertEquals(db.size(), 1);
        Table table = iterator.next();
        assertEquals(table, tableSingleKey);
    }

    @Test
    void testNormalizeTableAllFieldsDependOnAllKeysProducesInputTable() {
        // Given
        Normalizer secondNF = Normalizers.getSecondNF();
        // When
        NormalizerResult result = secondNF.isNormalized(tableTwoKeys);
        Database db = secondNF.normalize(tableTwoKeys);
        Iterator<Table> iterator = db.iterator();
        // Then
        assertTrue(result.isNormalized);
        assertNotNull(db);
        assertEquals(db.size(), 1);
        Table table = iterator.next();
        assertEquals(table, tableTwoKeys);
    }

    @Test
    void testNormalizeTableWithPartialDependencies() {
        // Given
        Normalizer secondNF = Normalizers.getSecondNF();
        // When
        NormalizerResult result = secondNF.isNormalized(tablePartialDependencies);
        Database db = secondNF.normalize(tablePartialDependencies);
        // Then
        assertFalse(result.isNormalized);
        assertNotNull(db);
        assertEquals(db.size(), 3);
        assertTrue(db.containsTable("F1, F2"));
        assertTrue(db.containsTable("F1"));
        assertTrue(db.containsTable("F2"));
        Table tableF1F2 = db.getTable("F1, F2");
        assertEquals(3, tableF1F2.columns());
        assertEquals(tablePartialDependencies.rows(), tableF1F2.rows());
        Table tableF1 = db.getTable("F1");
        assertEquals(2, tableF1.columns());
        assertEquals(3, tableF1.rows());
        Table tableF2 = db.getTable("F2");
        assertEquals(3, tableF2.columns());
        assertEquals(5, tableF2.rows());
    }

    @Test
    void testIsNormalizedTable2FNReturnsTrueAndEmptyAnomalyList() {
        // Given
        Normalizer secondNF = Normalizers.getSecondNF();
        // When
        NormalizerResult result = secondNF.isNormalized(tableSingleKey);
        // Then
        assertTrue(result.isNormalized);
        assertEquals(result.anomalyList.size(), 0);
    }

    @Test
    void testIsNormalizedTableIn2NFReturnsTrue() {
        // Given
        Normalizer secondNF = Normalizers.getSecondNF();
        // When
        NormalizerResult result = secondNF.isNormalized(tableTwoKeys);
        // Then
        assertTrue(result.isNormalized);
        assertEquals(result.anomalyList.size(), 0);
    }

    @Test
    void testIsNormalizedTableWithPartialDependenciesReturnsFalsee() {
        // Given
        Normalizer secondNF = Normalizers.getSecondNF();
        // When
        NormalizerResult result = secondNF.isNormalized(tablePartialDependencies);
        // Then
        assertFalse(result.isNormalized);
        assertEquals(result.anomalyList.size(), 3);
    }

}
