package com.myaccess.myaccesswebportal.controller;

import com.myaccess.myaccesswebportal.domain.User;
import com.myaccess.myaccesswebportal.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/reports")
public class ReportsController {

    private final UserRepository userRepository;

    public ReportsController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public String userAccessReport(Model model) {
        List<User> users = userRepository.findAll();
        LocalDateTime generatedAt = LocalDateTime.now();

        model.addAttribute("users", users);
        model.addAttribute("generatedAt", generatedAt);

        return "reports/users";
    }
}