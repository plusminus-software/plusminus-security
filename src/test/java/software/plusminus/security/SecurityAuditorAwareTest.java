package software.plusminus.security;

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
import software.plusminus.context.Context;
import software.plusminus.jwt.service.IssuerContext;
import software.plusminus.jwt.service.JwtGenerator;
import software.plusminus.security.fixtures.MyEntity;
import software.plusminus.security.fixtures.MyEntityRepository;

import java.util.Collections;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class SecurityAuditorAwareTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private MyEntityRepository repository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtGenerator generator;

    @MockBean
    private IssuerContext issuerContext;
    
    private String username = "test-username";

    @Before
    public void setUp() {
        Context.init();
        repository.deleteAll();
        repository.resetAutoIncrement();
        when(issuerContext.get()).thenReturn("localhost");
    }

    @Test
    public void usernameIsPresentInEntity() throws Exception {
        String token = getToken(Collections.singleton("admin"));
        MyEntity entity = new MyEntity();
        entity.setMyField("someField");
        
        MyEntity expected = new MyEntity();
        expected.setId(1L);
        expected.setMyField(entity.getMyField());
        expected.setUsername(username);

        mvc.perform(post("/my-controller")
                .header("Authorization", "Bearer " + token)
                .header("Content-type", "application/json")
                .content(objectMapper.writeValueAsBytes(entity)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    private String getToken(Set<String> roles) {
        Security security = Security.builder()
                .username(username)
                .roles(roles)
                .build();
        return generator.generateAccessToken(security);
    }
    
}