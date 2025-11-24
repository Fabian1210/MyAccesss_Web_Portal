package com.myaccess.myaccesswebportal.controller;

import com.myaccess.myaccesswebportal.domain.User;
import com.myaccess.myaccesswebportal.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
@PreAuthorize("isAuthenticated()")
public class ProfileController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfileController(UserRepository userRepository,
                             PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String showProfile(@AuthenticationPrincipal UserDetails principal,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        String email = principal.getUsername();

        User user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Your account could not be found.");
            return "redirect:/";
        }

        ProfileForm form = new ProfileForm();
        form.setEmail(user.getEmail());

        model.addAttribute("profileForm", form);
        model.addAttribute("currentRole", user.getRoleLabel());
        return "profile/form";
    }

    @PostMapping
    public String updateProfile(@AuthenticationPrincipal UserDetails principal,
                                @Valid @ModelAttribute("profileForm") ProfileForm form,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {

        String email = principal.getUsername();

        User user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Your account could not be found.");
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("currentRole", user.getRoleLabel());
            return "profile/form";
        }

        user.setEmail(form.getEmail());

        if (form.getPassword() != null && !form.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        }

        userRepository.save(user);

        redirectAttributes.addFlashAttribute("successMessage",
                "Profile updated successfully.");

        return "redirect:/profile";
    }
}