package picpay.challenge.api.application.gateway;

public interface IAuthorizationGateway<I, O> {
    O getAuthorization(I input);
}
