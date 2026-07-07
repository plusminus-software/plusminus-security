package software.plusminus.authentication.util;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;

import java.time.Duration;
import javax.servlet.http.HttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

public class CookieUtilTest {

    @Mock
    private HttpServletResponse mockHttpServletResponse;
    @Captor
    private ArgumentCaptor<String> captorHeader;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreate() {
        CookieUtil.create(mockHttpServletResponse, "CookieName", "CookieValue",
                "example.com", Duration.ofDays(30));

        verify(mockHttpServletResponse).addHeader(eq(HttpHeaders.SET_COOKIE), captorHeader.capture());
        String actual = captorHeader.getValue();
        assertThat(actual)
                .contains("CookieName=CookieValue")
                .contains("Secure")
                .contains("HttpOnly")
                .contains("SameSite=Strict")
                .contains("Max-Age=" + Duration.ofDays(30).getSeconds())
                .contains("Path=/");
    }

    @Test
    public void testCreateOnLocalhostIsNotSecure() {
        CookieUtil.create(mockHttpServletResponse, "CookieName", "CookieValue",
                "localhost", Duration.ofDays(30));

        verify(mockHttpServletResponse).addHeader(eq(HttpHeaders.SET_COOKIE), captorHeader.capture());
        assertThat(captorHeader.getValue()).doesNotContain("Secure");
    }
}
