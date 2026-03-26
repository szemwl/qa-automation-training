package commerce.store.postgres;

import commerce.model.Order;
import commerce.model.OrderStatus;
import commerce.store.OrderStore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class PostgresOrderStore implements OrderStore {

    private final DbConnectionFactory connectionFactory;

    public PostgresOrderStore(DbConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void save(Order order) {
        String sql = """
                insert into orders (id, status)
                values (?, ?)
                on conflict (id)
                do update set status = excluded.status
                """;

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, order.id());
            statement.setString(2, order.status().name());
            statement.executeUpdate();

        } catch (SQLException ex) {
            throw new RuntimeException("Не удалось сохранить заказ: " + order.id(), ex);
        }
    }

    @Override
    public Optional<Order> findById(String orderId) {
        String sql = """
                select id, status
                from orders
                where id = ?
                """;

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, orderId);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    String id = rs.getString("id");
                    OrderStatus status = OrderStatus.valueOf(rs.getString("status"));
                    return Optional.of(new Order(id, status));
                }
                return Optional.empty();
            }

        } catch (SQLException ex) {
            throw new RuntimeException("Не удалось найти заказ: " + orderId, ex);
        }
    }

    @Override
    public void updateStatus(String orderId, OrderStatus newStatus) {
        String sql = """
                update orders
                set status = ?
                where id = ?
                """;

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, newStatus.name());
            statement.setString(2, orderId);

            int updatedRows = statement.executeUpdate();
            if (updatedRows == 0) {
                throw new IllegalArgumentException("Заказ не найден: " + orderId);
            }

        } catch (SQLException ex) {
            throw new RuntimeException("Не удалось обновить статус заказа: " + orderId, ex);
        }
    }
}
