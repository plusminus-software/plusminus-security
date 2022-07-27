package software.plusminus.security.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import software.plusminus.jwt.service.JwtGenerator;
import software.plusminus.security.Security;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class SecurityContextHttpTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtGenerator generator;
    
    private Security security = Security.builder()
            .username("test-username")
            .roles(Collections.singleton("admin"))
            .build();
    
    @Test
    public void goodToken() throws Exception {
        String token = generator.generateAccessToken(security);

        mvc.perform(get("/security-context")
                .header("Authorization", "Bearer " + token)
                .header("Content-type", "application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(security)));
    }
    
    @Test
    public void badToken() throws Exception {
        Security emptySecurity = Security.builder().build();
        
        mvc.perform(get("/security-context")
                .header("Authorization", "Bad token")
                .header("Content-type", "application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(emptySecurity)));
    }
}