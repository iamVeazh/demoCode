package ru.norkin;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import org.hibernate.cfg.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.norkin.server.ServerResourceImpl;
import ru.norkin.tx.TxExecutor;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Main {

    public static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {

        log.info("Starting norkin monitor service...");
        log.info("Loading configuration...");

        loadProperties();

        DataSource dataSource = createDataSource(
                System.getProperty("norkin.db.url"),
                System.getProperty("norkin.db.user"),
                System.getProperty("norkin.db.pass")
        );

        log.info("Start connection to " + System.getProperty("norkin.db.url"));

        EntityManagerFactory emf = createEMF("norkin", dataSource);
        TxExecutor txExecutor = new TxExecutor(emf);

        log.info("Data base connected...");

        log.info("----------Server started-----------");
        Server server = ServerBuilder
                .forPort(50051)
                .addService(new ServerResourceImpl(txExecutor))
                .build();
        int[] i = new int[2];
        int a = i.length;

        server.start();
        server.awaitTermination();
    }

    public static void loadProperties() throws Exception {
        Properties properties = new Properties();
        properties.load(new FileInputStream(new File("/etc/static.properties")));
        System.getProperties().putAll(properties);
    }

    public static DataSource createDataSource(String url, String username, String password) {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);

        config.setInitializationFailTimeout(0);

        return new HikariDataSource(config);
    }

    public static EntityManagerFactory createEMF(String persistenceUnitName, DataSource dataSource) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(Environment.DATASOURCE, dataSource);
        return Persistence.createEntityManagerFactory(persistenceUnitName, properties);
    }

}