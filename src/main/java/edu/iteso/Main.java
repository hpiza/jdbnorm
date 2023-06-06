package edu.iteso;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.iteso.normalization.*;

public class Main {

    public static void main(String[] args) throws IOException {
        Normalizer firstNF = Normalizers.getFirstNF();
        Normalizer secondNF = Normalizers.getSecondNF();
        Normalizer thirdNF = Normalizers.getThirdNF();

        Table table = new Table("NotIn2FN");
        table.addFields("F1", "F2", "F3", "F4", "F5", "F6");
        //table.setPrimaryKey(new Key("F1", "F2"));
        table.addRow("aa", "11", "50348", "111", "aaa", "alpha");
        table.addRow("aa", "22", "40647", "222", "aaa", "beta");
        table.addRow("aa", "33", "37483", "333", "aaa", "gamma");
        table.addRow("bb", "11", "12345", "111", "bbb", "alpha");
        table.addRow("bb", "22", "54321", "222", "bbb", "beta");
        table.addRow("bb", "33", "50348", "333", "bbb", "gamma");
        table.addRow("cc", "11", "12345", "111", "ccc", "alpha");
        table.addRow("cc", "22", "91038", "222", "ccc", "beta");
        table.addRow("cc", "44", "68059", "444", "ccc", "gamma");
        table.addRow("cc", "55", "12345", "555", "ccc", "beta");
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
        System.out.println(thirdNF.isNormalized(db.getTable("F1")));
        System.out.println(thirdNF.isNormalized(db.getTable("F2")));
        System.out.println(thirdNF.isNormalized(db.getTable("F1, F2")));
        System.out.println("Después de normalizar a 3FN");
        db = thirdNF.normalize(db.getTable("F2"));
        for(Table t: db) {
            System.out.println(thirdNF.isNormalized(t));
            System.out.println(t);
        }

    }
}

