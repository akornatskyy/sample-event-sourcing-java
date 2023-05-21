package projector.infrastructure.sql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import projector.core.CustomerRepository;
import projector.core.TopicCursorRepository;
import projector.core.UnitOfWork;

public class UnitOfSqlWork implements UnitOfWork {
  private static final Logger LOGGER = LoggerFactory.getLogger(UnitOfSqlWork.class);

  private final TopicCursorRepository cursorRepository;
  private final CustomerRepository customerRepository;

  public UnitOfSqlWork(
      TopicCursorRepository cursorRepository,
      CustomerRepository customerRepository) {
    this.cursorRepository = cursorRepository;
    this.customerRepository = customerRepository;
  }

  @Override
  public TopicCursorRepository cursor() {
    return cursorRepository;
  }

  @Override
  public CustomerRepository customer() {
    return customerRepository;
  }

  @Override
  public void commit() {
    LOGGER.info("commit");
  }

  @Override
  public void close() {
  }
}
