package software.plusminus.authentication.model;

import lombok.Getter;
import software.plusminus.security.Security;

import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class AuthenticatedRequest extends HttpServletRequestWrapper {

    @Getter
    private Security security;

    public AuthenticatedRequest(HttpServletRequest request, Security security) {
        super(request);
        this.security = security;
    }

    @Override
    public Principal getUserPrincipal() {
        return this::getRemoteUser;
    }

    @Override
    public String getRemoteUser() {
        return security.getUsername();
    }

    @Override
    public boolean isUserInRole(String role) {
        return security.getRoles().contains(role);
    }
}
