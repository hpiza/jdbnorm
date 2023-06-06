package edu.iteso.normalization;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

public class FirstNFTest {

    private static Table table1FN = new Table("Table in 1FN");
    private static Table tableNoKey = new Table("Table with no key");
    private static Table mvfTable = new Table("Table with wultivalued fields");

    @BeforeAll
    static void createNormalizedTable() {
        table1FN.addFields("F1", "F2", "F3");
        table1FN.addRow("aaa", "123", "1078");
        table1FN.addRow("bbb", "246", "1079");
        table1FN.addRow("aaa", "123", "1678");
        table1FN.addRow("bbb", "123", "1679");
        table1FN.addRow("bbb", "246", "1680");
        table1FN.setPrimaryKey(new Key("F3"));
    }

    @BeforeAll
    static void createTableWithoutKey() {
        tableNoKey.addFields("F1", "F2", "F3", "F4");
        tableNoKey.addRow("aaa", "123", "10", "78");
        tableNoKey.addRow("bbb", "246", "10", "79");
        tableNoKey.addRow("aaa", "123", "16", "78");
        tableNoKey.addRow("bbb", "123", "16", "79");
        tableNoKey.addRow("bbb", "246", "16", "80");
    }

    @BeforeAll
    static void createTableWithMultivaluedFields() {
        mvfTable.addField("F1");
        mvfTable.addField("F2");
        mvfTable.addField("", true);
        mvfTable.addField("", true);
        mvfTable.addField("F3");
        mvfTable.addRow("A1", "aaa", "bbb", "", "123");
        mvfTable.addRow("A2", "aaa", "ccc", "", "456");
        mvfTable.addRow("A3", "bbb", "",    "", "123");
        mvfTable.addRow("B1", "aaa", "bbb", "ccc", "456");
        mvfTable.addRow("B2", "bbb", "ccc", "aaa", "123");
        mvfTable.addRow("B3", "ddd", "aaa", "", "456");
    }

    @Test
    void testNormalizeTableIn1FNProducesInputTable() {
        // Given
        Normalizer firstNF = Normalizers.getFirstNF();
        // When
        Database db = firstNF.normalize(table1FN);
        Iterator<Table> iterator = db.iterator();
        // Then
        assertNotNull(db);
        assertEquals(db.size(), 1);
        Table table = iterator.next();
        assertNotNull(table);
        assertEquals(table, table1FN);
    }

    @Test
    void testNormalizeFindsPrimaryKey() {
        // Given
        Normalizer firstNF = Normalizers.getFirstNF();
        // When
        Database db = firstNF.normalize(tableNoKey);
        Iterator<Table> iterator = db.iterator();
        // Then
        assertNotNull(db);
        assertEquals(db.size(), 1);
        Table table = iterator.next();
        assertNotNull(table);
        assertEquals(table.columns(), tableNoKey.columns());
        assertEquals(table.rows(), tableNoKey.rows());
        assertEquals(table.getPrimaryKey(), new Key("F3", "F4"));
    }

    @Test
    void testNormalizeFindsPrimaryKeyInTableWithMultivalued() {
        // Given
        Normalizer firstNF = Normalizers.getFirstNF();
        // When
        Database db = firstNF.normalize(mvfTable);
        Iterator<Table> iterator = db.iterator();
        // Then
        assertNotNull(db);
        assertEquals(db.size(), 1);
        Table table = iterator.next();
        assertNotNull(table);
        Key key = table.getPrimaryKey();
        assertEquals(key, new Key("F1", "F2"));
    }

    @Test
    void testNormalizeTableWithMultivaluedContainsLessColumnsAndMoreRows() {
        // Given
        Normalizer firstNF = Normalizers.getFirstNF();
        // When
        Database db = firstNF.normalize(mvfTable);
        Iterator<Table> iterator = db.iterator();
        Table t = iterator.next();
        // Then
        assertNotNull(t);
        assertEquals(t.columns(), 3);
        assertEquals(t.rows(), 13);
    }

    @Test
    void testIsNormalizedTable1FNReturnsTrueAndEmptyAnomalyList() {
        // Given
        Normalizer firstNF = Normalizers.getFirstNF();
        // When
        NormalizerResult result = firstNF.isNormalized(table1FN);
        // Then
        assertTrue(result.isNormalized);
        assertEquals(result.anomalyList.size(), 0);
    }

    @Test
    void testIsNormalizedTableNoKeyReturnsFalse() {
        // Given
        Normalizer firstNF = Normalizers.getFirstNF();
        // When
        NormalizerResult result = firstNF.isNormalized(tableNoKey);
        // Then
        assertFalse(result.isNormalized);
        assertEquals(result.anomalyList.size(), 1);
        assertEquals(result.anomalyList.get(0), "Primary key is not defined");
    }

    @Test
    void testIsNormalizedTableWithMultivaluedAnomalyListContainsThreeErrors() {
        // Given
        Normalizer firstNF = Normalizers.getFirstNF();
        // When
        NormalizerResult result = firstNF.isNormalized(mvfTable);
        // Then
        assertFalse(result.isNormalized);
        assertEquals(result.anomalyList.size(), 3);
        assertTrue(result.anomalyList.contains("Field 2 is empty"));
        assertTrue(result.anomalyList.contains("Field 3 is empty"));
        assertTrue(result.anomalyList.contains("Primary key is not defined"));
    }

}
