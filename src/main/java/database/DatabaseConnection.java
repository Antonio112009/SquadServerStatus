package database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import config.BotConfig;

import java.sql.Connection;
import java.sql.SQLException;

class DatabaseConnection {

    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    static {
        config.setJdbcUrl(BotConfig.DATABASE_URL);
        config.setUsername(BotConfig.DATABASE_USER);
        config.setPassword(BotConfig.DATABASE_PASSWORD);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setMaximumPoolSize(20);
        ds = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    DatabaseConnection(){}
}
