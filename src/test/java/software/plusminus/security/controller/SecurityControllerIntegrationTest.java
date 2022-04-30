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
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.NestedServletException;
import software.plusminus.authentication.AuthenticationParameters;
import software.plusminus.authentication.AuthenticationService;
import software.plusminus.security.configs.SecurityAutoconfig;
import software.plusminus.security.model.User;
import software.plusminus.security.properties.SecurityProperties;
import software.plusminus.security.service.UserService;

import java.util.Arrays;
import java.util.Collections;
import javax.servlet.http.Cookie;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static software.plusminus.check.Checks.check;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SecurityAutoconfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class SecurityControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private SecurityProperties properties;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationService authenticationService;

    private String email = "test@mail.com";
    private String password = "testPassword";
    private String tenant = "testTenant";
    private String role = "testRole"; 
    
    @Before
    public void setUp() {
        User user = new User();
        user.setEmail(email);
        user.setUsername(email);
        user.setTenant(tenant);
        user.setRoles(Collections.singleton(role));
        user.setPassword(password);
        userService.register(user);
    }
    
    @Test
    public void login() throws Exception {
        MvcResult result = mvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("email", email),
                        new BasicNameValuePair("password", password),
                        new BasicNameValuePair("tenant", tenant))))))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/unimarket/index.html"))
                .andReturn();
        
        checkJwt(result);
    }
    
    @Test
    public void loginWithTenantFromEmail() throws Exception {
        String specificEmail = "test+taskSpecificAddress@mail.com";
        String specificTenant = email;
        User user = new User();
        user.setEmail(specificEmail);
        user.setUsername(specificEmail);
        user.setTenant(specificTenant);
        user.setRoles(Collections.singleton(role));
        user.setPassword(password);
        userService.register(user);
        
        MvcResult result = mvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("tenantFromEmail", "true")
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("email", specificEmail),
                        new BasicNameValuePair("password", password))))))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/unimarket/index.html"))
                .andReturn();

        Cookie cookie = result.getResponse().getCookie(properties.getCookieName());
        check(cookie).isNotNull();

        String jwt = cookie.getValue();
        AuthenticationParameters parameters = authenticationService.parseToken(jwt);
        check(parameters.getUsername()).is(specificEmail);
        check(parameters.getRoles()).is(role);
        check(parameters.getOtherParameters().get("tenant")).is(specificTenant);
    }
    
    @Test
    public void loginWithRelativeRedirect() throws Exception {
        MvcResult result = mvc.perform(post("/login?redirect=/test")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("email", email),
                        new BasicNameValuePair("password", password),
                        new BasicNameValuePair("tenant", tenant))))))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/test"))
                .andReturn();
        
        checkJwt(result);
    }

    @SuppressWarnings("CPD-START")
    @Test(expected = NestedServletException.class)
    public void loginWithAbsoluteRedirect_IsNotAllowed() throws Exception {
        mvc.perform(post("/login?redirect=https://spokusasnu.com.ua")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("email", email),
                        new BasicNameValuePair("password", password))))));
    }

    @Test
    public void logout() throws Exception {
        mvc.perform(post("/logout"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/"))
                .andExpect(cookie().doesNotExist(properties.getCookieName()));
    }
    
    @SuppressWarnings("CPD-END")
    private void checkJwt(MvcResult result) {
        Cookie cookie = result.getResponse().getCookie(properties.getCookieName());
        check(cookie).isNotNull();
        
        String jwt = cookie.getValue();
        AuthenticationParameters parameters = authenticationService.parseToken(jwt);
        check(parameters.getUsername()).is(email);
        check(parameters.getRoles()).is(role);
        check(parameters.getOtherParameters().get("tenant")).is(tenant);
    }
}