package software.plusminus.security.config;

import org.junit.Test;
import software.plusminus.security.service.CredentialService;
import software.plusminus.security.service.TokenContext;
import software.plusminus.security.service.TokenProcessor;

import java.util.Collections;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

public class SecurityContextCheckTest {

    private SecurityContextCheck check = new SecurityContextCheck();

    @Test
    public void passesWhenAllPresent() {
        assertThatCode(() -> check.check(processors(), credentials(), contexts()))
                .doesNotThrowAnyException();
    }

    @Test
    public void failsWithoutTokenProcessor() {
        assertThatThrownBy(() -> check.check(Collections.emptyList(), credentials(), contexts()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void failsWithoutCredentialService() {
        assertThatThrownBy(() -> check.check(processors(), Collections.emptyList(), contexts()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void failsWithoutTokenContext() {
        assertThatThrownBy(() -> check.check(processors(), credentials(), Collections.emptyList()))
                .isInstanceOf(IllegalStateException.class);
    }

    private java.util.List<TokenProcessor> processors() {
        return singletonList(mock(TokenProcessor.class));
    }

    private java.util.List<CredentialService> credentials() {
        return singletonList(mock(CredentialService.class));
    }

    private java.util.List<TokenContext> contexts() {
        return singletonList(mock(TokenContext.class));
    }
}
