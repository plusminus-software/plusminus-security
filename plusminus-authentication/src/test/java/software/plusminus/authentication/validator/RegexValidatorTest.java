package software.plusminus.authentication.validator;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RegexValidatorTest {

    private RegexValidator validator = new RegexValidator();

    @Test
    public void validRegex() {
        assertThat(validator.isValid("[a-z]+", null)).isTrue();
    }

    @Test
    public void invalidRegex() {
        assertThat(validator.isValid("[", null)).isFalse();
    }
}
