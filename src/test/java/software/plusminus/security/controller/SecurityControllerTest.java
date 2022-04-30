package software.plusminus.security.controller;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;
import software.plusminus.authentication.AuthenticationParameters;
import software.plusminus.authentication.AuthenticationService;
import software.plusminus.security.configs.SecurityAutoconfig;
import software.plusminus.security.properties.SecurityProperties;
import software.plusminus.security.service.LoginService;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SecurityAutoconfig.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class SecurityControllerTest {

    private static final String TEST_KEY = "test_token";

    @Autowired
    private MockMvc mvc;
    @Autowired
    private SecurityProperties properties;
    @MockBean
    private AuthenticationService authenticationService;
    @MockBean
    private LoginService loginService;

    private String email = "test@email.com";
    private String password = "testPassword";
    
    @Before
    public void setUp() {
        Map<String, Object> otherParameters = new HashMap<>();
        otherParameters.put("tenant", "testTenant");
        otherParameters.put("email", email);
        AuthenticationParameters parameters = AuthenticationParameters.builder()
                .username(email)
                .roles(Collections.singleton("testRolee"))
                .otherParameters(otherParameters)
                .build();
        when(loginService.login(email, password, null))
                .thenReturn(parameters);
        when(authenticationService.generateToken(parameters))
                .thenReturn(TEST_KEY);
        when(authenticationService.parseToken(TEST_KEY))
                .thenReturn(AuthenticationParameters.builder().build());
    }
    
    @Test
    public void login() throws Exception {
        mvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("email", email),
                        new BasicNameValuePair("password", password))))))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/unimarket/index.html"))
                .andExpect(cookie().value(properties.getCookieName(), TEST_KEY));
    }
    
    @Test
    public void loginWithRelativeRedirect() throws Exception {
        mvc.perform(post("/login?redirect=/test")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("email", email),
                        new BasicNameValuePair("password", password))))))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/test"))
                .andExpect(cookie().value(properties.getCookieName(), TEST_KEY));
    }

    @Test(expected = NestedServletException.class)
    public void loginWithAbsoluteRedirect_IsNotAllowed() throws Exception {
        mvc.perform(post("/login?redirect=https://spokusasnu.com.ua")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("email", email),
                        new BasicNameValuePair("password", password))))));
    }
    
    public void loginWithIncorrectEmail() throws Exception {
        mvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("email", "incorrectEmail"),
                        new BasicNameValuePair("password", password))))))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void logout() throws Exception {
        mvc.perform(post("/logout"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/"))
                .andExpect(cookie().doesNotExist(properties.getCookieName()));
    }
}