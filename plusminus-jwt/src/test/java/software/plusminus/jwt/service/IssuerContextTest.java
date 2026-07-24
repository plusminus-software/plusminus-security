package software.plusminus.jwt.service;

import org.junit.jupiter.api.Test;
import software.plusminus.context.Context;
import software.plusminus.jwt.config.JwtProperties;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IssuerContextTest {

    @Test
    void usesConfiguredIssuerWhenSet() {
        JwtProperties properties = new JwtProperties();
        properties.setIssuer("https://issuer.example.com");
        IssuerContext context = new IssuerContext(Optional.empty(), properties);

        assertThat(context.get()).isEqualTo("https://issuer.example.com");
    }

    @Test
    @SuppressWarnings("unchecked")
    void fallsBackToRequestHostWhenIssuerUnset() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURL()).thenReturn(new StringBuffer("https://host.example.com/login"));
        Context<HttpServletRequest> requestContext = mock(Context.class);
        when(requestContext.optional()).thenReturn(Optional.of(request));
        IssuerContext context = new IssuerContext(Optional.of(requestContext), new JwtProperties());

        assertThat(context.get()).isEqualTo("host.example.com");
    }

    @Test
    void returnsNullWhenNoIssuerAndNoRequest() {
        IssuerContext context = new IssuerContext(Optional.empty(), new JwtProperties());

        assertThat(context.get()).isNull();
    }
}
