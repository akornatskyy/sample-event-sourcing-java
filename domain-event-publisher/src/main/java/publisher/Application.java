package publisher;

import publisher.core.Context;
import publisher.core.UserIdentity;
import publisher.core.domain.ActivateCustomer;
import publisher.core.domain.UpdateCustomer;
import publisher.infrastructure.Factory;

public class Application {
  public static void main(String[] args) {
    var factory = new Factory();
    var service = factory.createOrderService();

    var user = new UserIdentity();
    user.userId = "115c0568-4ff7-401f-a98b-65bfdf65a90b";
    user.tenantId = "408a5661-d41d-4c94-9dc8-13c733da37e2";
    var ctx = new Context();
    ctx.user = user;

    {
      var request = new UpdateCustomer();
      request.id = "18a91623-8c7d-44c6-a014-7521da88bf2c";
      request.name = "Schultz and Sons";

      var promise = service.updateCustomer(ctx, request);
      promise.join();
    }

    {
      var request = new ActivateCustomer();
      request.id = "f1594dbb-d084-4abe-ab5f-22769d996b97";

      var promise = service.activateCustomer(ctx, request);
      promise.join();
    }
  }
}