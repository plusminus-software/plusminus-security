package software.plusminus.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import software.plusminus.browser.Browser;
import software.plusminus.browser.BrowserSettings;
import software.plusminus.browser.Page;
import software.plusminus.security.Security;
import software.plusminus.user.model.User;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserCredentialServiceTest {

    @LocalServerPort
    private int port;
    @Autowired
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;

    private String tenant = "tenant1@email.com";
    private String email = "tenant1+user1@email.com";
    private String password = "test-password";
    private Browser browser;

    @BeforeEach
    void beforeEach() {
        BrowserSettings browserSettings = new BrowserSettings()
                .port(port)
                .logsFilter(logEntry -> !logEntry.contains("favicon"));
        browser = Browser.create(browserSettings);
        browser.open();
        browser.cookies().clear();
    }

    @Test
    void login() throws JsonProcessingException {
        User user = createUser();
        Page page = browser.go("/login");

        page.find("#email").one().sendKeys(email);
        page.find("#password").one().sendKeys(password);
        page.find("#submit").one().click();

        String json = browser.currentPage().find("body").one().text();
        Security security = objectMapper.readValue(json, Security.class);
        assertThat(security.getUsername()).isEqualTo(user.getUsername());
        assertThat(security.getRoles()).containsExactly("admin");
        assertThat(security.getParameters().get("tenant")).isEqualTo(user.getTenant());
        assertThat(security.getParameters().get("email")).isEqualTo(user.getEmail());
    }

    private User createUser() {
        User user = new User();
        user.setUsername(email);
        user.setEmail(email);
        user.setPassword(password);
        user.setTenant(tenant);
        user.setRoles(Collections.singleton("admin"));
        userService.register(user);
        return user;
    }
}
