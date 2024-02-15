package edu.iteso;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.iteso.database.DbScriptFactory;
import edu.iteso.normalization.*;

public class Main {

    public static void main(String[] args) throws IOException {
        Table inputTable = Table.fromFile("datasets/love.csv");
        Database db3FN = NormalizerFactory.getThirdNF().normalize(inputTable);
        //NormalizerFactory.getThirdNF().setDependencyCalculator(StandardDependencyCalculator.getInstance());
        Iterator<Table> ite = db3FN.iterator();
        while(ite.hasNext()) {
            Table table3FN = ite.next();
            System.out.printf("rows: %d, columns: %d\n", table3FN.rows(), table3FN.columns());
            for (int i = 0; i < table3FN.columns(); i++) System.out.printf(table3FN.getFieldName(i) + " ");
            System.out.println("\nPrimary key: " + table3FN.getPrimaryKey());
        }
        DbScriptFactory.getMysql().createDatabase(db3FN, "love");
        DbScriptFactory.getMongodb().createDatabase(db3FN, "love");
        DbScriptFactory.getCSV().createDatabase(db3FN, "love");
    }

    public static void main1(String[] args) throws IOException {
        Normalizer firstNF = NormalizerFactory.getFirstNF();
        Normalizer secondNF = NormalizerFactory.getSecondNF();
        Normalizer thirdNF = NormalizerFactory.getThirdNF();

        Table table = new Table("NotIn2FN");
        table.addFields("F1", "F2", "F3", "F4", "F5", "F6");
        //table.setPrimaryKey(new Key("F1", "F2"));
        table.addRow("aa", "11", "50348", "11.1", "aaa", "alpha");
        table.addRow("aa", "22", "40647", "22.2", "aaa", "beta");
        table.addRow("aa", "33", "37483", "33.3", "aaa", "gamma");
        table.addRow("bb", "11", "12345", "11.1", "bbb", "alpha");
        table.addRow("bb", "22", "54321", "22.2", "bbb", "beta");
        table.addRow("bb", "33", "50348", "33.3", "bbb", "gamma");
        table.addRow("cc", "11", "12345", "11.1", "ccc", "alpha");
        table.addRow("cc", "22", "91038", "22.2", "ccc", "beta");
        table.addRow("cc", "44", "68059", "44.4", "ccc", "gamma");
        table.addRow("cc", "55", "12345", "55.5", "ccc", "beta");
        System.out.println(firstNF.isNormalized(table));
        System.out.println(secondNF.isNormalized(table));
        System.out.println(thirdNF.isNormalized(table));
        Iterator<Table> iterator = firstNF.normalize(table).iterator();
        table = iterator.next();
        //System.out.println(table);
        Map<Key, List<String>> dependencies = table.getDependencies();
        System.out.println("Dependencies: " + dependencies);

        System.out.println(firstNF.isNormalized(table));
        System.out.println(secondNF.isNormalized(table));
        System.out.println(thirdNF.isNormalized(table));

        Database db = secondNF.normalize(table);
        System.out.println(db);
        System.out.println("Antes de normalizar a 3FN");
        System.out.println(thirdNF.isNormalized(db.getTable("Catalog_F1")));
        System.out.println(thirdNF.isNormalized(db.getTable("Catalog_F2")));
        System.out.println(thirdNF.isNormalized(db.getTable("Catalog_F1_F2")));
        System.out.println("Después de normalizar a 3FN");
        db = thirdNF.normalize(db.getTable("Catalog_F2"));
        for(Table t: db) {
            System.out.println(thirdNF.isNormalized(t));
            System.out.println(t);
        }

        DbScriptFactory.getMysql().createDatabase(db, "NotIn2FN");
        Database amigosDb = NormalizerFactory.getThirdNF().normalize(Table.fromFile("datasets/amigos.csv"));
        DbScriptFactory.getMongodb().createDatabase(amigosDb, "Amigos");
        Database inscripcionesDb = NormalizerFactory.getThirdNF().normalize(Table.fromFile("datasets/inscripciones.csv"));
        DbScriptFactory.getMysql().createDatabase(inscripcionesDb, "Inscripciones");

        /*
        Table bigTable = Table.fromFile("datasets/dataset2.csv");
        System.out.println(bigTable.columns() + ", " + bigTable.rows());
        System.out.println("Is in First Normal Form? " + firstNF.isNormalized(bigTable));
        Database bigDB1NF = firstNF.normalize(bigTable);
        System.out.println(bigDB1NF.size());
        Database bigDB2NF = new Database();
        for(Table bigT: bigDB1NF) bigDB2NF.add(secondNF.normalize(bigT));
        System.out.println(bigDB2NF.size());
        Database bigDB3NF = new Database();
        for(Table bigT: bigDB2NF) bigDB3NF.add(thirdNF.normalize(bigT));
        System.out.println(bigDB3NF.size());

        CsvFileDriver csvFileDriver = new CsvFileDriver();
        csvFileDriver.createDatabase(bigDB3NF, "dataset2");

        Table dataset2 = Table.fromFile("datasets/dataset2.csv");
        System.out.println(dataset2.columns() + ", " + dataset2.rows());
        long start = System.currentTimeMillis();
        Database db2 = thirdNF.normalize(dataset2);
        long end = System.currentTimeMillis();
        System.out.printf("Seconds: %.2f\n", (end - start) / 1000.0);

        /*
        Table dataset5 = Table.fromFile("datasets/dataset5.csv");
        System.out.println(dataset5.columns() + ", " + dataset5.rows());
        start = System.currentTimeMillis();
        Database db5 = thirdNF.normalize(dataset5);
        end = System.currentTimeMillis();
        System.out.printf("Seconds: %.2f\n", (end - start) / 1000.0);

        Table dataset4 = Table.fromFile("datasets/dataset4.csv");
        System.out.println(dataset4.columns() + ", " + dataset4.rows());
        start = System.currentTimeMillis();
        Database db4 = thirdNF.normalize(dataset4);
        end = System.currentTimeMillis();
        System.out.printf("Seconds: %.2f\n", (end - start) / 1000.0);

        Driver d = DriverFactory.getDriver("MySQL");
        d.createDatabase(db2, "SuperDB");*/
    }
}

