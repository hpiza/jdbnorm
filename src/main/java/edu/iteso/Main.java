package edu.iteso;

import java.io.IOException;

import edu.iteso.database.DbScriptFactory;
import edu.iteso.normalization.*;

public class Main {

    public static void main(String[] args) throws IOException {
        Table inputTable = Table.fromFile("datasets/albums.csv");
        Normalizer thirdNF = NormalizerFactory.getThirdNF();
        //thirdNF.setDependencyCalculator(StandardDependencyCalculator.getInstance());
        Database albums3FN = thirdNF.normalize(inputTable);
        Database.toCsvFiles(albums3FN, "Albums");
        DbScriptFactory.getMysql().createDatabase(albums3FN, "Albums", "albums.script");
        DbScriptFactory.getMongodb().createDatabase(albums3FN, "Albums", "albums.mongo");
    }

}
