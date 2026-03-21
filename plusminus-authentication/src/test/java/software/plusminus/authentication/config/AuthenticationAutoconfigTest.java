package software.plusminus.authentication.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import software.plusminus.authentication.properties.SecurityProperties;
import software.plusminus.authentication.service.AuthenticationAspect;

import static software.plusminus.check.Checks.check;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class AuthenticationAutoconfigTest {
    
    @Autowired(required = false)
    private AuthenticationAspect authenticationAspect;
    @Autowired(required = false)
    private SecurityProperties securityProperties;
    
    @Test
    public void defaultAuthenticationServiceIsPresent() {
        check(authenticationAspect).isNotNull();
    }
    
    @Test
    public void securityPropertiesIsPresent() {
        check(securityProperties).isNotNull();
    }
}