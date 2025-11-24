package com.myaccess.myaccesswebportal.controller;

import com.myaccess.myaccesswebportal.domain.Department;
import com.myaccess.myaccesswebportal.dto.DepartmentForm;
import com.myaccess.myaccesswebportal.repository.DepartmentRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@RequestMapping("/admin/departments")
@PreAuthorize("hasRole('ADMIN')")
public class DepartmentController {

    private final DepartmentRepository departmentRepository;

    public DepartmentController(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @GetMapping
    public String listDepartments(@RequestParam(value = "q", required = false) String query,
                                  Model model) {
        List<Department> departments;

        if (query != null && !query.isBlank()) {
            departments = departmentRepository.findByNameContainingIgnoreCase(query);
        } else {
            departments = departmentRepository.findAll();
        }

        model.addAttribute("departments", departments);
        model.addAttribute("query", query == null ? "" : query);

        return "departments/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("departmentForm", new DepartmentForm());
        return "departments/form";
    }

    @PostMapping
    public String createDepartment(@Valid @ModelAttribute("departmentForm") DepartmentForm departmentForm,
                                   BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "departments/form";
        }

        Department department = new Department(departmentForm.getName().trim());
        departmentRepository.save(department);

        return "redirect:/admin/departments";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found"));

        DepartmentForm form = new DepartmentForm();
        form.setName(department.getName());

        model.addAttribute("departmentForm", form);
        model.addAttribute("departmentId", department.getId());

        return "departments/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateDepartment(@PathVariable Long id,
                                   @Valid @ModelAttribute("departmentForm") DepartmentForm departmentForm,
                                   BindingResult bindingResult,
                                   Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("departmentId", id);
            return "departments/edit";
        }

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found"));

        department.setName(departmentForm.getName().trim());
        departmentRepository.save(department);

        return "redirect:/admin/departments";
    }

    @PostMapping("/{id}/delete")
    public String deleteDepartment(@PathVariable Long id) {
        departmentRepository.deleteById(id);
        return "redirect:/admin/departments";
    }
}