package com.example.storefront.config;

import com.example.storefront.models.Order;
import com.example.storefront.models.Product;
import com.example.storefront.models.User;
import com.example.storefront.repositories.OrderRepository;
import com.example.storefront.repositories.ProductRepository;
import com.example.storefront.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public void run(String... args) throws Exception {
        if (userService.findByUsername("admin") == null) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("admin123");
            admin.setName("Admin User");
            admin.setRole("ADMIN");
            userService.registerUser(admin);

            User user1 = new User();
            user1.setUsername("johndoe");
            user1.setPassword("password123");
            user1.setName("John Doe");
            user1.setRole("USER");
            user1 = userService.registerUser(user1);

            User user2 = new User();
            user2.setUsername("janedoe");
            user2.setPassword("password123");
            user2.setName("Jane Doe");
            user2.setRole("USER");
            user2 = userService.registerUser(user2);

            // Create products
            String[] names = {
                    "Laptop", "Smartphone", "Headphones", "Monitor", "Keyboard", "Mouse",
                    "Tablet", "Smartwatch", "Camera", "Microphone", "Speaker", "Router"
            };
            Double[] prices = { 999.99, 699.99, 199.99, 299.99, 89.99, 49.99, 399.99, 249.99, 549.99, 129.99, 149.99,
                    99.99 };
            String[] categories = { "Computers", "Phones", "Audio", "Monitors", "Accessories", "Accessories", "Tablets",
                    "Wearables", "Cameras", "Audio", "Audio", "Networking" };

            for (int i = 0; i < 12; i++) {
                Product p = new Product();
                p.setName(names[i]);
                p.setDescription("High quality " + names[i]);
                p.setPrice(prices[i]);
                p.setCategory(categories[i]);
                p.setImageUrl("https://via.placeholder.com/150");
                productRepository.save(p);
            }

            // Create sample orders
            List<Product> allProducts = productRepository.findAll();

            Order order1 = new Order();
            order1.setUser(user1);
            order1.setProducts(Arrays.asList(allProducts.get(0), allProducts.get(2)));
            order1.setTotalAmount(allProducts.get(0).getPrice() + allProducts.get(2).getPrice());
            order1.setOrderDate(new Date());
            order1.setStatus("Shipped");
            order1.setFullName("John Doe");
            order1.setAddress("123 Main St, City, ST 12345");
            order1.setCreditCardNumber("1111222233334444");
            order1.setExpiry("12/25");
            order1.setCvv("123");
            orderRepository.save(order1);

            Order order2 = new Order();
            order2.setUser(user2);
            order2.setProducts(Arrays.asList(allProducts.get(1)));
            order2.setTotalAmount(allProducts.get(1).getPrice());
            order2.setOrderDate(new Date());
            order2.setStatus("Processing");
            order2.setFullName("Jane Doe");
            order2.setAddress("456 Oak Ave, Town, ST 67890");
            order2.setCreditCardNumber("5555666677778888");
            order2.setExpiry("10/24");
            order2.setCvv("456");
            orderRepository.save(order2);
        }
    }
}
