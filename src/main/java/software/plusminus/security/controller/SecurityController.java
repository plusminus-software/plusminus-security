package software.plusminus.security.controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import software.plusminus.authentication.AuthenticationParameters;
import software.plusminus.authentication.AuthenticationService;
import software.plusminus.security.model.User;
import software.plusminus.security.properties.SecurityProperties;
import software.plusminus.security.service.AuthenticationParametersService;
import software.plusminus.security.service.UserService;
import software.plusminus.security.util.CookieUtil;

import java.util.Collections;
import java.util.Map;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Pattern;

@Validated
@Controller
public class SecurityController {

    public static final String RELATIVE_URI_REGEX = "^(?!www\\.|(?:http|ftp)s?://|[A-Za-z]:\\\\|//).*";
    
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationParametersService authenticationParametersService;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private SecurityProperties properties;
    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("PMD.CloseResource")
    @SuppressFBWarnings(value = "SPRING_UNVALIDATED_REDIRECT",
            justification = "False-positive: the redirect is validated with @Pattern annotation")
    @PostMapping("/login")
    public String login(HttpServletResponse response,
                        String email,
                        String password,
                        @Nullable String tenant,
                        @Nullable String device,
                        @Pattern(regexp = RELATIVE_URI_REGEX) @RequestParam(required = false) String redirect,
                        Model model) {

        User user;
        
        if (tenant == null) {
            tenant = "";
        }
        Session session = entityManager.unwrap(Session.class);
        try {
            Filter filter = session.enableFilter("tenantFilter");
            filter.setParameter("tenant", tenant);
            user = userService.findUser(email, password);
        } finally {
            session.disableFilter("tenantFilter");
        }
        
        if (user == null) {
            model.addAttribute("error",
                    "Invalid username or password!");
            return "index";
        }

        Map<String, Object> additionalParameters;
        if (device == null) {
            additionalParameters = Collections.emptyMap();
        } else {
            additionalParameters = Collections.singletonMap("device", device);
        }
        AuthenticationParameters parameters =
                authenticationParametersService.createParameters(user, additionalParameters);
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
