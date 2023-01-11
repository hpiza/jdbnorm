package edu.iteso;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import edu.iteso.normalization.Table;

public class Main {
    public static void main(String[] args) throws IOException {
        Table table = Table.loadFromFile(new File("datasets/inscripciones.csv"));
        System.out.println(table.getName() + ": " + table.columns() + " x " + table.rows());
        System.out.println("-------");
        Set<Table> database = table.normalize();
        for(Table t: database) System.out.println(t.getName() + ": " + t.columns() + " x " + t.rows());
    }
}

