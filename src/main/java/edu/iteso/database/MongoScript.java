package edu.iteso.database;

import edu.iteso.normalization.Database;
import edu.iteso.normalization.Row;
import edu.iteso.normalization.Table;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class MongoScript implements DbScript {

    public void createDatabase(Database database, String name) {
        try {
            FileWriter fw = new FileWriter(name + ".mongo");
            BufferedWriter bw = new BufferedWriter(fw);
            for (Table table: database) {
                bw.append(String.format("db.createCollection(%s)", table.getName()));
                bw.newLine();
            }
            bw.newLine();
            for (Table table: database) {
                populateCollection(bw, table);
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void populateCollection(BufferedWriter bw, Table table) throws IOException {
        bw.append(String.format("db.%s.insert([", table.getName()));
        bw.newLine();
        for(Row row: table) {
            String document = "";
            for(int i = 0; i < row.size(); i ++) {
                document += String.format("\"%s\": \"%s\"", table.getFieldName(i), row.get(i));
                if(i < row.size() - 1) document += ", ";
            }
            bw.append(String.format("{%s}", document));
            bw.newLine();
        }
        bw.append("])");
        bw.newLine();
    }
}
