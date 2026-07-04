package software.plusminus.login.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import software.plusminus.authentication.service.token.HttpTokenContext;
import software.plusminus.security.Security;
import software.plusminus.security.service.TokenProcessor;
import software.plusminus.selenium.Findable;
import software.plusminus.selenium.Finder;
import software.plusminus.selenium.Selenium;
import software.plusminus.selenium.model.SeleniumOptions;

import static org.mockito.Mockito.when;
import static software.plusminus.check.Checks.check;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class LogoutControllerSeleniumTest implements Findable {

    @LocalServerPort
    private int port;
    @MockBean
    private TokenProcessor tokenProcessor;

    private String token = "test-token";
    private String email = "test@email.com";
    private Security security = Security.builder().username(email).build();
    private Selenium selenium = new Selenium();
    private SeleniumOptions options = new SeleniumOptions()
            .logsFilter(logEntry -> !logEntry.getMessage().contains("404")
                    && !logEntry.getMessage().contains("favicon.ico"));

    @BeforeEach
    void beforeEach() {
        when(tokenProcessor.getSecurity(token)).thenReturn(security);
        selenium.openBrowser(options);
        selenium.loadPage(options, "http://localhost:" + port);
        selenium.driver().manage().addCookie(new Cookie(HttpTokenContext.COOKIE_NAME, token));
        selenium.driver().navigate().refresh();
    }

    @Override
    public Finder find() {
        return new Finder(selenium);
    }

    @Test
    void logoutClearsCookies() {
        find("#logout").one().click();
        find("#logout").none();

        check(selenium.driver().manage().getCookies()).isEmpty();
    }

    @Test
    void logoutRedirectsToIndexPage() {
        find("#logout").one().click();
        find("#logout").none();

        check(selenium.driver().getCurrentUrl()).is("http://localhost:" + port + "/");
    }
}
