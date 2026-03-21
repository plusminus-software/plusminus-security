package software.plusminus.login.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import software.plusminus.security.service.SecurityService;

@AllArgsConstructor
@Controller
public class LogoutController {
    
    private SecurityService securityService;
    
    @PostMapping(path = "/logout", produces = "text/html")
    public String logout() {
        securityService.clearToken();
        return "redirect:/";
    }
}
