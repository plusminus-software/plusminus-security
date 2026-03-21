package software.plusminus.login.controller;

import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import software.plusminus.security.Security;
import software.plusminus.security.service.CredentialService;
import software.plusminus.security.service.TokenProcessor;
import software.plusminus.selenium.model.WebTestOptions;
import software.plusminus.test.BrowserTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class LoginControllerSeleniumTest extends BrowserTest {

    private String email = "test@email.com";
    private String password = "test-password";
    private Security security = Security.builder().username(email).build();
    private String token = "test-token";

    @MockBean
    private CredentialService credentialService;
    @MockBean
    private TokenProcessor tokenProcessor;

    @Override
    protected WebTestOptions options() {
        return super.options().logsFilter(logEntry ->
                !logEntry.getMessage().contains("404") && !logEntry.getMessage().contains("favicon.ico")
        );
    }

    @Override
    protected String url() {
        return "http://localhost:" + port() + "/login";
    }

    @Override
    public void beforeEach() {
        super.beforeEach();
        when(credentialService.provideSecurity(email, password)).thenReturn(security);
        when(tokenProcessor.getToken(security)).thenReturn(token);
        when(tokenProcessor.getSecurity(token)).thenReturn(security);
        driver().manage().deleteAllCookies();
    }
    
    @Test
    public void successfulLogin() {
        find("#email").one().sendKeys(email);
        find("#password").one().sendKeys(password);
        find("#submit").one().click();
        
        find().byText("div", "Logged in").one();
    }

    @Test
    public void badCredentials() {
        find("#email").one().sendKeys("bad@email.com");
        find("#password").one().sendKeys("bad-password");
        find("#submit").one().click();

        find().byText("div", "Invalid username or password!").one();
    }

    @Test
    public void indexPageIsNotPublic() {
        go("/");
        String body = find("body").one().getText();
        assertThat(body).doesNotContain("Index page")
                .contains("Not Found");
    }
    
    @Test
    public void explicitRedirect() {
        driver().get(buildUrl("/login?redirect=explicit-redirect"));
        find("#email").one().sendKeys(email);
        find("#password").one().sendKeys(password);
        find("#submit").one().click();
        
        assertThat(driver().getCurrentUrl()).endsWith("explicit-redirect");
    }
}
