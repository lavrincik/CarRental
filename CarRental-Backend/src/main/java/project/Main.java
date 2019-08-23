package project;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.derby.jdbc.ClientDataSource;
import org.apache.derby.jdbc.EmbeddedDriver;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import project.impl.Car;
import project.impl.CarManagerImpl;

import javax.sql.DataSource;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * @author Daniel Jurca
 */
public class Main {
    public static ResourceBundle local = ResourceBundle.getBundle("localization");

    public static void saveDBPreferences() {
        Preferences databasePrefs = Preferences.userNodeForPackage(Main.class);

        String dbName = "CarRentalDatabase";
        String dbServerName = "localhost";
        int dbPortNumber = 1527;

        databasePrefs.put("CRDatabase-Name", dbName);
        databasePrefs.put("CRDatabase-ServerName", dbServerName);
        databasePrefs.putInt("CRDatabase-PortNumber", dbPortNumber);
        try {
            databasePrefs.exportSubtree(System.out);
        } catch (IOException | BackingStoreException e) {
            e.printStackTrace();
        }
    }
    public static DataSource createMemoryDatabase() {
        BasicDataSource bds = new BasicDataSource();
        bds.setDriverClassName(EmbeddedDriver.class.getName());
        bds.setUrl("jdbc:derby:memory:CarRentalDB;create=true");
        new ResourceDatabasePopulator(
                new ClassPathResource("createTables.sql"),
                new ClassPathResource("fillTables.sql")
        ).execute(bds);
        return bds;
    }

    public static DataSource connectDatabase() {

        Preferences databasePrefs = Preferences.userNodeForPackage(Main.class);
        String dbName = databasePrefs.get("CRDatabase-Name", "None");
        String dbServerName = databasePrefs.get("CRDatabase-ServerName", "None");
        int dbPortNumber = databasePrefs.getInt("CRDatabase-PortNumber", 0);
        ClientDataSource ds = new ClientDataSource();
        ds.setServerName(dbServerName);
        ds.setPortNumber(dbPortNumber);
        ds.setDatabaseName(dbName);
        return ds;
    }

    public static void main(String[] args){
        EventQueue.invokeLater(() -> {
            JFrame frame = new JFrame("Car Rental");
            frame.setContentPane(new CarRentalForm().getTopPanel());
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setPreferredSize(new Dimension(1000,600));
            frame.pack();
            frame.setVisible(true);
        });

    }
}
