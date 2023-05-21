package publisher.core;

import java.util.concurrent.CompletableFuture;
import publisher.core.domain.ActivateCustomer;
import publisher.core.domain.UpdateCustomer;

public class CustomerService {
  private final DomainEventPublisher domainEventPublisher;

  public CustomerService(
      DomainEventPublisher domainEventPublisher) {
    this.domainEventPublisher = domainEventPublisher;
  }

  public CompletableFuture<Void> activateCustomer(
      Context ctx, ActivateCustomer request) {
    request.tenantId = ctx.user.tenantId;

    var event = new DomainEvent<ActivateCustomer>();
    event.type = "ActivateCustomer";
    event.data = request;

    return this.publish(ctx, event);
  }

  public CompletableFuture<Void> updateCustomer(
      Context ctx, UpdateCustomer request) {
    request.tenantId = ctx.user.tenantId;
    request.updateUserId = ctx.user.userId;

    var event = new DomainEvent<UpdateCustomer>();
    event.type = "UpdateCustomer";
    event.data = request;

    return this.publish(ctx, event);
  }

  private <T> CompletableFuture<Void> publish(
      Context ctx, DomainEvent<T> event) {
    return this.domainEventPublisher.publish(
        ctx.user.tenantId,
        event);
  }
}
