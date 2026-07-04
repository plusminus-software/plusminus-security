package software.plusminus.login.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import software.plusminus.authentication.service.token.HttpTokenContext;
import software.plusminus.browser.Browser;
import software.plusminus.browser.BrowserSettings;
import software.plusminus.browser.Page;
import software.plusminus.security.Security;
import software.plusminus.security.service.TokenProcessor;

import static org.mockito.Mockito.when;
import static software.plusminus.check.Checks.check;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class LogoutControllerSeleniumTest {

    @LocalServerPort
    private int port;
    @MockBean
    private TokenProcessor tokenProcessor;

    private String token = "test-token";
    private String email = "test@email.com";
    private Security security = Security.builder().username(email).build();
    private Browser browser;
    private Page page;

    @BeforeEach
    void beforeEach() {
        BrowserSettings browserSettings = new BrowserSettings()
                .port(port)
                .logsFilter(logEntry -> !logEntry.contains("404") && !logEntry.contains("favicon.ico"));
        browser = Browser.create(browserSettings);
        when(tokenProcessor.getSecurity(token)).thenReturn(security);
        browser.open();
        browser.go("/");
        browser.cookies().add(HttpTokenContext.COOKIE_NAME, token);
        page = browser.refresh();
    }

    @Test
    void logoutClearsCookies() {
        page.find("#logout").one().click();
        page.find("#logout").none();

        check(browser.cookies().getAll()).isEmpty();
    }

    @Test
    void logoutRedirectsToIndexPage() {
        page.find("#logout").one().click();
        page.find("#logout").none();

        check(browser.url()).is("http://localhost:" + port + "/");
    }
}
