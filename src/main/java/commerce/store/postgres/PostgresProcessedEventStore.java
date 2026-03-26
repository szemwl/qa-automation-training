package commerce.store.postgres;

import commerce.store.ProcessedEventStore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PostgresProcessedEventStore implements ProcessedEventStore {

    private final DbConnectionFactory connectionFactory;

    public PostgresProcessedEventStore(DbConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public boolean markEventProcessed(String eventId) {
        String sql = """
                insert into processed_events (event_id)
                values (?)
                on conflict (event_id) do nothing
                """;

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, eventId);
            return statement.executeUpdate() > 0;

        } catch (SQLException ex) {
            throw new RuntimeException("Не удалось отметить событие как обработанное: " + eventId, ex);
        }
    }

    @Override
    public boolean isEventProcessed(String eventId) {
        String sql = """
                select 1
                from processed_events
                where event_id = ?
                """;

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, eventId);

            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException ex) {
            throw new RuntimeException("Не удалось проверить событие: " + eventId, ex);
        }
    }

    @Override
    public int processedEventsCount() {
        String sql = """
                select count(*) as cnt
                from processed_events
                """;

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("cnt");
            }
            return 0;

        } catch (SQLException ex) {
            throw new RuntimeException("Не удалось посчитать обработанные события", ex);
        }
    }
}
