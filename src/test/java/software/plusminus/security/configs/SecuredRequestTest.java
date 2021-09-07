package software.plusminus.security.configs;

import org.junit.Before;
import org.junit.Test;
import software.plusminus.authentication.AuthenticationParameters;

import java.security.Principal;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class SecuredRequestTest {

    private SecuredRequest request;

    @Before
    public void setUp() {
        AuthenticationParameters parameters = AuthenticationParameters.builder()
                .username("client_id")
                .roles(Collections.singleton("present_claim"))
                .build();
        request = new SecuredRequest(mock(HttpServletRequest.class), parameters);
    }

    @Test
    public void getRemoteUser_ReturnsIdentityId() {
        String remoteUser = request.getRemoteUser();
        assertThat(remoteUser).isEqualTo("client_id");
    }

    @Test
    public void getUserPrincipal_ReturnsPrincipalThatReturnsIdentityId() {
        Principal principal = request.getUserPrincipal();
        assertThat(principal.getName()).isEqualTo("client_id");
    }

    @Test
    public void isUserInRole_ReturnsTrue_IfIdentityContainsClaim() {
        boolean inRole = request.isUserInRole("present_claim");
        assertThat(inRole).isTrue();
    }

    @Test
    public void isUserInRole_ReturnsFalse_IfIdentityDoesNotContainClaim() {
        boolean inRole = request.isUserInRole("missed_claim");
        assertThat(inRole).isFalse();
    }
}