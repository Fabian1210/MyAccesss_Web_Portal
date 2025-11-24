package com.myaccess.myaccesswebportal.controller;

import com.myaccess.myaccesswebportal.domain.Admin;
import com.myaccess.myaccesswebportal.domain.Employee;
import com.myaccess.myaccesswebportal.domain.Manager;
import com.myaccess.myaccesswebportal.domain.User;
import com.myaccess.myaccesswebportal.dto.UserForm;
import com.myaccess.myaccesswebportal.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String listUsers(Model model,
                            @RequestParam(value = "q", required = false) String query) {

        List<User> users;
        if (query != null && !query.isBlank()) {
            users = userRepository.findByEmailContainingIgnoreCase(query);
            model.addAttribute("searchQuery", query);
        } else {
            users = userRepository.findAll();
        }

        model.addAttribute("users", users);
        return "users/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("editing", false);
        model.addAttribute("userForm", new UserForm());
        return "users/form";
    }

    @PostMapping
    public String createUser(@Valid @ModelAttribute("userForm") UserForm form,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("editing", false);
            return "users/form";
        }

        User newUser;
        String encodedPassword = passwordEncoder.encode(form.getPassword());

        switch (form.getRole()) {
            case "ADMIN" -> newUser = new Admin(
                    form.getEmail(),
                    encodedPassword,
                    true
            );
            case "MANAGER" -> newUser = new Manager(
                    form.getEmail(),
                    encodedPassword
            );
            case "EMPLOYEE" -> newUser = new Employee(
                    form.getEmail(),
                    encodedPassword
            );
            default -> {
                bindingResult.rejectValue("role", "invalid.role", "Invalid role selected.");
                model.addAttribute("editing", false);
                return "users/form";
            }
        }

        userRepository.save(newUser);
        redirectAttributes.addFlashAttribute("successMessage", "User created successfully.");
        return "redirect:/admin/users";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
            return "redirect:/admin/users";
        }

        UserForm form = new UserForm();
        form.setEmail(user.getEmail());
        form.setRole(user.getDisplayRole()); // "ADMIN"/"MANAGER"/"EMPLOYEE"
        // password left blank on purpose

        model.addAttribute("editing", true);
        model.addAttribute("userId", id);
        model.addAttribute("userForm", form);
        return "users/form";
    }

    @PostMapping("/{id}")
    public String updateUser(@PathVariable Long id,
                             @Valid @ModelAttribute("userForm") UserForm form,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {

        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
            return "redirect:/admin/users";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("editing", true);
            model.addAttribute("userId", id);
            return "users/form";
        }

        User user = userOpt.get();

        user.setEmail(form.getEmail());


        if (form.getPassword() != null && !form.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        }

        userRepository.save(user);
        redirectAttributes.addFlashAttribute("successMessage", "User updated successfully.");
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {

        if (!userRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
        } else {
            userRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully.");
        }

        return "redirect:/admin/users";
    }

    @GetMapping("/search")
    public String searchUsers(@RequestParam("q") String query) {
        return "redirect:/admin/users?q=" + query;
    }

    @GetMapping("/report")
    public String userReport(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("reportTitle", "User Accounts Report");
        model.addAttribute("generatedAt", java.time.LocalDateTime.now());
        model.addAttribute("users", users);
        return "reports/users";
    }
}