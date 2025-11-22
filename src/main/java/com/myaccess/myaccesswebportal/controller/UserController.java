package com.myaccess.myaccesswebportal.controller;

import com.myaccess.myaccesswebportal.domain.Admin;
import com.myaccess.myaccesswebportal.domain.Employee;
import com.myaccess.myaccesswebportal.domain.Manager;
import com.myaccess.myaccesswebportal.domain.User;
import com.myaccess.myaccesswebportal.dto.UserForm;
import com.myaccess.myaccesswebportal.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String listUsers(@RequestParam(value = "q", required = false) String query,
                            Model model) {
        List<User> users;
        if (query != null && !query.isBlank()) {
            users = userRepository.findByEmailContainingIgnoreCase(query);
        } else {
            users = userRepository.findAll();
        }

        model.addAttribute("users", users);
        model.addAttribute("query", query == null ? "" : query);

        return "users/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("userForm", new UserForm());
        return "users/form";
    }

    @PostMapping
    public String createUser(@Valid @ModelAttribute("userForm") UserForm userForm,
                             BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "users/form";
        }

        String encodedPassword = passwordEncoder.encode(userForm.getPassword());
        User newUser;

        switch (userForm.getRole().toUpperCase()) {
            case "ADMIN" -> newUser = new Admin(userForm.getEmail(), encodedPassword);
            case "MANAGER" -> newUser = new Manager(userForm.getEmail(), encodedPassword);
            case "EMPLOYEE" -> newUser = new Employee(userForm.getEmail(), encodedPassword);
            default -> {
                bindingResult.rejectValue("role", "invalid.role", "Role must be ADMIN, MANAGER, or EMPLOYEE");
                return "users/form";
            }
        }

        userRepository.save(newUser);
        return "redirect:/users";
    }
}