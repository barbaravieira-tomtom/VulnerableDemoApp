package demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthenticationController {

    @GetMapping("/authenticate")
    public String welcome(Model model) {
        model.addAttribute("authentication", new Authentication());
        return "authenticate";
    }

    @PostMapping("/authenticate")
    public String authenticationSubmit(@ModelAttribute Authentication authentication) {
        return "authenticationresult";
    }

}
