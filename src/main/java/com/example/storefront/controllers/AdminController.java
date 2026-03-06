package com.example.storefront.controllers;

import com.example.storefront.models.User;
import com.example.storefront.services.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/admin/orders")
    public String viewAllOrders(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "redirect:/"; // Redirect non-admins
        }
        model.addAttribute("orders", orderService.getAllOrders());
        model.addAttribute("user", user);
        return "admin_orders";
    }
}
