package software.plusminus.login.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import software.plusminus.login.properties.LoginProperties;
import software.plusminus.security.service.SecurityService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LoginControllerUnitTest {

    @Mock
    private SecurityService securityService;
    @Spy
    private LoginProperties loginProperties = new LoginProperties();
    @InjectMocks
    private LoginController controller;

    @Test
    public void badCredentialsReturnConfiguredTemplate() {
        loginProperties.setTemplate("myLoginPage");
        when(securityService.createToken("email", "password")).thenReturn(null);
        Model model = new ExtendedModelMap();

        String view = controller.loginPage("email", "password", null, model);

        assertThat(view).isEqualTo("myLoginPage");
        assertThat(model.getAttribute("error")).isEqualTo("Invalid username or password!");
    }

    @Test
    public void badCredentialsDefaultToIndex() {
        when(securityService.createToken("email", "password")).thenReturn(null);
        String view = controller.loginPage("email", "password", null, new ExtendedModelMap());
        assertThat(view).isEqualTo("index");
    }

    @Test
    public void successRedirectsToRoot() {
        when(securityService.createToken("email", "password")).thenReturn("token");
        String view = controller.loginPage("email", "password", null, new ExtendedModelMap());
        assertThat(view).isEqualTo("redirect:/");
    }

    @Test
    public void successRedirectsToParam() {
        when(securityService.createToken("email", "password")).thenReturn("token");
        String view = controller.loginPage("email", "password", "/next", new ExtendedModelMap());
        assertThat(view).isEqualTo("redirect:/next");
    }

    @Test
    public void redirectRegexAcceptsSameSitePaths() {
        assertThat("/next".matches(LoginController.RELATIVE_URI_REGEX)).isTrue();
        assertThat("/path/to/page?a=1&b=2".matches(LoginController.RELATIVE_URI_REGEX)).isTrue();
        assertThat("/".matches(LoginController.RELATIVE_URI_REGEX)).isTrue();
    }

    @Test
    public void redirectRegexRejectsOpenRedirects() {
        assertThat("//evil.com".matches(LoginController.RELATIVE_URI_REGEX)).isFalse();
        assertThat("/\\evil.com".matches(LoginController.RELATIVE_URI_REGEX)).isFalse();
        assertThat("https://evil.com".matches(LoginController.RELATIVE_URI_REGEX)).isFalse();
        assertThat("https:evil.com".matches(LoginController.RELATIVE_URI_REGEX)).isFalse();
        assertThat("HTTPS://evil.com".matches(LoginController.RELATIVE_URI_REGEX)).isFalse();
        assertThat("javascript:alert(1)".matches(LoginController.RELATIVE_URI_REGEX)).isFalse();
        assertThat("next".matches(LoginController.RELATIVE_URI_REGEX)).isFalse();
        assertThat("\\\\evil.com".matches(LoginController.RELATIVE_URI_REGEX)).isFalse();
    }
}
