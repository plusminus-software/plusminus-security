package software.plusminus.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import software.plusminus.authentication.annotation.Public;
import software.plusminus.context.ClearableContext;
import software.plusminus.context.WritableContext;

import java.util.List;

@Controller
public class TestController {

    @Autowired(required = false)
    private List<ClearableContext<?>> contextsToClear;
    @Autowired(required = false)
    private List<WritableContext<?>> writableContexts;

    @Public
    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String redirect, Model model) {
        if (redirect != null) {
            model.addAttribute("redirect", redirect);
        }
        return "index";
    }

    @GetMapping("/")
    public String indexPage() {
        return "logged-in";
    }
    
    @GetMapping("/explicit-redirect")
    @ResponseBody
    public String explicitRedirect() {
        return "explicitRedirect";
    }
}
