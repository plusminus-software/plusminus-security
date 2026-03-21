package software.plusminus.authorization.service.role;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import software.plusminus.security.Security;
import software.plusminus.test.IntegrationTest;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static software.plusminus.check.Checks.check;

public class RoleAuthorizerIntegrationTest extends IntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @SpyBean
    private RoleAuthorizer roleAuthorizer;

    @Test
    public void roleAllowed() {
        Security security = Security.builder()
                .username("TestUser")
                .roles(Collections.singleton("admin"))
                .build();
        security().setContext(security);

        ResponseEntity<String> result = restTemplate.getForEntity("/", String.class);

        check(result.getBody()).is("test");
        check(result.getStatusCode()).is(HttpStatus.OK);
        verify(roleAuthorizer).authorize(any(Role.class));
    }

    @Test
    public void roleIsCaseInsensitive() {
        Security security = Security.builder()
                .username("TestUser")
                .roles(Collections.singleton("ADMIN"))
                .build();
        security().setContext(security);

        ResponseEntity<String> result = restTemplate.getForEntity("/", String.class);

        check(result.getBody()).is("test");
        check(result.getStatusCode()).is(HttpStatus.OK);
        verify(roleAuthorizer).authorize(any(Role.class));
    }

    @Test
    public void roleNotAllowed() {
        Security security = Security.builder()
                .username("TestUser")
                .roles(Collections.singleton("user"))
                .build();
        security().setContext(security);

        ResponseEntity<Map> response = restTemplate.getForEntity("/", Map.class);

        checkTimestamp(response.getBody());
        check(response.getBody()).is("/forbidden-message.json");
        check(response.getStatusCode()).is(HttpStatus.FORBIDDEN);
        verify(roleAuthorizer).authorize(any(Role.class));
    }

    private void checkTimestamp(Map<String, String> errors) {
        String timestamp = errors.get("timestamp");
        ZonedDateTime time = ZonedDateTime.parse(timestamp.replace("+0000", "Z"));
        check(time).recent();
        errors.remove("timestamp");
    }
}