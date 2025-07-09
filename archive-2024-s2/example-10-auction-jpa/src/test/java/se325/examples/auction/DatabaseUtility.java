package se325.examples.auction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.FileNameMap;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class that provides methods for working with a database. This class
 * interacts with the database using plain SQL, and doesn't use any ORM
 * functionality.
 */
public class DatabaseUtility {

    public static final String DATABASE_DRIVER_NAME = "org.h2.Driver";
    public static final String DATABASE_URL = "jdbc:h2:~/test;mv_store=false";
    public static final String DATABASE_USERNAME = "sa";
    public static final String DATABASE_PASSWORD = "sa";

    private static Logger LOGGER = LoggerFactory.getLogger(DatabaseUtility.class);

    public static void clearTables() throws ClassNotFoundException, SQLException {

        Class.forName(DATABASE_DRIVER_NAME);

        try (Connection jdbcConnection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);) {
            try (Statement s = jdbcConnection.createStatement()) {
                s.execute("SET REFERENTIAL_INTEGRITY FALSE");

                Set<String> tables = new HashSet<>();
                try (ResultSet rs = s.executeQuery("select table_name "
                        + "from INFORMATION_SCHEMA.tables "
                        + "where table_type='TABLE' and table_schema='PUBLIC'")) {

                    while (rs.next()) {
                        tables.add(rs.getString(1));
                    }
                }
                for (String table : tables) {
                    LOGGER.debug("Deleting content from " + table);
                    s.executeUpdate("DELETE FROM " + table);
                }

                s.execute("SET REFERENTIAL_INTEGRITY TRUE");
            }
        }
    }

    public static void deleteDatabase() {
        deleteFile(System.getProperty("user.home") + "/test.h2.db");
        deleteFile(System.getProperty("user.home") + "/test.trace.db");
    }

    private static void deleteFile(String fileName) {
        LOGGER.warn(fileName);
        File f = new File(fileName);
        if (f.exists() && !f.isDirectory()) {
            f.delete();
            LOGGER.warn(fileName + " deleted!");
        }
    }
}
