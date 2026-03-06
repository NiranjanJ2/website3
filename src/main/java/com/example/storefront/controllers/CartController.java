package com.example.storefront.controllers;

import com.example.storefront.models.Order;
import com.example.storefront.models.Product;
import com.example.storefront.models.User;
import com.example.storefront.services.OrderService;
import com.example.storefront.services.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class CartController {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @ModelAttribute("cart")
    public List<Product> getCart(HttpSession session) {
        List<Product> cart = (List<Product>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long productId, HttpSession session) {
        Product product = productService.getProductById(productId);
        if (product != null) {
            getCart(session).add(product);
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam Long productId, HttpSession session) {
        List<Product> cart = getCart(session);
        for (int i = 0; i < cart.size(); i++) {
            if (cart.get(i).getId().equals(productId)) {
                cart.remove(i);
                break;
            }
        }
        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        List<Product> cart = getCart(session);
        double total = cart.stream().mapToDouble(Product::getPrice).sum();
        model.addAttribute("cart", cart);
        model.addAttribute("total", total);
        model.addAttribute("user", session.getAttribute("user"));
        return "cart";
    }

    @GetMapping("/checkout")
    public String showCheckout(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        List<Product> cart = getCart(session);
        if (cart.isEmpty()) {
            return "redirect:/cart";
        }
        model.addAttribute("user", user);
        return "checkout";
    }

    @PostMapping("/checkout")
    public String processCheckout(
            @RequestParam String fullName,
            @RequestParam String address,
            @RequestParam String creditCardNumber, // VULNERABILITY: Stored as plaintext
            @RequestParam String expiry,
            @RequestParam String cvv,
            HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<Product> cart = getCart(session);
        if (cart.isEmpty()) {
            return "redirect:/cart";
        }

        double total = cart.stream().mapToDouble(Product::getPrice).sum();

        Order order = new Order();
        order.setUser(user);
        order.setProducts(new ArrayList<>(cart));
        order.setTotalAmount(total);
        order.setOrderDate(new Date());
        order.setStatus("Processing");
        order.setFullName(fullName);
        order.setAddress(address);
        order.setCreditCardNumber(creditCardNumber);
        order.setExpiry(expiry);
        order.setCvv(cvv);

        orderService.saveOrder(order);

        // Clear cart
        cart.clear();

        return "redirect:/orders";
    }
}
