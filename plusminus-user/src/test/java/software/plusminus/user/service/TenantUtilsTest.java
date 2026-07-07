package software.plusminus.user.service;

import org.hibernate.Filter;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TenantUtilsTest {

    @Mock
    private EntityManager entityManager;
    @Mock
    private Session session;
    @Mock
    private Filter filter;

    @Before
    public void setUp() {
        when(entityManager.unwrap(Session.class)).thenReturn(session);
        when(session.enableFilter("tenantFilter")).thenReturn(filter);
    }

    @Test
    public void callWithTenantReturnsValue() {
        String result = TenantUtils.callWithTenant(entityManager, "t1", () -> "value");
        assertThat(result).isEqualTo("value");
        verify(filter).setParameter("tenant", "t1");
        verify(session).disableFilter("tenantFilter");
    }

    @Test
    public void callWithTenantWrapsException() {
        assertThatThrownBy(() -> TenantUtils.callWithTenant(entityManager, "t1", () -> {
            throw new IllegalStateException("boom");
        }))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(IllegalStateException.class);
        verify(session).disableFilter("tenantFilter");
    }

    @Test
    public void runWithTenantRuns() {
        StringBuilder sb = new StringBuilder();
        TenantUtils.runWithTenant(entityManager, "t1", () -> sb.append("ran"));
        assertThat(sb.toString()).isEqualTo("ran");
        verify(filter).setParameter("tenant", "t1");
        verify(session).disableFilter("tenantFilter");
    }
}
