package edu.iteso.database;

import edu.iteso.normalization.Database;
import edu.iteso.normalization.Row;
import edu.iteso.normalization.Table;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CsvFileScript implements DbScript {

    public void createDatabase(Database database, String name) {
        Path path = Paths.get(name);
        try {
            path = Files.createDirectory(path);
            for(Table table: database) toCsvFile(path, table);
        } catch(Exception ex) {
        }
    }

    private void toCsvFile(Path dirPath, Table table) throws IOException {
        Path filePath = Paths.get(dirPath.toString(), table.getName() + ".csv");
        BufferedWriter bw = new BufferedWriter(new FileWriter(filePath.toFile()));
        String str = "";
        for(int i = 0; i < table.columns(); i ++) {
            str += table.getFieldName(i);
            if(i < table.columns() - 1) str += ",";
        }
        bw.append(str);
        bw.newLine();
        for(Row row: table) {
            str = "";
            for(int i = 0; i < row.size(); i ++) {
                str += row.get(i);
                if(i < row.size() - 1) str += ",";
            }
            bw.append(str);
            bw.newLine();
        }
        bw.close();
    }
}
