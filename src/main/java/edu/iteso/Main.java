package edu.iteso;

import java.io.IOException;

import edu.iteso.database.DbScriptFactory;
import edu.iteso.normalization.*;

public class Main {

    public static void main(String[] args) throws IOException {
        Table schoolTable = Table.fromFile("datasets/registrations.csv");
        Database schoolDB =  NormalizerFactory.getThirdNF().normalize(schoolTable);
        System.out.println(schoolDB);
        System.out.println();
        Table albumTable = Table.fromFile("datasets/albums.csv");
        Database musicDB =  NormalizerFactory.getThirdNF().normalize(albumTable);
        System.out.println(musicDB);

        Database.toCsvFiles(schoolDB, "School");
        DbScriptFactory.getMysql().createDatabase(musicDB, "Music", "Music.script");
        DbScriptFactory.getMongodb().createDatabase(musicDB, "Music", "Music.mongo");
    }

}
