package outbox.core;

import outbox.core.domain.UpdateCustomer;

public interface CustomerRepository {
  void updateCustomer(UpdateCustomer request);
}
