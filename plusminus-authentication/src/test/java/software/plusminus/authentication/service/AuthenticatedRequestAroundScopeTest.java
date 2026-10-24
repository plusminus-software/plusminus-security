package software.plusminus.authentication.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import software.plusminus.authentication.model.AuthenticatedRequest;
import software.plusminus.context.Context;
import software.plusminus.security.Security;

import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static software.plusminus.check.Checks.check;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticatedRequestAroundScopeTest {

    @Mock
    private HttpServletRequest request;

    private final Context<HttpServletRequest> requestContext = Context.required();
    private final Security security = Security.builder().username("user").build();

    private AuthenticatedRequestAroundScope aroundScope(Security current) {
        return new AuthenticatedRequestAroundScope(requestContext, () -> current);
    }

    @Test
    public void supportsAnySource() {
        check(aroundScope(null).supports(new Object())).isTrue();
    }

    @Test
    public void nullSecurityKeepsRequest() {
        AtomicReference<HttpServletRequest> inside = new AtomicReference<>();

        Context.where(requestContext, request)
                .run(() -> aroundScope(null).around(() -> inside.set(requestContext.get())));

        check(inside.get()).is(request);
    }

    @Test
    public void noRequestRunsBody() {
        AtomicReference<Boolean> executed = new AtomicReference<>(false);

        Context.run(() -> aroundScope(security).around(() -> executed.set(true)));

        check(executed.get()).isTrue();
    }

    @Test
    public void nonNullSecurityReplacesRequest() {
        AtomicReference<HttpServletRequest> inside = new AtomicReference<>();

        Context.where(requestContext, request)
                .run(() -> aroundScope(security).around(() -> inside.set(requestContext.get())));

        check(inside.get() instanceof AuthenticatedRequest).isTrue();
        check(((AuthenticatedRequest) inside.get()).getSecurity()).is(security);
    }

    @Test
    public void requestIsRestoredAfterTheBlock() {
        Context.where(requestContext, request).run(() -> {
            aroundScope(security).around(() -> { });
            check(requestContext.get()).is(request);
        });
    }

    private AuthenticatedRequestAroundScope failingAroundScope() {
        return new AuthenticatedRequestAroundScope(requestContext, () -> {
            throw new IllegalStateException("boom");
        });
    }

    @Test
    public void securityFailureOnRequestDispatchIsRethrown() {
        when(request.getDispatcherType()).thenReturn(DispatcherType.REQUEST);

        Context.where(requestContext, request).run(() -> {
            assertThatThrownBy(() -> failingAroundScope().around(() -> { }))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("boom");
        });
    }

    @Test
    public void securityFailureOnErrorDispatchRunsBodyUnauthenticated() {
        when(request.getDispatcherType()).thenReturn(DispatcherType.ERROR);
        AtomicReference<HttpServletRequest> inside = new AtomicReference<>();

        Context.where(requestContext, request)
                .run(() -> failingAroundScope().around(() -> inside.set(requestContext.get())));

        check(inside.get()).is(request);
    }
}
