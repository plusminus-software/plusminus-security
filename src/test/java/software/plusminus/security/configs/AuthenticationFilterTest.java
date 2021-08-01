package software.plusminus.security.configs;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import software.plusminus.authentication.AuthenticationParameters;
import software.plusminus.authentication.AuthenticationService;
import software.plusminus.context.Context;
import software.plusminus.security.properties.SecurityProperties;

import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationFilterTest {

    private static final String TOKEN = "test_token";
    private static final String USERNAME = "some_username";

    @Spy
    private SecurityProperties properties = new SecurityProperties();
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private Context<AuthenticationParameters> securityContext;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain chain;
    @Mock
    private Cookie cookie;
    @Captor
    private ArgumentCaptor<SecuredRequest> requestArgumentCaptor;
    @Captor
    private ArgumentCaptor<Cookie> cookieArgumentCaptor;

    @InjectMocks
    private AuthenticationFilter filter;

    @Test
    public void doesNotCallChain_IfUnauthenticated() throws Exception {
        filter.doFilterInternal(request, response, chain);
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    public void doesNotCallsChain_IfSecurityParametersIsNull() throws Exception {
        addCookies();
        filter.doFilterInternal(request, response, chain);
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    public void doesNotCallAuthenticationService_IfNoCookiesAndHeader() throws Exception {
        filter.doFilterInternal(request, response, chain);
        verify(authenticationService, never()).parseToken(any());
    }

    @Test
    public void sendsRedirect_IfUnauthenticated() throws Exception {
        filter.doFilterInternal(request, response, chain);
        verify(response).sendRedirect(any());
    }

    @Test
    public void callsChain_IfAuthenticated() throws Exception {
        addCookies();
        when(authenticationService.parseToken(TOKEN))
                .thenReturn(new AuthenticationParameters());

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(any(SecuredRequest.class), eq(response));
    }

    @Test
    public void callsAuthenticationService_IfCookiesPresent() throws Exception {
        addCookies();
        filter.doFilterInternal(request, response, chain);
        verify(authenticationService).parseToken(TOKEN);
    }
    
    @Test
    public void callsAuthenticationService_IfHeaderPresent() throws Exception {
        addHeader();
        filter.doFilterInternal(request, response, chain);
        verify(authenticationService).parseToken(TOKEN);
    }

    @Test
    public void clearsCookies_IfSecurityParametersIsNull() throws Exception {
        addCookies();
        filter.doFilterInternal(request, response, chain);
        verify(response).addCookie(cookieArgumentCaptor.capture());
        assertThat(cookieArgumentCaptor.getValue().getName()).isEqualTo("JWT-TOKEN");
        assertThat(cookieArgumentCaptor.getValue().getValue()).isNull();
    }

    @Test
    public void callsChainWithSecuredRequest_IfAuthenticated() throws Exception {
        addCookies();
        AuthenticationParameters parameters = new AuthenticationParameters();
        parameters.setUsername(USERNAME);
        when(authenticationService.parseToken(TOKEN)).thenReturn(parameters);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(requestArgumentCaptor.capture(), eq(response));
        assertThat(requestArgumentCaptor.getValue()).isInstanceOf(SecuredRequest.class);
        assertThat(requestArgumentCaptor.getValue().getRemoteUser()).isEqualTo(USERNAME);
    }
    
    @Test
    public void populatesSecurityContext_IfAuthenticated() throws Exception {
        addCookies();
        AuthenticationParameters parameters = new AuthenticationParameters();
        when(authenticationService.parseToken(TOKEN)).thenReturn(parameters);

        filter.doFilterInternal(request, response, chain);

        verify(securityContext).set(parameters);
    }

    @Test
    public void shouldNotFilterLoginPage() throws Exception {
        when(request.getRequestURI()).thenReturn("/login");
        boolean result = filter.shouldNotFilter(request);
        assertThat(result).isTrue();
    }

    @Test
    public void shouldNotFilterHealthEndpoint() throws Exception {
        when(request.getRequestURI()).thenReturn("/health");
        boolean result = filter.shouldNotFilter(request);
        assertThat(result).isTrue();
    }

    @Test
    public void shouldFilterRegularUrl() throws Exception {
        when(request.getRequestURI()).thenReturn("/regular");
        boolean result = filter.shouldNotFilter(request);
        assertThat(result).isFalse();
    }

    private void addCookies() {
        when(cookie.getName())
                .thenReturn("JWT-TOKEN");
        when(cookie.getValue())
                .thenReturn(TOKEN);
        when(request.getCookies())
                .thenReturn(new Cookie[]{cookie});
    }

    private void addHeader() {
        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + TOKEN);
    }

}