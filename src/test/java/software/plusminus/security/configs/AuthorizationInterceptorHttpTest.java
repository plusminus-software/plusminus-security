package software.plusminus.security.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import software.plusminus.jwt.service.JwtGenerator;
import software.plusminus.security.MyEntity;
import software.plusminus.security.MyEntityRepository;
import software.plusminus.security.Security;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.Cookie;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestPropertySource(properties = "security.loginPage=/login")
public class AuthorizationInterceptorHttpTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private MyEntityRepository repository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtGenerator generator;

    @Before
    public void setUp() {
        repository.deleteAll();
    }

    @Test
    public void goodTokenInHeader() throws Exception {
        List<MyEntity> entities = populateDatabase();
        String token = getToken(Collections.singleton("admin"));

        mvc.perform(get("/my-controller")
                .header("Authorization", "Bearer " + token)
                .header("Content-type", "application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(entities)));
    }
    
    @Test
    public void goodTokenInCookies() throws Exception {
        List<MyEntity> entities = populateDatabase();
        String token = getToken(Collections.singleton("admin"));

        mvc.perform(get("/my-controller")
                .header("Content-type", "application/json")
                .cookie(new Cookie("JWT-TOKEN", token)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(entities)));
    }

    @Test
    public void missedRole() throws Exception {
        String token = getToken(Collections.singleton("not_admin"));

        mvc.perform(get("/my-controller")
                .header("Authorization", "Bearer " + token)
                .header("Content-type", "application/json"))
                .andExpect(status().isForbidden())
                .andExpect(content().string(""));
    }
    
    @Test
    public void badToken() throws Exception {
        mvc.perform(get("/my-controller")
                .header("Authorization", "Bad token")
                .header("Content-type", "application/json"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(""));
    }
    
    @Test
    public void missedToken() throws Exception {
        mvc.perform(get("/my-controller")
                .header("Content-type", "application/json"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(""));
    }
    
    @Test
    public void notLoggedInUserOpensNonPublicEndpoint() throws Exception {
        mvc.perform(get("/my-controller")
                .header("Content-type", "application/json")
                .header("Accept", "text/html"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login"));
    }
    
    @Test
    public void openedUri() throws Exception {
        mvc.perform(get("/opened"))
                .andExpect(status().isOk());
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