package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public MainController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/")
    public String getUserPage(Principal principal, Model model) {
        if (principal != null) {
            User user = userService.findByUsername(principal.getName());
            model.addAttribute("user", user);
        }
        return "startPage";
    }

    @GetMapping("/choice")
    public String pageChoiceAction(Model model) {
        model.addAttribute("title", "Выберите действие");
        model.addAttribute("message", "Куда вы хотите перейти?");
        return "choicePage";
    }

    @GetMapping("/admin")
    public String pageOnlyForAdmins(Model model) {
        model.addAttribute("users", userService.allUsers());
        return "adminPage";
    }

    @GetMapping("/admin/new")
    public String newUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleService.getAllRoles()); // Добавляем список ролей в модель
        return "newUser";
    }

    @PostMapping("/admin/new")
    public String addUser(@ModelAttribute("user") User user, @RequestParam List<Long> roleIds) {
        userService.addUser(user, roleIds);
        return "redirect:/admin";
    }

    @GetMapping("/admin/edit/{id}")
    public String editUserForm(@PathVariable("id") Long id, Model model) {
        User user = userService.findUserById(id);
        if (user == null) {
            return "redirect:/admin";
        }
        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.getAllRoles()); // Добавляем список ролей в модель
        return "editUser";
    }

    @PostMapping("/admin/edit")
    public String updateUser(@ModelAttribute("user") User user, @RequestParam(required = false) List<Long> roleIds) {
        if (roleIds == null) {
            roleIds = new ArrayList<>(); // Если роли не выбраны, используем пустой список
        }
        userService.updateUser(user, roleIds);
        return "redirect:/admin";
    }

    @GetMapping("/admin/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        User user = userService.findUserById(id);
        if (user != null) {
            userService.deleteUser(user);
        }
        return "redirect:/admin";
    }
}