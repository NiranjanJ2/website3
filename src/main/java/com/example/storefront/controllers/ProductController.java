package com.example.storefront.controllers;

import com.example.storefront.models.Product;
import com.example.storefront.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public String index(Model model, @RequestParam(required = false) String query, HttpSession session) {
        if (query != null && !query.trim().isEmpty()) {
            model.addAttribute("products", productService.searchProducts(query));
            model.addAttribute("query", query);
        } else {
            model.addAttribute("products", productService.getAllProducts());
        }
        model.addAttribute("user", session.getAttribute("user"));
        return "index";
    }

    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable Long id, Model model, HttpSession session) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return "redirect:/";
        }
        model.addAttribute("product", product);
        model.addAttribute("user", session.getAttribute("user"));
        return "product";
    }
}
