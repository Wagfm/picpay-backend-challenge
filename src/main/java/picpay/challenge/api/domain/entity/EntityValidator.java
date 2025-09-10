package picpay.challenge.api.domain.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import picpay.challenge.api.domain.exception.ValidationException;

import java.util.Set;
import java.util.stream.Collectors;

public class EntityValidator {
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public static <T> void validate(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new ValidationException(errorMessage);
        }
    }

    private EntityValidator() {
    }
}
