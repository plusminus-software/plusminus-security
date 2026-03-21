package software.plusminus.user.service;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import software.plusminus.context.Context;
import software.plusminus.user.exception.TenantParsingException;

import java.util.concurrent.Callable;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

@ConditionalOnClass(HttpServletRequest.class)
@AllArgsConstructor
@Component
public class TenantWrapper {
    
    private Context<HttpServletRequest> httpServletRequestContext;
    private EntityManager entityManager;
    
    public <T> T callWithTenantIfNeeded(String username, Callable<T> callable) {
        if (isTenantFromEmail()) {
            String tenant = getTenantFromEmail(username);
            return TodoMoveTenantUtils.callWithTenant(entityManager, tenant, callable);
        } else {
            try {
                return callable.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    private boolean isTenantFromEmail() {
        String tenantFromEmail = httpServletRequestContext.optional()
                .map(request -> request.getParameter("tenantFromEmail"))
                .orElse(null);
        if (tenantFromEmail == null) {
            return false;
        }
        return Boolean.parseBoolean(tenantFromEmail);
    }

    private String getTenantFromEmail(String email) {
        //TODO use HostTenantProvider instead?
        int start = email.indexOf("+");
        int end = email.indexOf("@");
        if (start == -1 || end == -1 || start > end) {
            throw new TenantParsingException("Incorrect email to get tenant: '" + email + "'");
        }
        return email.substring(0, start) + email.substring(end, email.length());
    }
}
