package software.plusminus.authentication.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import software.plusminus.authentication.model.AuthenticatedRequest;
import software.plusminus.context.Context;
import software.plusminus.context.WritableContext;
import software.plusminus.scope.events.ScopeStartedEvent;
import software.plusminus.security.Security;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticatedRequestListenerTest {

    @Mock
    private WritableContext<HttpServletRequest> httpServletRequestContext;
    @Mock
    private Context<Security> securityContext;
    @Mock
    private HttpServletRequest request;

    private AuthenticatedRequestListener listener;

    @Before
    public void setUp() {
        listener = new AuthenticatedRequestListener(httpServletRequestContext, securityContext);
    }

    @Test
    public void nullSecurityDoesNothing() {
        when(securityContext.get()).thenReturn(null);
        listener.scopeStarted(new ScopeStartedEvent());
        verifyNoInteractions(httpServletRequestContext);
    }

    @Test
    public void nonNullSecurityReplacesRequest() {
        when(securityContext.get()).thenReturn(Security.builder().username("user").build());
        when(httpServletRequestContext.get()).thenReturn(request);
        listener.scopeStarted(new ScopeStartedEvent());
        verify(httpServletRequestContext).replace(any(AuthenticatedRequest.class));
    }
}
