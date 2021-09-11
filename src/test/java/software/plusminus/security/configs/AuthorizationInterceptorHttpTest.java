package software.plusminus.security.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import software.plusminus.authentication.AuthenticationParameters;
import software.plusminus.authentication.AuthenticationService;
import software.plusminus.security.MyEntity;
import software.plusminus.security.MyEntityRepository;
import software.plusminus.security.SecurityConfig;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SecurityConfig.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AuthorizationInterceptorHttpTest {

    private static final String TEST_KEY = "test_token";

    @Autowired
    private MockMvc mvc;
    @Autowired
    private MyEntityRepository repository;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AuthenticationService authenticationService;

    @Before
    public void setUp() {
        repository.deleteAll();
    }

    @Test
    public void read_ReturnsOkStatusAndResponseBody_IfTokenInHeaderIsValid() throws Exception {
        List<MyEntity> entities = populateDatabase();
        createAuthenticationParameters(Collections.singleton("admin"));

        mvc.perform(get("/my-controller")
                .header("Authorization", "Bearer " + TEST_KEY)
                .header("Content-type", "application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(entities)));
    }

    @Test
    public void read_ReturnsForbidden_OfTokenDoesNotContainNeededRole() throws Exception {
        populateDatabase();
        createAuthenticationParameters(Collections.singleton("not_admin"));

        mvc.perform(get("/my-controller")
                .header("Authorization", "Bearer " + TEST_KEY)
                .header("Content-type", "application/json"))
                .andExpect(status().isForbidden())
                .andExpect(content().string(""));
    }
    
    private AuthenticationParameters createAuthenticationParameters(Set<String> roles) {
        AuthenticationParameters parameters = AuthenticationParameters.builder()
                .username("my_username")
                .roles(roles)
                .build();
        when(authenticationService.parseToken(TEST_KEY))
                .thenReturn(parameters);
        return parameters;
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