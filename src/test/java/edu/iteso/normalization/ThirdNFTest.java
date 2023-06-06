package edu.iteso.normalization;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ThirdNFTest {

    private static Table tableNoDependencies = new Table("Table with no dependencies");
    private static Table tableDependencies = new Table("Table with dependencies among non-key fields");
    private static Table tableTransitives = new Table("Table with transitive dependencies among non-key fields");

    @BeforeAll
    static void createTables() {
        tableNoDependencies.addFields("F1", "F2", "F3");
        tableNoDependencies.setPrimaryKey(new Key("F1"));
        tableNoDependencies.addRow("1", "AA", "aa");
        tableNoDependencies.addRow("2", "AA", "bb");
        tableNoDependencies.addRow("3", "BB", "aa");
        tableNoDependencies.addRow("4", "BB", "cc");
        tableNoDependencies.addRow("5", "CC", "dd");

        tableDependencies.addFields("F1", "F2", "F3");
        tableDependencies.setPrimaryKey(new Key("F1"));
        tableDependencies.addRow("1", "AB", "ab");
        tableDependencies.addRow("2", "AB", "ab");
        tableDependencies.addRow("3", "BA", "ab");
        tableDependencies.addRow("4", "CD", "cd");
        tableDependencies.addRow("5", "DC", "cd");
        tableDependencies.addRow("6", "DC", "cd");

        tableTransitives.addFields("F1", "F2", "F3", "F4");
        tableTransitives.setPrimaryKey(new Key("F1"));
        tableTransitives.addRow("1", "AB", "ab", "123");
        tableTransitives.addRow("2", "AB", "ab", "123");
        tableTransitives.addRow("3", "BA", "ab", "123");
        tableTransitives.addRow("4", "CD", "cd", "123");
        tableTransitives.addRow("5", "DC", "cd", "123");
        tableTransitives.addRow("6", "DC", "cd", "123");
        tableTransitives.addRow("7", "EF", "ef", "456");
        tableTransitives.addRow("8", "FE", "ef", "456");
        tableTransitives.addRow("9", "GH", "gh", "456");
        tableTransitives.addRow("10", "GH", "gh", "456");
        tableTransitives.addRow("11", "HG", "gh", "456");
    }

    @Test
    void testNormalizeWithoutDependencies() {
        // Given
        Normalizer thirdNF = Normalizers.getThirdNF();
        // When
        NormalizerResult result = thirdNF.isNormalized(tableNoDependencies);
        Database db = thirdNF.normalize(tableNoDependencies);
        // Then
        assertTrue(result.isNormalized);
        assertNotNull(db);
        assertEquals(db.size(), 1);
        assertTrue(db.containsTable(tableNoDependencies.getName()));
        Table tableF1 = db.getTable(tableNoDependencies.getName());
        assertEquals(tableNoDependencies, tableF1);
    }

    @Test
    void testNormalizeWithDependencies() {
        // Given
        Normalizer thirdNF = Normalizers.getThirdNF();
        // When
        NormalizerResult result = thirdNF.isNormalized(tableDependencies);
        Database db = thirdNF.normalize(tableDependencies);
        // Then
        assertFalse(result.isNormalized);
        assertNotNull(db);
        assertEquals(db.size(), 2);
        assertTrue(db.containsTable("F1"));
        assertTrue(db.containsTable("F2"));
        Table tableF1 = db.getTable("F1");
        assertEquals(2, tableF1.columns());
        assertEquals(tableDependencies.rows(), tableF1.rows());
        Table tableF2 = db.getTable("F2");
        assertEquals(2, tableF2.columns());
        assertEquals(4, tableF2.rows());
    }

    @Test
    void testNormalizeWithTransitives() {
        // Given
        Normalizer thirdNF = Normalizers.getThirdNF();
        // When
        NormalizerResult result = thirdNF.isNormalized(tableTransitives);
        Database db = thirdNF.normalize(tableTransitives);
        // Then
        assertFalse(result.isNormalized);
        assertNotNull(db);
        assertEquals(db.size(), 3);
        assertTrue(db.containsTable("F1"));
        assertTrue(db.containsTable("F2"));
        assertTrue(db.containsTable("F3"));
        Table tableF1 = db.getTable("F1");
        assertEquals(2, tableF1.columns());
        assertEquals(tableTransitives.rows(), tableF1.rows());
        Table tableF2 = db.getTable("F2");
        assertEquals(2, tableF2.columns());
        assertEquals(8, tableF2.rows());
        Table tableF3 = db.getTable("F3");
        assertEquals(2, tableF3.columns());
        assertEquals(4, tableF3.rows());
    }

    @Test
    void testIsNormalizedTableWithNoDependenciesReturnsTrue() {
        // Given
        Normalizer thirdNF = Normalizers.getThirdNF();
        // When
        NormalizerResult result = thirdNF.isNormalized(tableNoDependencies);
        // Then
        assertTrue(result.isNormalized);
        assertEquals(result.anomalyList.size(), 0);
    }

    @Test
    void testIsNormalizedTableWithDependenciesReturnsFalse() {
        // Given
        Normalizer thirdNF = Normalizers.getThirdNF();
        // When
        NormalizerResult result = thirdNF.isNormalized(tableDependencies);
        // Then
        assertFalse(result.isNormalized);
        assertEquals(result.anomalyList.size(), 1);
    }

    @Test
    void testIsNormalizedTableWithTransitiveDependenciesReturnsFalse() {
        // Given
        Normalizer thirdNF = Normalizers.getThirdNF();
        // When
        NormalizerResult result = thirdNF.isNormalized(tableTransitives);
        // Then
        assertFalse(result.isNormalized);
        assertEquals(result.anomalyList.size(), 3);
    }
}
