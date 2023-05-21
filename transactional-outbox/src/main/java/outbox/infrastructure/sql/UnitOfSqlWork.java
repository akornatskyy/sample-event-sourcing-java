package outbox.infrastructure.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.function.Function;
import java.util.function.Supplier;
import outbox.core.CustomerRepository;
import outbox.core.OutboxRepository;
import outbox.core.UnitOfWork;

public class UnitOfSqlWork implements UnitOfWork {
  private final Connection connection;
  private final Lazy<OutboxRepository> outboxRepositoryLazy;
  private final Lazy<CustomerRepository> customerRepositoryLazy;

  private boolean done = false;
  private SQLException error;

  public UnitOfSqlWork(
      String url,
      Function<Connection, OutboxRepository> outboxRepositoryFactory,
      Function<Connection, CustomerRepository> customerRepositoryFactory) {
    this.connection = connect(url);
    this.outboxRepositoryLazy = new Lazy<>(
        () -> outboxRepositoryFactory.apply(connection));
    this.customerRepositoryLazy = new Lazy<>(
        () -> customerRepositoryFactory.apply(connection));
  }

  @Override
  public OutboxRepository outbox() {
    return outboxRepositoryLazy.get();
  }

  public CustomerRepository customer() {
    return customerRepositoryLazy.get();
  }

  public void commit() {
    try {
      this.connection.commit();
      this.done = true;
    } catch (SQLException e) {
      this.error = e;
    }
  }

  @Override
  public void close() {
    try {
      if (!done) {
        this.connection.rollback();
      }

      if (error != null) {
        throw error;
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } finally {
      try {
        this.connection.setAutoCommit(true);
      } catch (SQLException e) {
        //
      }

      try {
        this.connection.close();
      } catch (SQLException e) {
        //
      }
    }
  }

  private static Connection connect(String url) {
    try {
      var connection = DriverManager.getConnection(url);
      connection.setAutoCommit(false);
      return connection;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  static class Lazy<T> {
    private final Supplier<T> supplier;
    private T value;

    Lazy(Supplier<T> supplier) {
      this.supplier = supplier;
    }

    public T get() {
      if (value == null) {
        value = this.supplier.get();
      }

      return value;
    }
  }
}
