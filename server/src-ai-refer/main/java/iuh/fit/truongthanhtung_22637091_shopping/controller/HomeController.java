package iuh.fit.truongthanhtung_22637091_shopping.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping({"/", "/home"})
public class HomeController {

    @GetMapping
    public String home(Model model) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        model.addAttribute("message", "Welcome to Shopping Management System");
        model.addAttribute("currentTime", now.format(formatter));

        return "home";
    }
}
