package software.plusminus.login.controller;

import org.junit.Test;
import org.openqa.selenium.Cookie;
import org.springframework.boot.test.mock.mockito.MockBean;
import software.plusminus.authentication.service.token.HttpTokenContext;
import software.plusminus.security.Security;
import software.plusminus.security.service.TokenProcessor;
import software.plusminus.selenium.model.WebTestOptions;
import software.plusminus.test.BrowserTest;

import static org.mockito.Mockito.when;
import static software.plusminus.check.Checks.check;

public class LogoutControllerSeleniumTest extends BrowserTest {

    private String token = "test-token";
    private String email = "test@email.com";
    private Security security = Security.builder().username(email).build();

    @MockBean
    private TokenProcessor tokenProcessor;

    @Override
    protected WebTestOptions options() {
        return super.options().logsFilter(logEntry ->
                !logEntry.getMessage().contains("404") && !logEntry.getMessage().contains("favicon.ico")
        );
    }

    @Override
    public void beforeEach() {
        super.beforeEach();
        when(tokenProcessor.getSecurity(token)).thenReturn(security);
        driver().manage().addCookie(new Cookie(HttpTokenContext.COOKIE_NAME, token));
        driver().navigate().refresh();
    }

    @Override
    protected String url() {
        return "http://localhost:" + port();
    }

    @Test
    public void logoutClearsCookies() {
        find("#logout").one().click();
        find("#logout").none();
        check(driver().manage().getCookies()).isEmpty();
    }

    @Test
    public void logoutRedirectsToIndexPage() {
        find("#logout").one().click();
        find("#logout").none();
        check(driver().getCurrentUrl()).is(buildUrl("/"));
    }
}
