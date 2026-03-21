package software.plusminus.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import software.plusminus.security.Security;
import software.plusminus.selenium.model.WebTestOptions;
import software.plusminus.test.BrowserTest;
import software.plusminus.user.model.User;

import java.util.Collections;

import static software.plusminus.check.Checks.check;

public class UserCredentialServiceTest extends BrowserTest {
    
    @Autowired
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;

    private String tenant = "tenant1@email.com";
    private String email = "tenant1+user1@email.com";
    private String password = "test-password";

    @Override
    protected WebTestOptions options() {
        return super.options()
                .logsFilter(e -> !e.getMessage().contains("favicon"));
    }

    @Override
    protected String url() {
        return "/login";
    }

    @Test
    public void login() throws JsonProcessingException {
        User user = createUser();
        
        find("#email").one().sendKeys(email);
        find("#password").one().sendKeys(password);
        find("#submit").one().click();

        String json = find("body").one().getText();
        Security security = objectMapper.readValue(json, Security.class);
        check(security.getUsername()).is(user.getUsername());
        check(security.getRoles()).is("admin");
        check(security.getParameters().get("tenant")).is(user.getTenant());
        check(security.getParameters().get("email")).is(user.getEmail());
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
