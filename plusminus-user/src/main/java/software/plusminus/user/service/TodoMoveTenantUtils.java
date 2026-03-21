package software.plusminus.user.service;

import lombok.experimental.UtilityClass;
import org.hibernate.Filter;
import org.hibernate.Session;

import java.util.concurrent.Callable;
import javax.persistence.EntityManager;

@UtilityClass
public class TodoMoveTenantUtils {
    
    public void runWithTenant(EntityManager entityManager, String tenant, Runnable runnable) {
        callWithTenant(entityManager, tenant, () -> {
            runnable.run();
            return null;
        });
    }

    @SuppressWarnings("PMD.CloseResource")
    public <T> T callWithTenant(EntityManager entityManager, String tenant, Callable<T> callable) {
        Session session = entityManager.unwrap(Session.class);
        try {
            Filter filter = session.enableFilter("tenantFilter");
            filter.setParameter("tenant", tenant);
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            session.disableFilter("tenantFilter");
        }
    }
    
}
