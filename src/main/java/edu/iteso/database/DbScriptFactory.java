package edu.iteso.database;

import java.util.Map;

public class DbScriptFactory {

    private static final Map<String, DbScript> instances =
            Map.of( "Mysql", new MysqlScript(),
                    "Mongodb", new MongoScript(),
                    "CSV", new CsvFileScript());

    public static DbScript getMysql() {
        return instances.get("Mysql");
    }
    public static DbScript getMongodb() {
        return instances.get("Mongodb");
    }
    public static DbScript getCSV() { return instances.get("CSV"); }
}
