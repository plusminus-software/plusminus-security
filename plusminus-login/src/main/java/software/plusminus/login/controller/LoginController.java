package software.plusminus.login.controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import software.plusminus.authentication.annotation.Public;
import software.plusminus.login.exception.IncorrectCredentialsException;
import software.plusminus.login.properties.LoginProperties;
import software.plusminus.security.service.SecurityService;

import javax.validation.constraints.Email;

@Public
@Validated
@AllArgsConstructor
@Controller
public class LoginController {

    public static final String RELATIVE_URI_REGEX = "^/(?![/\\\\])[^\\\\\\s]*$";

    private SecurityService securityService;
    private LoginProperties loginProperties;

    @SuppressFBWarnings(value = "SPRING_UNVALIDATED_REDIRECT",
            justification = "The redirect is validated by safeRedirect(): only targets matching "
                    + "RELATIVE_URI_REGEX (same-site paths) are used, any other value falls back to '/'.")
    @PostMapping(path = "/login", produces = "text/html")
    public String loginPage(@Email String email,
                            String password,
                            @RequestParam(required = false) String redirect,
                            Model model) {
        String token = securityService.createToken(email, password);
        if (token == null) {
            model.addAttribute("error", "Invalid username or password!");
            return loginProperties.getTemplate();
        }
        return "redirect:" + safeRedirect(redirect);
    }

    private String safeRedirect(String redirect) {
        if (redirect == null) {
            return "/";
        }
        if (redirect.matches(RELATIVE_URI_REGEX)) {
            return redirect;
        }
        String normalized = '/' + redirect;
        if (normalized.matches(RELATIVE_URI_REGEX)) {
            return normalized;
        }
        return "/";
    }

    @PostMapping(path = "/login", produces = "application/json")
    @ResponseBody
    public String loginApi(@Email String email,
                           String password) {
        String token = securityService.createToken(email, password);
        if (token == null) {
            throw new IncorrectCredentialsException("Incorrect credentials");
        }
        return token;
    }
}
