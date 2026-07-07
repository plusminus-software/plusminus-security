package software.plusminus.authentication.util;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.util.WebUtils;

import java.time.Duration;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@UtilityClass
public class CookieUtil {

    @SuppressWarnings("PMD.AvoidUsingHardCodedIP")
    public void create(HttpServletResponse httpServletResponse,
                       String name,
                       String value,
                       String domain,
                       Duration maxAge) {

        boolean secure = !"localhost".equals(domain) && !"127.0.0.1".equals(domain);
        long maxAgeSeconds = Math.min(maxAge.getSeconds(), Integer.MAX_VALUE);
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(secure)
                .sameSite("Strict")
                .maxAge(maxAgeSeconds)
                .path("/")
                .build();
        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void clear(HttpServletResponse httpServletResponse,
                      String name) {

        ResponseCookie cookie = ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .maxAge(0)
                .path("/")
                .build();
        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
