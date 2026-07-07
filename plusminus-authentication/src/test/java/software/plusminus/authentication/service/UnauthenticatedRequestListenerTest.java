package software.plusminus.authentication.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import software.plusminus.authentication.exception.NonPublicApiException;
import software.plusminus.authentication.exception.NotFoundException;
import software.plusminus.authentication.properties.SecurityProperties;
import software.plusminus.authentication.service.endpoint.PublicEndpointChecker;
import software.plusminus.context.Context;
import software.plusminus.scope.events.InvocationStartedEvent;
import software.plusminus.security.Security;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static java.util.Collections.enumeration;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UnauthenticatedRequestListenerTest {

    @Mock
    private Context<Security> securityContext;
    @Mock
    private Context<Object> handlerContext;
    @Mock
    private Context<HttpServletRequest> httpServletRequestContext;
    @Mock
    private Context<HttpServletResponse> httpServletResponseContext;
    @Mock
    private PublicEndpointChecker publicEndpointChecker;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private SecurityProperties properties;
    private UnauthenticatedRequestListener listener;

    @Before
    public void setUp() {
        properties = new SecurityProperties();
        listener = new UnauthenticatedRequestListener(securityContext, handlerContext,
                httpServletRequestContext, httpServletResponseContext,
                singletonList(publicEndpointChecker), properties);
    }

    private InvocationStartedEvent<HandlerMethod> event() {
        return new InvocationStartedEvent<>(null);
    }

    private void notAuthenticated() {
        when(securityContext.optional()).thenReturn(Optional.empty());
    }

    private void accept(String header) {
        Enumeration<String> headers = header == null
                ? enumeration(Collections.emptyList())
                : enumeration(singletonList(header));
        when(httpServletRequestContext.get()).thenReturn(request);
        when(request.getHeaders("accept")).thenReturn(headers);
    }

    @Test
    public void securityPresentReturns() {
        when(securityContext.optional()).thenReturn(Optional.of(Security.builder().build()));
        listener.invocationStarted(event());
    }

    @Test
    public void resourceHandlerReturns() {
        notAuthenticated();
        when(handlerContext.get()).thenReturn(new ResourceHttpRequestHandler());
        listener.invocationStarted(event());
    }

    @Test
    public void publicEndpointReturns() {
        notAuthenticated();
        when(handlerContext.get()).thenReturn(new Object());
        when(publicEndpointChecker.isPublicEndpoint()).thenReturn(true);
        listener.invocationStarted(event());
    }

    @Test
    public void nonHtmlThrows() {
        notAuthenticated();
        when(handlerContext.get()).thenReturn(new Object());
        when(publicEndpointChecker.isPublicEndpoint()).thenReturn(false);
        accept("application/json");
        assertThatThrownBy(() -> listener.invocationStarted(event()))
                .isInstanceOf(NonPublicApiException.class);
    }

    @Test
    public void htmlNoLoginPageThrows() {
        notAuthenticated();
        when(handlerContext.get()).thenReturn(new Object());
        when(publicEndpointChecker.isPublicEndpoint()).thenReturn(false);
        accept("text/html");
        properties.setLoginPage(null);
        assertThatThrownBy(() -> listener.invocationStarted(event()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void loginPageEqualsUriReturns() {
        notAuthenticated();
        when(handlerContext.get()).thenReturn(new Object());
        when(publicEndpointChecker.isPublicEndpoint()).thenReturn(false);
        accept("text/html");
        properties.setLoginPage("/login");
        when(request.getRequestURI()).thenReturn("/login");
        listener.invocationStarted(event());
    }

    @Test
    public void redirectsToLoginPage() throws Exception {
        notAuthenticated();
        when(handlerContext.get()).thenReturn(new Object());
        when(publicEndpointChecker.isPublicEndpoint()).thenReturn(false);
        accept("text/html");
        properties.setLoginPage("/login");
        when(request.getRequestURI()).thenReturn("/other");
        when(httpServletResponseContext.get()).thenReturn(response);

        InvocationStartedEvent<HandlerMethod> event = event();
        listener.invocationStarted(event);

        verify(response).sendRedirect("/login");
        assertThat(event.isIntercepted()).isTrue();
    }
}
