package software.plusminus.user.service;

import org.hibernate.Filter;
import org.hibernate.Session;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import software.plusminus.context.Context;
import software.plusminus.user.exception.TenantParsingException;

import java.util.Optional;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TenantWrapperTest {

    @Mock
    private Context<HttpServletRequest> context;
    @Mock
    private HttpServletRequest request;
    @Mock
    private EntityManager entityManager;
    @Mock
    private Session session;
    @Mock
    private Filter filter;
    @InjectMocks
    private TenantWrapper wrapper;

    private void stubTenantFromEmail(String value) {
        when(context.optional()).thenReturn(Optional.of(request));
        when(request.getParameter("tenantFromEmail")).thenReturn(value);
    }

    private void stubSession() {
        when(entityManager.unwrap(Session.class)).thenReturn(session);
        when(session.enableFilter("tenantFilter")).thenReturn(filter);
    }

    @Test
    public void noTenantFromEmailCallsDirectly() {
        when(context.optional()).thenReturn(Optional.empty());
        String result = wrapper.callWithTenantIfNeeded("tenant1+user1@email.com", () -> "value");
        assertThat(result).isEqualTo("value");
    }

    @Test
    public void tenantFromEmailFalseCallsDirectly() {
        stubTenantFromEmail("false");
        String result = wrapper.callWithTenantIfNeeded("tenant1+user1@email.com", () -> "value");
        assertThat(result).isEqualTo("value");
    }

    @Test
    public void directCallWrapsException() {
        when(context.optional()).thenReturn(Optional.empty());
        assertThatThrownBy(() -> wrapper.callWithTenantIfNeeded("email", () -> {
            throw new IllegalStateException("boom");
        }))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(IllegalStateException.class);
    }

    @Test
    public void tenantFromEmailTrueUsesTenant() {
        stubTenantFromEmail("true");
        stubSession();
        String result = wrapper.callWithTenantIfNeeded("tenant1+user1@email.com", () -> "value");
        assertThat(result).isEqualTo("value");
    }

    @Test
    public void malformedEmailWithoutPlusThrows() {
        stubTenantFromEmail("true");
        assertThatThrownBy(() -> wrapper.callWithTenantIfNeeded("user1@email.com", () -> "value"))
                .isInstanceOf(TenantParsingException.class);
    }

    @Test
    public void malformedEmailWithoutAtThrows() {
        stubTenantFromEmail("true");
        assertThatThrownBy(() -> wrapper.callWithTenantIfNeeded("tenant1+user1", () -> "value"))
                .isInstanceOf(TenantParsingException.class);
    }

    @Test
    public void malformedEmailPlusAfterAtThrows() {
        stubTenantFromEmail("true");
        assertThatThrownBy(() -> wrapper.callWithTenantIfNeeded("user1@email+.com", () -> "value"))
                .isInstanceOf(TenantParsingException.class);
    }
}
