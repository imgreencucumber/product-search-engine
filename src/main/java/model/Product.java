
package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

// Представляет продукт с информацией о нем

@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {
    private int id;
    
    private String name;
    
    private String description;
    private String category;
    
    @JsonProperty("price")
    private double price;
    
    private String image;

    // Конструктор по умолчанию для Jackson
    public Product() {}
    
    public Product(int id, String name, String description, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
    }
    
    public Product(int id, String name, String description, String category, double price, String image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }
    
    public double getPrice() {
        return price;
    }
    
    @JsonProperty("image")
    public String getImage() {
        return image;
    }
    
    // Сеттеры для Jackson
    public void setId(int id) {
        this.id = id;
    }
    
    @JsonProperty("title")
    public void setName(String name) {
        this.name = name;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    @JsonProperty("thumbnail")
    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Product{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", description='" + description + '\'' +
               ", category='" + category + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

