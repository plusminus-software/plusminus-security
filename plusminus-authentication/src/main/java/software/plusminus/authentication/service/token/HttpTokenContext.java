package software.plusminus.authentication.service.token;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import software.plusminus.authentication.properties.SecurityProperties;
import software.plusminus.authentication.util.CookieUtil;
import software.plusminus.context.Context;
import software.plusminus.security.service.TokenContext;

import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@AllArgsConstructor
@Component
public class HttpTokenContext implements TokenContext {

    public static final String HEADER_NAME = "Authorization";
    public static final String COOKIE_NAME = "AUTH-TOKEN";

    private Context<HttpServletRequest> requestContext;
    private Context<HttpServletResponse> responseContext;
    private SecurityProperties properties;

    @Nullable
    @Override
    public String getToken() {
        Optional<HttpServletRequest> request = requestContext.optional();
        if (!request.isPresent()) {
            return null;
        }
        String token = request.get().getHeader(HEADER_NAME);
        if (token != null) {
            return token;
        }
        if (request.get().getCookies() == null) {
            return null;
        }
        return Stream.of(request.get().getCookies())
                .filter(c -> COOKIE_NAME.equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    @Override
    public boolean setToken(String token) {
        Optional<HttpServletResponse> response = responseContext.optional();
        if (!response.isPresent()) {
            return false;
        }
        boolean secure = requestContext.optional()
                .map(HttpTokenContext::isSecure)
                .orElse(true);
        CookieUtil.create(response.get(),
                COOKIE_NAME,
                token,
                secure,
                properties.getCookieMaxAge());
        return true;
    }

    private static boolean isSecure(HttpServletRequest request) {
        if (request.isSecure()) {
            return true;
        }
        String forwardedProto = request.getHeader("X-Forwarded-Proto");
        if (forwardedProto == null) {
            return false;
        }
        int comma = forwardedProto.indexOf(',');
        String proto = comma == -1 ? forwardedProto : forwardedProto.substring(0, comma);
        return "https".equalsIgnoreCase(proto.trim());
    }

    @Override
    public void clearToken() {
        Optional<HttpServletResponse> response = responseContext.optional();
        if (!response.isPresent()) {
            return;
        }
        CookieUtil.clear(response.get(), COOKIE_NAME);
    }
}
