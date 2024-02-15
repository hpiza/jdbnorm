package edu.iteso.database;

import edu.iteso.normalization.Database;

public interface DbScript {
    void createDatabase(Database db, String name);

}
