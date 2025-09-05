package picpay.challenge.api.application.usecase;

public interface ICommand<I, O> {
    O execute(I input);
}
