package hexlet.code.repository;

import hexlet.code.model.UrlCheck;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UrlCheckRepository extends BaseRepository {
    public static void save(UrlCheck urlCheck) throws SQLException {
        String sql = "INSERT INTO url_checks (url_id, status_code, h1, title, description, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (var conn = dataSource.getConnection();
             var preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setLong(1, urlCheck.getUrlId());
            preparedStatement.setInt(2, urlCheck.getStatusCode());
            preparedStatement.setString(3, urlCheck.getH1());
            preparedStatement.setString(4, urlCheck.getTitle());
            preparedStatement.setString(5, urlCheck.getDescription());
            preparedStatement.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.executeUpdate();
            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                urlCheck.setId(generatedKeys.getLong(1));
            } else {
                throw new SQLException("DB have not returned an id after saving an entity");
            }
        }
    }
    public static Optional<UrlCheck> getLastUrlCheck(long urlId) throws SQLException {
        String sql = "SELECT * FROM url_checks WHERE url_id = ? ORDER BY created_at DESC";
        try (var conn = dataSource.getConnection();
             var preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setLong(1, urlId);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                UrlCheck urlCheck = getUrlCheckFromResultSet(resultSet);
                return Optional.of(urlCheck);
            }
            return Optional.empty();
        }
    }

    public static List<UrlCheck> getAllUrlChecks(long urlId) throws SQLException {
        String sql = "SELECT * FROM url_checks WHERE url_id = ? ORDER BY created_at DESC";
        try (var conn = dataSource.getConnection();
             var preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setLong(1, urlId);
            var resultSet = preparedStatement.executeQuery();
            var result = new ArrayList<UrlCheck>();
            while (resultSet.next()) {
                UrlCheck urlCheck = getUrlCheckFromResultSet(resultSet);
                result.add(urlCheck);
            }
            return result;
        }
    }

    private static UrlCheck getUrlCheckFromResultSet(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("id");
        long urlId = resultSet.getLong("url_id");
        int statusCode = resultSet.getInt("status_code");
        String title = resultSet.getString("title");
        String h1 = resultSet.getString("h1");
        String desc = resultSet.getString("description");
        LocalDateTime createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
        UrlCheck urlCheck = new UrlCheck(statusCode, title, h1, desc, urlId);
        urlCheck.setId(id);
        urlCheck.setCreatedAt(createdAt);
        return urlCheck;
    }
}
