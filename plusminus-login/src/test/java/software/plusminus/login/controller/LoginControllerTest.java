package software.plusminus.login.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import software.plusminus.authentication.service.token.HttpTokenContext;
import software.plusminus.security.Security;
import software.plusminus.security.service.CredentialService;
import software.plusminus.security.service.TokenProcessor;
import software.plusminus.test.IntegrationTest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static software.plusminus.check.Checks.check;

@AutoConfigureMockMvc
public class LoginControllerTest extends IntegrationTest {

    private String email = "test@email.com";
    private String password = "test-password";
    private Security security = Security.builder().username(email).build();
    private String token = "test-token";

    @MockBean
    private CredentialService credentialService;
    @MockBean
    private TokenProcessor tokenProcessor;
    @Autowired
    private MockMvc mvc;

    @Override
    public void beforeEach() {
        super.beforeEach();
        when(credentialService.provideSecurity(email, password)).thenReturn(security);
        when(tokenProcessor.getToken(security)).thenReturn(token);
        when(tokenProcessor.getSecurity(token)).thenReturn(security);
    }

    @Test
    public void loginPage() throws Exception {
        MockHttpServletResponse response = mvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .accept(MediaType.TEXT_HTML)
                        .param("email", email)
                        .param("password", password))
                .andReturn()
                .getResponse();

        check(response.getStatus()).is(HttpStatus.FOUND.value());
        check(response.getHeaderNames()).contains(HttpHeaders.SET_COOKIE);
        check(response.getContentAsString()).is("");
        check(response.getCookies()).hasSize(1);
        check(response.getCookies()[0].getName()).is(HttpTokenContext.COOKIE_NAME);
        check(response.getCookies()[0].getValue()).is(token);
    }

    @Test
    public void loginApi() throws Exception {
        MockHttpServletResponse response = mvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("email", email)
                        .param("password", password))
                .andReturn()
                .getResponse();

        check(response.getStatus()).is(HttpStatus.OK.value());
        check(response.getContentAsString()).is(token);
        check(response.getCookies()).hasSize(1);
        check(response.getCookies()[0].getName()).is(HttpTokenContext.COOKIE_NAME);
        check(response.getCookies()[0].getValue()).is(token);
    }

    @Test
    public void badCredentialsPage() throws Exception {
        MockHttpServletResponse response = mvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .accept(MediaType.TEXT_HTML)
                        .param("email", "bad@email.com")
                        .param("password", "bad-password"))
                .andReturn()
                .getResponse();

        check(response.getStatus()).is(HttpStatus.OK.value());
        check(response.getCookies()).isEmpty();
    }

    @Test
    public void badCredentialsApi() throws Exception {
        MockHttpServletResponse response = mvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("email", "bad@email.com")
                        .param("password", "bad-password"))
                .andReturn()
                .getResponse();

        check(response.getStatus()).is(HttpStatus.UNAUTHORIZED.value());
        check(response.getContentAsString()).is("");
        check(response.getCookies()).isEmpty();
    }
}
