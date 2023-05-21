package outbox.infrastructure.postgres;

import java.sql.Connection;
import java.sql.SQLException;
import outbox.core.OutboxRepository;
import outbox.core.domain.AddOutboxEntry;

import javax.sql.rowset.serial.SerialBlob;

public class OutboxPostgresRepository implements OutboxRepository {
  private final Connection connection;

  public OutboxPostgresRepository(Connection connection) {
    this.connection = connection;
  }

  @Override
  public void add(AddOutboxEntry entry) {
    try {
      var statement = connection.prepareStatement(SQL.INSERT_OUTBOX);
      statement.setInt(1, entry.partition);
      statement.setString(2, entry.key);
      statement.setBlob(3, new SerialBlob(entry.data));

      int affectedRows = statement.executeUpdate();
      if (affectedRows != 1) {
        throw new RuntimeException("Unable to insert into outbox");
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private static final class SQL {
    public static final String INSERT_OUTBOX = """
        INSERT INTO outbox VALUES (?, ?, ?)
        """;
  }
}
