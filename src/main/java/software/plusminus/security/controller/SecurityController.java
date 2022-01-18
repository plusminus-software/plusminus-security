package software.plusminus.security.controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import software.plusminus.authentication.AuthenticationParameters;
import software.plusminus.authentication.AuthenticationService;
import software.plusminus.security.properties.SecurityProperties;
import software.plusminus.security.service.LoginService;
import software.plusminus.security.util.CookieUtil;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Pattern;

@Validated
@Controller
public class SecurityController {

    public static final String RELATIVE_URI_REGEX = "^(?!www\\.|(?:http|ftp)s?://|[A-Za-z]:\\\\|//).*";
    
    @Autowired
    private LoginService loginService;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private SecurityProperties properties;

    @SuppressFBWarnings(value = "SPRING_UNVALIDATED_REDIRECT",
            justification = "False-positive: the redirect is validated with @Pattern annotation")
    @PostMapping("/login")
    public String login(HttpServletResponse response,
                        String email,
                        String password,
                        @Nullable String tenant,
                        @Pattern(regexp = RELATIVE_URI_REGEX) @RequestParam(required = false) String redirect,
                        Model model) {


        AuthenticationParameters parameters = loginService.login(email, password, tenant);
        if (parameters == null) {
            model.addAttribute("error",
                    "Invalid username or password!");
            return "index";
        }
        String token = authenticationService.generateToken(parameters);
        CookieUtil.create(response,
                properties.getCookieName(),
                token,
                "localhost");
        if (redirect == null) {
            return "redirect:" + properties.getLoginRedirect();
        }
        return "redirect:" + redirect;
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        CookieUtil.clear(response, properties.getCookieName());
        return "redirect:/";
    }

}
