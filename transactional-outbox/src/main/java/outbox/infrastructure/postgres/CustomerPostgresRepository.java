package outbox.infrastructure.postgres;

import java.sql.Connection;
import java.sql.SQLException;
import outbox.core.CustomerRepository;
import outbox.core.domain.UpdateCustomer;

public class CustomerPostgresRepository implements CustomerRepository {
  private final Connection connection;

  public CustomerPostgresRepository(Connection connection) {
    this.connection = connection;
  }

  @Override
  public void updateCustomer(UpdateCustomer request) {
    try {
      var statement = connection.prepareStatement(SQL.UPDATE_CUSTOMER);
      statement.setString(1, request.name);
      statement.setString(2, request.updateUserId);
      statement.setString(3, request.tenantId);
      statement.setString(4, request.id);

      int affectedRows = statement.executeUpdate();
      if (affectedRows != 1) {
        throw new RuntimeException("Customer not found");
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private static final class SQL {
    public static final String UPDATE_CUSTOMER = """
        UPDATE customer
        SET
          name = ?,
          update_user_id = ?,
          update_time = NOW()
        WHERE tenant_id = ? AND id = ?
        """;
  }
}
