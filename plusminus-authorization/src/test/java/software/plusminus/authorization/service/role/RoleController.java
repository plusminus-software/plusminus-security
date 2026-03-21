package software.plusminus.authorization.service.role;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoleController {
    
    @Role("admin")
    @GetMapping("/")
    public String get() {
        return "test";
    }
}
