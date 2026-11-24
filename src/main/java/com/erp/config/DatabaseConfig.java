package com.erp.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConfig {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConfig.class.getName());
    private static final String ARQUIVO = "db.properties";
    private static final Properties PROPS = carregar();

    private static Properties carregar() {
        Properties props = new Properties();
        try (InputStream in = DatabaseConfig.class.getClassLoader().getResourceAsStream(ARQUIVO)) {
            if (in == null) {
                throw new IllegalStateException("Arquivo " + ARQUIVO + " não encontrado. Copie db.properties.example para db.properties.");
            }
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao ler " + ARQUIVO, e);
        }
        return props;
    }

    public static String getUrl() {
        return PROPS.getProperty("db.url");
    }

    public static String getUser() {
        return PROPS.getProperty("db.user");
    }

    public static String getPassword() {
        return PROPS.getProperty("db.password");
    }

    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(getUrl(), getUser(), getPassword());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao conectar ao banco de dados", e);
            throw e;
        }
    }
}
