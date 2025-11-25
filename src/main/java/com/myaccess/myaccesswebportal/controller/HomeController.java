package com.myaccess.myaccesswebportal.controller;
import com.myaccess.myaccesswebportal.repository.UserRepository;
import com.myaccess.myaccesswebportal.repository.DepartmentRepository;
import com.myaccess.myaccesswebportal.repository.ProjectRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final ProjectRepository projectRepository;

    public HomeController(UserRepository userRepository,
                          DepartmentRepository departmentRepository,
                          ProjectRepository projectRepository) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.projectRepository = projectRepository;
    }

    @GetMapping("/")
    public String root(Authentication authentication, Model model) {

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            return "home/index";
        }

        long userCount = userRepository.count();
        long departmentCount = departmentRepository.count();
        long projectCount = projectRepository.count();

        model.addAttribute("userCount", userCount);
        model.addAttribute("departmentCount", departmentCount);
        model.addAttribute("projectCount", projectCount);

        return "dashboard";
    }
}