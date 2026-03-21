package software.plusminus.authentication.validator;

import software.plusminus.authentication.annotation.Regex;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RegexValidator implements ConstraintValidator<Regex, String> {
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            Pattern.compile(value);
            return true;
        } catch (PatternSyntaxException e) {
            return false;
        }
    }
}
