package picpay.challenge.api.application.gateway;

public interface INotificationGateway<I, O> {
    O notify(I input);
}
