package outbox.core;

import java.util.function.Supplier;
import outbox.core.domain.AddOutboxEntry;
import outbox.core.domain.UpdateCustomer;

public class CustomerService {
  private final Supplier<UnitOfWork> unitOfWorkSupplier;
  private final DomainEventSerializer serializer;
  private final Partitioner partitioner;

  public CustomerService(
      Supplier<UnitOfWork> unitOfWorkSupplier,
      DomainEventSerializer serializer,
      Partitioner partitioner) {
    this.unitOfWorkSupplier = unitOfWorkSupplier;
    this.serializer = serializer;
    this.partitioner = partitioner;
  }

  public void updateCustomer(Context ctx, UpdateCustomer request) {
    request.tenantId = ctx.user.tenantId;
    request.updateUserId = ctx.user.userId;

    var event = new DomainEvent<UpdateCustomer>();
    event.type = "UpdateCustomer";
    event.data = request;

    var outboxEntry = new AddOutboxEntry();
    outboxEntry.partition = partitioner.partition(ctx.user.tenantId);
    outboxEntry.key = ctx.user.tenantId;
    outboxEntry.data = serializer.serialize(event);

    try (var unitOfWork = unitOfWorkSupplier.get()) {
      unitOfWork.customer().updateCustomer(request);

      unitOfWork.outbox().add(outboxEntry);

      unitOfWork.commit();
    }
  }
}
