package software.plusminus.login.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import software.plusminus.security.Security;
import software.plusminus.security.service.CredentialService;
import software.plusminus.security.service.TokenProcessor;
import software.plusminus.selenium.Findable;
import software.plusminus.selenium.Finder;
import software.plusminus.selenium.Selenium;
import software.plusminus.selenium.model.SeleniumOptions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class LoginControllerSeleniumTest implements Findable {

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
    private Selenium selenium = new Selenium();
    private SeleniumOptions options = new SeleniumOptions()
            .logsFilter(logEntry -> !logEntry.getMessage().contains("404")
                    && !logEntry.getMessage().contains("favicon.ico"));

    @BeforeEach
    void beforeEach() {
        when(credentialService.provideSecurity(email, password)).thenReturn(security);
        when(tokenProcessor.getToken(security)).thenReturn(token);
        when(tokenProcessor.getSecurity(token)).thenReturn(security);
        selenium.openBrowser(options);
        selenium.driver().manage().deleteAllCookies();
    }

    @Override
    public Finder find() {
        return new Finder(selenium);
    }

    @Test
    void successfulLogin() {
        selenium.loadPage(options, url("/login"));

        find("#email").one().sendKeys(email);
        find("#password").one().sendKeys(password);
        find("#submit").one().click();
        
        find().byText("div", "Logged in").one();
    }

    @Test
    void badCredentials() {
        selenium.loadPage(options, url("/login"));

        find("#email").one().sendKeys("bad@email.com");
        find("#password").one().sendKeys("bad-password");
        find("#submit").one().click();

        find().byText("div", "Invalid username or password!").one();
    }

    @Test
    void indexPageIsNotPublic() {
        selenium.loadPage(options, url("/"));
        String body = find("body").one().getText();
        assertThat(body).doesNotContain("Index page")
                .contains("Not Found");
    }
    
    @Test
    void explicitRedirect() {
        selenium.loadPage(options, url("/login?redirect=explicit-redirect"));

        find("#email").one().sendKeys(email);
        find("#password").one().sendKeys(password);
        find("#submit").one().click();
        
        assertThat(selenium.driver().getCurrentUrl()).endsWith("explicit-redirect");
    }

    private String url(String page) {
        return "http://localhost:" + port + page;
    }
}
