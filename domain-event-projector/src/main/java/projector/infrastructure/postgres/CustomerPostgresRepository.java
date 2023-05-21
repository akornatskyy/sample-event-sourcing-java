package projector.infrastructure.postgres;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import projector.core.CustomerRepository;
import projector.core.domain.UpdateCustomer;

public class CustomerPostgresRepository implements CustomerRepository {
  private static final Logger LOGGER = LoggerFactory.getLogger(
      CustomerPostgresRepository.class);

  @Override
  public void updateCustomer(UpdateCustomer customer) {
    LOGGER.info("update customer '{}'", customer.id);
  }
}
