package software.plusminus.authentication.annotation;

import software.plusminus.authentication.validator.RegexValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = RegexValidator.class)
@Target({ElementType.FIELD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Regex {
    String message() default "'${validatedValue}' is not a valid regex";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
