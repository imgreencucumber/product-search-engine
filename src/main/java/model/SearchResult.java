package model;

// Представляет результат поиска с оценкой релевантности

public class SearchResult {
    private final Product product;
    private final double relevanceScore;
    private final String matchType;
    private final String highlightedText;
    
    public SearchResult(Product product, double relevanceScore) {
        this.product = product;
        this.relevanceScore = relevanceScore;
        this.matchType = determineMatchType(relevanceScore);
        this.highlightedText = "";
    }
    
    public SearchResult(Product product, double relevanceScore, String matchType, String highlightedText) {
        this.product = product;
        this.relevanceScore = relevanceScore;
        this.matchType = matchType;
        this.highlightedText = highlightedText;
    }
    
    private String determineMatchType(double score) {
        if (score >= 4.0) return "Exact Match";
        if (score >= 2.0) return "High Relevance";
        if (score >= 1.0) return "Medium Relevance";
        return "Low Relevance";
    }
    
    public Product getProduct() {
        return product;
    }
    
    public double getRelevanceScore() {
        return relevanceScore;
    }
    
    public String getMatchType() {
        return matchType;
    }
    
    public String getHighlightedText() {
        return highlightedText;
    }
    
    /**
     * Получение процента релевантности (0-100%)
     */
    public int getRelevancePercentage() {
        // Нормализация оценки до процентов
        return Math.min(100, (int) (relevanceScore * 10));
    }
    
    @Override
    public String toString() {
        return String.format("[%.2f★] %s (%s)", 
                relevanceScore, product.toString(), matchType);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SearchResult that = (SearchResult) obj;
        return product.equals(that.product);
    }
    
    @Override
    public int hashCode() {
        return product.hashCode();
    }
} 