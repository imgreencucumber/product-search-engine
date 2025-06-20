
package service;

import model.Product;

import java.util.HashMap;
import java.util.Map;

// Управляет данными о продуктах

public class DataManager {
    private Map<Integer, Product> products;

    public DataManager() {
        this.products = new HashMap<>();
    }

    public void addProduct(Product product) {
        products.put(product.getId(), product);
    }

    public Product getProductById(int id) {
        return products.get(id);
    }

    public Map<Integer, Product> getAllProducts() {
        return products;
    }
}

