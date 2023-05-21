package outbox;

import outbox.core.Context;
import outbox.core.UserIdentity;
import outbox.core.domain.UpdateCustomer;
import outbox.infrastructure.Factory;

public class Application {
  public static void main(String[] args) {
    var factory = new Factory();
    var service = factory.createOrderService();

    var user = new UserIdentity();
    user.userId = "115c0568-4ff7-401f-a98b-65bfdf65a90b";
    user.tenantId = "408a5661-d41d-4c94-9dc8-13c733da37e2";
    var ctx = new Context();
    ctx.user = user;

    var request = new UpdateCustomer();
    request.id = "18a91623-8c7d-44c6-a014-7521da88bf2c";
    request.name = "Schultz and Sons";

    service.updateCustomer(ctx, request);
  }
}