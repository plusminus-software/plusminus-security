package software.plusminus.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import software.plusminus.security.Security;
import software.plusminus.user.service.UserCredentialService;

@Controller
public class TestController {
    
    @Autowired
    private UserCredentialService credentialService;
    
    @GetMapping("login")
    public String login() {
        return "login";
    }
    
    @PostMapping("/login")
    @ResponseBody
    public Security login(String email, String password) {
        return credentialService.provideSecurity(email, password);
    }
}
