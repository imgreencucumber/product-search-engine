package service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Product;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ProductLoader {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static List<Product> loadProductsFromJson(String resourcePath) {
        List<Product> products = new ArrayList<>();
        
        try (InputStream inputStream = ProductLoader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            
            JsonNode rootNode = objectMapper.readTree(inputStream);
            JsonNode productsNode = rootNode.get("products");
            
            if (productsNode != null && productsNode.isArray()) {
                for (JsonNode productNode : productsNode) {
                    Product product = objectMapper.treeToValue(productNode, Product.class);
                    products.add(product);
                }
            }
            
        } catch (IOException e) {
            System.err.println("Error loading products from JSON: " + e.getMessage());
            e.printStackTrace();
        }
        
        return products;
    }
} 