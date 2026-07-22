package software.plusminus.login.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import software.plusminus.authentication.service.token.HttpTokenContext;
import software.plusminus.login.TestController;
import software.plusminus.security.Security;
import software.plusminus.security.service.CredentialService;
import software.plusminus.security.service.TokenProcessor;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static software.plusminus.check.Checks.check;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class LoginControllerTest {

    @MockBean
    private CredentialService credentialService;
    @MockBean
    private TokenProcessor tokenProcessor;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private TestController testController;

    private String email = "test@email.com";
    private String password = "test-password";
    private Security security = Security.builder().username(email).build();
    private String token = "test-token";

    @BeforeEach
    void beforeEach() {
        when(credentialService.provideSecurity(email, password)).thenReturn(security);
        when(tokenProcessor.getToken(security)).thenReturn(token);
        when(tokenProcessor.getSecurity(token)).thenReturn(security);
    }

    @Test
    void loginPage() throws Exception {
        MockHttpServletResponse response = mvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .accept(MediaType.TEXT_HTML)
                        .param("email", email)
                        .param("password", password))
                .andReturn()
                .getResponse();

        check(response.getStatus()).is(HttpStatus.FOUND.value());
        check(response.getContentAsString()).is("");
        checkAuthCookie(response.getHeader(HttpHeaders.SET_COOKIE));
    }

    @Test
    void loginApi() throws Exception {
        MockHttpServletResponse response = mvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("email", email)
                        .param("password", password))
                .andReturn()
                .getResponse();

        check(response.getStatus()).is(HttpStatus.OK.value());
        check(response.getContentAsString()).is(token);
        checkAuthCookie(response.getHeader(HttpHeaders.SET_COOKIE));
    }

    @Test
    void badCredentialsPage() throws Exception {
        MockHttpServletResponse response = mvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .accept(MediaType.TEXT_HTML)
                        .param("email", "bad@email.com")
                        .param("password", "bad-password"))
                .andReturn()
                .getResponse();

        check(response.getStatus()).is(HttpStatus.OK.value());
        check(response.getHeader(HttpHeaders.SET_COOKIE)).isNull();
    }

    @Test
    void badCredentialsApi() throws Exception {
        MockHttpServletResponse response = mvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("email", "bad@email.com")
                        .param("password", "bad-password"))
                .andReturn()
                .getResponse();

        check(response.getStatus()).is(HttpStatus.UNAUTHORIZED.value());
        check(response.getContentAsString()).is("");
        check(response.getHeader(HttpHeaders.SET_COOKIE)).isNull();
    }

    private void checkAuthCookie(String setCookieHeader) {
        check(setCookieHeader).isNotNull();
        check(setCookieHeader.contains(HttpTokenContext.COOKIE_NAME + "=" + token)).isTrue();
        check(setCookieHeader.contains("HttpOnly")).isTrue();
        check(setCookieHeader.contains("SameSite=Strict")).isTrue();
    }
}
