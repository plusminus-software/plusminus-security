package software.plusminus.security.configs;

import software.plusminus.authentication.AuthenticationParameters;

import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class SecuredRequest extends HttpServletRequestWrapper {

    private AuthenticationParameters parameters;

    public SecuredRequest(HttpServletRequest request,
                          AuthenticationParameters parameters) {

        super(request);
        this.parameters = parameters;
    }

    @Override
    public Principal getUserPrincipal() {
        return this::getRemoteUser;
    }

    @Override
    public String getRemoteUser() {
        return parameters.getUsername();
    }

    @Override
    public boolean isUserInRole(String role) {
        return parameters.getRoles().contains(role);
    }
}
