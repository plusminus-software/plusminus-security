package software.plusminus.security.configs;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import software.plusminus.authentication.AuthenticationService;

import static software.plusminus.check.Checks.check;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SecurityAutoconfig.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class SecurityAutoconfigTest {
    
    @Autowired(required = false)
    private AuthenticationService authenticationService;
    
    @Test
    public void defaultAuthenticationServiceIsPresent() {
        check(authenticationService).isNotNull();
    }
}