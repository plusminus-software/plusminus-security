package software.plusminus.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import software.plusminus.authentication.service.token.HttpTokenContext;
import software.plusminus.jwt.service.IssuerContext;
import software.plusminus.jwt.service.JwtGenerator;
import software.plusminus.security.fixtures.MyEntity;
import software.plusminus.security.fixtures.MyEntityRepository;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = "plusminus.security.loginPage=/login")
public class AuthorizationInterceptorHttpTest {

    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private MyEntityRepository repository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtGenerator generator;

    @MockBean
    private IssuerContext issuerContext;

    @Before
    public void setUp() {
        restTemplate.getRestTemplate().setRequestFactory(noRedirectRequestFactory());
        repository.deleteAll();
        when(issuerContext.get()).thenReturn("localhost");
    }

    @Test
    public void goodTokenInHeader() throws Exception {
        List<MyEntity> entities = populateDatabase();
        HttpHeaders headers = jsonHeaders();
        headers.set("Authorization", "Bearer " + getToken(Collections.singleton("admin")));

        ResponseEntity<String> response = getMyController(headers);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(objectMapper.readTree(response.getBody()))
                .isEqualTo(objectMapper.readTree(objectMapper.writeValueAsString(entities)));
    }

    @Test
    public void goodTokenInCookies() throws Exception {
        List<MyEntity> entities = populateDatabase();
        HttpHeaders headers = jsonHeaders();
        headers.add(HttpHeaders.COOKIE, HttpTokenContext.COOKIE_NAME + "=" + getToken(Collections.singleton("admin")));

        ResponseEntity<String> response = getMyController(headers);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(objectMapper.readTree(response.getBody()))
                .isEqualTo(objectMapper.readTree(objectMapper.writeValueAsString(entities)));
    }

    @Test
    public void missedRole() {
        HttpHeaders headers = jsonHeaders();
        headers.set("Authorization", "Bearer " + getToken(Collections.singleton("not_admin")));

        ResponseEntity<String> response = getMyController(headers);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void badToken() {
        HttpHeaders headers = jsonHeaders();
        headers.set("Authorization", "Bad token");

        ResponseEntity<String> response = getMyController(headers);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void missedToken() {
        ResponseEntity<String> response = getMyController(jsonHeaders());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void notLoggedInUserOpensNonPublicEndpoint() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.TEXT_HTML));

        ResponseEntity<String> response = getMyController(headers);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation()).isNotNull();
        assertThat(response.getHeaders().getLocation().toString()).endsWith("/login");
    }

    @Test
    public void openedUri() {
        ResponseEntity<String> response = restTemplate.getForEntity(url("/opened"), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private ResponseEntity<String> getMyController(HttpHeaders headers) {
        return restTemplate.exchange(url("/my-controller"), HttpMethod.GET,
                new HttpEntity<>(headers), String.class);
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    // Do not follow redirects, so the 302 to the login page can be asserted directly.
    private static SimpleClientHttpRequestFactory noRedirectRequestFactory() {
        return new SimpleClientHttpRequestFactory() {
            @Override
            protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
                super.prepareConnection(connection, httpMethod);
                connection.setInstanceFollowRedirects(false);
            }
        };
    }

    private String getToken(Set<String> roles) {
        Security security = Security.builder()
                .username("test-username")
                .roles(roles)
                .build();
        return generator.generateAccessToken(security);
    }

    private List<MyEntity> populateDatabase() {
        MyEntity entity1 = new MyEntity();
        entity1.setMyField("Some value 1");
        MyEntity entity2 = new MyEntity();
        entity2.setMyField("Some value 2");

        return Stream.of(entity1, entity2)
                .map(repository::save)
                .collect(Collectors.toList());
    }
}
