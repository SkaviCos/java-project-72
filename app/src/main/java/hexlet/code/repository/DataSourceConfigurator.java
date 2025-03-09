package hexlet.code.repository;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.App;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Slf4j
public class DataSourceConfigurator {
    public static void prepareDataBase(String jdbcUrl, String schemaFileName) throws Exception {
        log.trace("Begin prepare database");
        log.trace("jdbcUrl = " + jdbcUrl);
        log.trace("schemaFileName = " + schemaFileName);

        var hikariConfig = new HikariConfig();

        hikariConfig.setJdbcUrl(jdbcUrl);
        var dataSource = new HikariDataSource(hikariConfig);

        var url = App.class.getClassLoader().getResourceAsStream(schemaFileName);
        var sql = new BufferedReader(new InputStreamReader(url))
                .lines().collect(Collectors.joining("\n"));

        log.trace("sql = " + sql);

        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {
            statement.execute(sql);
        }

        BaseRepository.dataSource = dataSource;
        log.trace("Database prepared successfully");
    }
}
