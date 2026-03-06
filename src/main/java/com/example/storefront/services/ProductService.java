package com.example.storefront.services;

import com.example.storefront.models.Product;
import com.example.storefront.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> searchProducts(String query) {
        return productRepository.findByCategoryContainingIgnoreCaseOrNameContainingIgnoreCase(query, query);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }
}
