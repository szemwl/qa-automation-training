package commerce.store.postgres;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicBoolean;

public class SchemaInitializer {

    private static final AtomicBoolean initialized = new AtomicBoolean(false);

    private final DbConnectionFactory connectionFactory;

    public SchemaInitializer(DbConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void initialize() {
        if (!initialized.compareAndSet(false, true)) {
            return;
        }

        try (Connection connection = connectionFactory.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute("""
                    create table if not exists orders (
                        id varchar(64) primary key,
                        status varchar(32) not null
                    )
                    """);

            statement.execute("""
                    create table if not exists processed_events (
                        event_id varchar(128) primary key
                    )
                    """);

        } catch (SQLException ex) {
            throw new RuntimeException("Не удалось инициализировать схему БД", ex);
        }
    }
}
