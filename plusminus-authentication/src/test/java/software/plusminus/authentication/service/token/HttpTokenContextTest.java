package software.plusminus.authentication.service.token;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import software.plusminus.authentication.properties.SecurityProperties;
import software.plusminus.context.Context;

import java.util.Optional;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HttpTokenContextTest {

    @Mock
    private Context<HttpServletRequest> requestContext;
    @Mock
    private Context<HttpServletResponse> responseContext;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Captor
    private ArgumentCaptor<String> headerCaptor;

    private SecurityProperties properties;
    private HttpTokenContext tokenContext;

    @Before
    public void setUp() {
        properties = new SecurityProperties();
        tokenContext = new HttpTokenContext(requestContext, responseContext, properties);
    }

    @Test
    public void getTokenNoRequest() {
        when(requestContext.optional()).thenReturn(Optional.empty());
        assertThat(tokenContext.getToken()).isNull();
    }

    @Test
    public void getTokenFromHeader() {
        when(requestContext.optional()).thenReturn(Optional.of(request));
        when(request.getHeader(HttpTokenContext.HEADER_NAME)).thenReturn("header-token");

        assertThat(tokenContext.getToken()).isEqualTo("header-token");
    }

    @Test
    public void getTokenFromCookie() {
        when(requestContext.optional()).thenReturn(Optional.of(request));
        when(request.getHeader(HttpTokenContext.HEADER_NAME)).thenReturn(null);
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie(HttpTokenContext.COOKIE_NAME, "cookie-token")});

        assertThat(tokenContext.getToken()).isEqualTo("cookie-token");
    }

    @Test
    public void getTokenNoCookies() {
        when(requestContext.optional()).thenReturn(Optional.of(request));
        when(request.getHeader(HttpTokenContext.HEADER_NAME)).thenReturn(null);
        when(request.getCookies()).thenReturn(null);

        assertThat(tokenContext.getToken()).isNull();
    }

    @Test
    public void getTokenMissingCookie() {
        when(requestContext.optional()).thenReturn(Optional.of(request));
        when(request.getHeader(HttpTokenContext.HEADER_NAME)).thenReturn(null);
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("OTHER", "value")});

        assertThat(tokenContext.getToken()).isNull();
    }

    @Test
    public void setTokenNoResponse() {
        when(responseContext.optional()).thenReturn(Optional.empty());
        assertThat(tokenContext.setToken("token")).isFalse();
    }

    @Test
    public void setTokenWrites() {
        when(responseContext.optional()).thenReturn(Optional.of(response));
        when(requestContext.optional()).thenReturn(Optional.of(request));
        when(request.getServerName()).thenReturn("example.com");

        assertThat(tokenContext.setToken("token")).isTrue();
        verify(response).addHeader(eq(HttpHeaders.SET_COOKIE), headerCaptor.capture());
        assertThat(headerCaptor.getValue()).contains(HttpTokenContext.COOKIE_NAME + "=token");
    }

    @Test
    public void clearTokenNoResponse() {
        when(responseContext.optional()).thenReturn(Optional.empty());
        tokenContext.clearToken();
        verifyNoInteractions(response);
    }

    @Test
    public void clearTokenClears() {
        when(responseContext.optional()).thenReturn(Optional.of(response));
        tokenContext.clearToken();
        verify(response).addHeader(eq(HttpHeaders.SET_COOKIE), headerCaptor.capture());
        assertThat(headerCaptor.getValue()).contains(HttpTokenContext.COOKIE_NAME + "=");
    }
}
