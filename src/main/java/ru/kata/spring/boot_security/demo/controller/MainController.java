package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.UserService;

import java.security.Principal;
import java.util.List;

@Controller
public class MainController {

    private final UserService userService;

    @Autowired
    public MainController(UserService userService) {
        this.userService = userService;
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

    // Отображение формы для добавления нового пользователя
    @GetMapping("/admin/new")
    public String newUserForm(Model model) {
        model.addAttribute("user", new User());
        return "newUser";
    }

    // Обработка формы для добавления нового пользователя
    @PostMapping("/admin/new")
    public String addUser(@ModelAttribute("user") User user) {
        userService.addUser(user);
        return "redirect:/admin";
    }

    // Отображение формы для редактирования пользователя
    @GetMapping("/admin/edit/{id}")
    public String editUserForm(@PathVariable("id") Long id, Model model) {
        User user = userService.findUserById(id);
        if (user == null) {
            return "redirect:/admin";
        }
        model.addAttribute("user", user);
        return "editUser";
    }

    // Обработка формы для редактирования пользователя
    @PostMapping("/admin/edit")
    public String updateUser(@ModelAttribute("user") User user) {
        userService.updateUser(user);
        return "redirect:/admin";
    }

    // Удаление пользователя
    @GetMapping("/admin/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        User user = userService.findUserById(id);
        if (user != null) {
            userService.deleteUser(user);
        }
        return "redirect:/admin";
    }
}