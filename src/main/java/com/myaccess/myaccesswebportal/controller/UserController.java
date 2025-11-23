package com.myaccess.myaccesswebportal.controller;

import com.myaccess.myaccesswebportal.domain.Admin;
import com.myaccess.myaccesswebportal.domain.User;
import com.myaccess.myaccesswebportal.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        // Use a concrete subclass instead of abstract User
        model.addAttribute("user", new Admin());
        return "users/form";
    }

    @PostMapping
    public String createUser(@Valid @ModelAttribute("user") User user,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "users/form";
        }

        // Do NOT call user.setCreatedAt(...) – createdAt should be set in the entity
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("successMessage", "User created successfully.");
        return "redirect:/admin/users";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
            return "redirect:/admin/users";
        }

        model.addAttribute("user", userOpt.get());
        return "users/form";
    }

    // HANDLE UPDATE
    @PostMapping("/{id}")
    public String updateUser(@PathVariable Long id,
                             @Valid @ModelAttribute("user") User user,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "users/form";
        }

        // Load existing user
        Optional<User> existingOpt = userRepository.findById(id);
        if (existingOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
            return "redirect:/admin/users";
        }

        User existing = existingOpt.get();

        // Copy over fields that are allowed to change
        existing.setEmail(user.getEmail());
        existing.setEnabled(user.isEnabled());
        existing.setPasswordHash(user.getPasswordHash());
        // if you have other mutable fields (like department, etc.), copy them too

        userRepository.save(existing);

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
        model.addAttribute("generatedAt", LocalDateTime.now());
        model.addAttribute("users", users);

        return "users/report";
    }
}