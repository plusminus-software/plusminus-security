package software.plusminus.login.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import software.plusminus.browser.Browser;
import software.plusminus.browser.BrowserSettings;
import software.plusminus.browser.Page;
import software.plusminus.security.Security;
import software.plusminus.security.service.CredentialService;
import software.plusminus.security.service.TokenProcessor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class LoginControllerSeleniumTest {

    @LocalServerPort
    private int port;
    @MockBean
    private CredentialService credentialService;
    @MockBean
    private TokenProcessor tokenProcessor;

    private String email = "test@email.com";
    private String password = "test-password";
    private Security security = Security.builder().username(email).build();
    private String token = "test-token";
    private Browser browser;

    @BeforeEach
    void beforeEach() {
        BrowserSettings browserSettings = new BrowserSettings()
                .port(port)
                .logsFilter(logEntry -> !logEntry.contains("404") && !logEntry.contains("favicon.ico"));
        browser = Browser.create(browserSettings);
        when(credentialService.provideSecurity(email, password)).thenReturn(security);
        when(tokenProcessor.getToken(security)).thenReturn(token);
        when(tokenProcessor.getSecurity(token)).thenReturn(security);
        browser.open();
        browser.cookies().clear();
    }

    @Test
    void successfulLogin() {
        Page page = browser.go("/login");

        page.find("#email").one().sendKeys(email);
        page.find("#password").one().sendKeys(password);
        page.find("#submit").one().click();

        browser.currentPage().find().byText("div", "Logged in").one();
    }

    @Test
    void badCredentials() {
        Page page = browser.go("/login");

        page.find("#email").one().sendKeys("bad@email.com");
        page.find("#password").one().sendKeys("bad-password");
        page.find("#submit").one().click();

        page.find().byText("div", "Invalid username or password!").one();
    }

    @Test
    void indexPageIsNotPublic() {
        Page page = browser.go("/");
        String body = page.find("body").one().text();
        assertThat(body).doesNotContain("Index page")
                .contains("Not Found");
    }
    
    @Test
    void explicitRedirect() {
        Page page = browser.go("/login?redirect=explicit-redirect");

        page.find("#email").one().sendKeys(email);
        page.find("#password").one().sendKeys(password);
        page.find("#submit").one().click();
        
        assertThat(browser.url()).endsWith("explicit-redirect");
    }
}
