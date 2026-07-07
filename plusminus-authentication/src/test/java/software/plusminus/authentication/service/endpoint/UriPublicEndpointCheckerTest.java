package software.plusminus.authentication.service.endpoint;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import software.plusminus.authentication.properties.SecurityProperties;
import software.plusminus.context.Context;

import java.util.Arrays;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UriPublicEndpointCheckerTest {

    @Mock
    private Context<HttpServletRequest> httpServletRequestContext;
    @Mock
    private HttpServletRequest request;
    @Spy
    private SecurityProperties properties = new SecurityProperties();
    @InjectMocks
    private UriPublicEndpointChecker checker;

    @Test
    public void emptyContext() {
        when(httpServletRequestContext.optional()).thenReturn(Optional.empty());
        assertThat(checker.isPublicEndpoint()).isFalse();
    }

    @Test
    public void matchingUri() {
        properties.setOpenUris(Arrays.asList("/public.*"));
        when(request.getRequestURI()).thenReturn("/public/data");
        when(httpServletRequestContext.optional()).thenReturn(Optional.of(request));
        assertThat(checker.isPublicEndpoint()).isTrue();
    }

    @Test
    public void nonMatchingUri() {
        properties.setOpenUris(Arrays.asList("/public.*"));
        when(request.getRequestURI()).thenReturn("/private/data");
        when(httpServletRequestContext.optional()).thenReturn(Optional.of(request));
        assertThat(checker.isPublicEndpoint()).isFalse();
    }
}
