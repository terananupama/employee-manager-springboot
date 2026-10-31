package com.example.employee_manager.controller;

import com.example.employee_manager.model.Employee;
import com.example.employee_manager.service.EmployeeService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/employees";
    }

    @GetMapping("/employees")
    public String list(Model model) {
        model.addAttribute("employee", new Employee());
        model.addAttribute("employees", service.findAll());
        return "employees";
    }

    @PostMapping("/employees")
    public String create(
            @Valid @ModelAttribute("employee") Employee employee,
            BindingResult result,
            Model model,
            RedirectAttributes redirect) {

        if (service.emailExists(employee.getEmail(), null)) {
            result.rejectValue(
                "email", "duplicate",
                "This email is already registered"
            );
        }

        if (result.hasErrors()) {
            model.addAttribute("employees", service.findAll());
            return "employees";
        }

        service.save(employee);
        redirect.addFlashAttribute(
            "success", "Employee added successfully."
        );

        return "redirect:/employees";
    }

    @GetMapping("/employees/{id}/edit")
    public String editForm(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirect) {

        return service.findById(id)
            .map(employee -> {
                model.addAttribute("employee", employee);
                return "edit-employee";
            })
            .orElseGet(() -> {
                redirect.addFlashAttribute(
                    "error", "Employee not found."
                );
                return "redirect:/employees";
            });
    }

    @PostMapping("/employees/{id}/update")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("employee") Employee employee,
            BindingResult result,
            RedirectAttributes redirect) {

        employee.setId(id);

        if (service.emailExists(employee.getEmail(), id)) {
            result.rejectValue(
                "email", "duplicate",
                "This email is already registered"
            );
        }

        if (result.hasErrors()) {
            return "edit-employee";
        }

        service.save(employee);

        redirect.addFlashAttribute(
            "success", "Employee updated successfully."
        );

        return "redirect:/employees";
    }

    @PostMapping("/employees/{id}/delete")
    public String delete(
            @PathVariable Long id,
            RedirectAttributes redirect) {

        service.deleteById(id);

        redirect.addFlashAttribute(
            "success", "Employee deleted successfully."
        );

        return "redirect:/employees";
    }
}