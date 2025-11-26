package software.plusminus.security;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
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
    public static final String COOKIE_NAME = "JWT-TOKEN"; //TODO rename to neutral name

    private Context<HttpServletRequest> httpServletRequestContext;
    private Context<HttpServletResponse> httpServletResponseContext;

    @Nullable
    @Override
    public String getToken() {
        Optional<HttpServletRequest> request = httpServletRequestContext.optional();
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
        Optional<HttpServletResponse> response = httpServletResponseContext.optional();
        if (!response.isPresent()) {
            return false;
        }
        CookieUtil.create(response.get(),
                COOKIE_NAME,
                token,
                "localhost");
        return true;
    }

    @Override
    public void clearToken() {
        Optional<HttpServletResponse> response = httpServletResponseContext.optional();
        if (!response.isPresent()) {
            return;
        }
        CookieUtil.clear(response.get(), COOKIE_NAME);
    }
}
