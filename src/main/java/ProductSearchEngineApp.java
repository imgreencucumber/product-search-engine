import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.fasterxml.jackson.databind.ObjectMapper;

import index.InvertedIndex;
import index.Trie;
import model.Product;
import model.SearchResult;
import service.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Production-ready Smart Product Search Engine Application
 * 
 * Features:
 * - Advanced search algorithms (Inverted Index, Boyer-Moore, Levenshtein Distance, Trie)
 * - RESTful API endpoints
 * - Real-time autocomplete
 * - Modern web interface
 * - JSON-based product data
 */
public class ProductSearchEngineApp {
    private SmartSearchEngine smartSearchEngine;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final int DEFAULT_PORT = 8080;
    private static final String PRODUCTS_FILE = "products.json";
    private final int port;

    public ProductSearchEngineApp() {
        this(DEFAULT_PORT);
    }
    
    public ProductSearchEngineApp(int port) {
        this.port = port;
        initializeSystem();
    }

    private void initializeSystem() {
        System.out.println("🚀 Initializing Web Search Engine...");
        
        // Initialize components
        InvertedIndex invertedIndex = new InvertedIndex();
        Trie trie = new Trie();
        DataManager dataManager = new DataManager();
        Indexer indexer = new Indexer(invertedIndex, trie);
        
        // Load products from JSON
        List<Product> products = ProductLoader.loadProductsFromJson(PRODUCTS_FILE);
        System.out.println("📦 Loaded " + products.size() + " products from JSON");
        
        // Add products to data manager
        for (Product product : products) {
            dataManager.addProduct(product);
        }
        
        // Index the data
        indexer.indexProducts(dataManager.getAllProducts().values());
        
        // Initialize smart search engine
        smartSearchEngine = new SmartSearchEngine(invertedIndex, trie, dataManager);
        
        System.out.println("✅ Web Search Engine initialized successfully!");
        System.out.println("📊 Indexed " + dataManager.getAllProducts().size() + " products");
    }

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        
        // Serve static HTML page
        server.createContext("/", new StaticHandler());
        
        // API endpoint for search
        server.createContext("/api/search", new SearchHandler());
        
        // API endpoint for autocomplete
        server.createContext("/api/autocomplete", new AutocompleteHandler());
        
        server.setExecutor(null);
        server.start();
        
        System.out.println("🌐 Web server started at http://localhost:" + port);
        System.out.println("🔍 Search engine is ready for queries!");
    }

    class StaticHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String htmlContent = getHtmlContent();
            
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, htmlContent.getBytes(StandardCharsets.UTF_8).length);
            
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(htmlContent.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    class AutocompleteHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Enable CORS
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
            
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, 0);
                exchange.getResponseBody().close();
                return;
            }

            String query = "";
            if ("GET".equals(exchange.getRequestMethod())) {
                String queryString = exchange.getRequestURI().getQuery();
                if (queryString != null) {
                    Map<String, String> params = parseQuery(queryString);
                    query = params.getOrDefault("q", "");
                }
            }

            List<String> suggestions = smartSearchEngine.getSearchSuggestions(query);
            
            // Convert suggestions to JSON
            String jsonResponse = objectMapper.writeValueAsString(suggestions);
            
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(200, jsonResponse.getBytes(StandardCharsets.UTF_8).length);
            
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(jsonResponse.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    class SearchHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Enable CORS
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
            
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, 0);
                exchange.getResponseBody().close();
                return;
            }

            String query = "";
            if ("GET".equals(exchange.getRequestMethod())) {
                String queryString = exchange.getRequestURI().getQuery();
                if (queryString != null) {
                    Map<String, String> params = parseQuery(queryString);
                    query = params.getOrDefault("q", "");
                }
            }

            List<SearchResult> results = smartSearchEngine.smartSearch(query);
            
            // Convert results to JSON
            String jsonResponse = objectMapper.writeValueAsString(results);
            
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(200, jsonResponse.getBytes(StandardCharsets.UTF_8).length);
            
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(jsonResponse.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    private Map<String, String> parseQuery(String query) {
        Map<String, String> result = new HashMap<>();
        if (query != null) {
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                if (pair.length == 2) {
                    try {
                        result.put(URLDecoder.decode(pair[0], StandardCharsets.UTF_8.name()),
                                 URLDecoder.decode(pair[1], StandardCharsets.UTF_8.name()));
                    } catch (Exception e) {
                        // Skip malformed parameters
                    }
                }
            }
        }
        return result;
    }

    private String getHtmlContent() {
        return """
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Smart Search Engine</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
        }

        .container {
            max-width: 1200px;
            margin: 0 auto;
        }

        .header {
            text-align: center;
            margin-bottom: 40px;
            color: white;
        }

        .header h1 {
            font-size: 2.5rem;
            margin-bottom: 10px;
            font-weight: 300;
        }

        .header p {
            font-size: 1.1rem;
            opacity: 0.9;
        }

        .search-box {
            position: relative;
            z-index: 1000;
            background: rgba(255, 255, 255, 0.1);
            backdrop-filter: blur(10px);
            border-radius: 20px;
            padding: 30px;
            margin-bottom: 30px;
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
        }

        .search-input {
            width: 100%;
            padding: 15px 20px;
            font-size: 1.1rem;
            border: none;
            border-radius: 50px;
            background: white;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
            outline: none;
            transition: all 0.3s ease;
        }

        .search-input:focus {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
        }

        .search-input::placeholder {
            color: #999;
        }

        .autocomplete-container {
            position: relative;
            z-index: 1000;
        }

        .autocomplete-suggestions {
            position: absolute;
            top: 100%;
            left: 0;
            right: 0;
            background: white;
            border-radius: 15px;
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
            max-height: 200px;
            overflow-y: auto;
            z-index: 1000;
            margin-top: 5px;
            display: none;
        }

        .autocomplete-item {
            padding: 12px 20px;
            cursor: pointer;
            border-bottom: 1px solid #f0f0f0;
            transition: background-color 0.2s ease;
        }

        .autocomplete-item:last-child {
            border-bottom: none;
        }

        .autocomplete-item:hover,
        .autocomplete-item.active {
            background-color: #f8f9fa;
        }

        .results {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
            gap: 20px;
        }

        .product-card {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(10px);
            border-radius: 15px;
            padding: 20px;
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
            transition: all 0.3s ease;
            border: 1px solid rgba(255, 255, 255, 0.2);
        }

        .product-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 15px 40px rgba(0, 0, 0, 0.15);
        }

        .product-image {
            width: 100%;
            height: 200px;
            object-fit: contain;
            border-radius: 10px;
            margin-bottom: 15px;
            background: #f8f9fa;
        }

        .product-title {
            font-size: 1.2rem;
            font-weight: 600;
            margin-bottom: 10px;
            color: #333;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }

        .product-description {
            color: #666;
            font-size: 0.9rem;
            line-height: 1.4;
            margin-bottom: 15px;
            display: -webkit-box;
            -webkit-line-clamp: 3;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }

        .product-meta {
            display: flex;
            justify-content: space-between;
            align-items: center;
            font-size: 0.9rem;
        }

        .product-price {
            font-weight: 600;
            color: #667eea;
            font-size: 1.1rem;
        }

        .product-category {
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: white;
            padding: 4px 12px;
            border-radius: 15px;
            font-size: 0.8rem;
            text-transform: capitalize;
        }

        .relevance-score {
            background: #e8f5e8;
            color: #2d5a2d;
            padding: 4px 8px;
            border-radius: 10px;
            font-size: 0.8rem;
            font-weight: 500;
        }

        .no-results {
            text-align: center;
            color: white;
            font-size: 1.2rem;
            margin-top: 50px;
            opacity: 0.8;
        }

        .loading {
            text-align: center;
            color: white;
            font-size: 1.1rem;
            margin-top: 30px;
        }

        @media (max-width: 768px) {
            .header h1 {
                font-size: 2rem;
            }
            
            .search-box {
                padding: 20px;
            }
            
            .results {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>🔍 Smart Search Engine</h1>
            <p>Умный поиск товаров с использованием современных алгоритмов</p>
        </div>

        <div class="search-box">
            <div class="autocomplete-container">
                <input type="text" class="search-input" placeholder="Введите запрос для поиска товаров..." id="searchInput">
                <div id="autocompleteSuggestions" class="autocomplete-suggestions"></div>
            </div>
        </div>

        <div id="results" class="results"></div>
        <div id="loading" class="loading" style="display: none;">⏳ Поиск...</div>
        <div id="noResults" class="no-results" style="display: none;">
            😔 По вашему запросу ничего не найдено.<br>
            Попробуйте изменить поисковый запрос.
        </div>
    </div>

    <script>
        const searchInput = document.getElementById('searchInput');
        const resultsContainer = document.getElementById('results');
        const loadingElement = document.getElementById('loading');
        const noResultsElement = document.getElementById('noResults');
        const autocompleteSuggestions = document.getElementById('autocompleteSuggestions');

        let searchTimeout;
        let autocompleteTimeout;
        let selectedSuggestionIndex = -1;

        searchInput.addEventListener('input', function() {
            clearTimeout(searchTimeout);
            clearTimeout(autocompleteTimeout);
            
            const query = this.value.trim();
            
            // Show autocomplete suggestions
            if (query.length >= 2) {
                autocompleteTimeout = setTimeout(() => {
                    loadAutocomplete(query);
                }, 200);
            } else {
                hideAutocomplete();
            }
            
            // Perform search
            searchTimeout = setTimeout(() => {
                performSearch();
            }, 300);
        });

        searchInput.addEventListener('keydown', function(e) {
            const suggestions = autocompleteSuggestions.children;
            
            if (e.key === 'ArrowDown') {
                e.preventDefault();
                selectedSuggestionIndex = Math.min(selectedSuggestionIndex + 1, suggestions.length - 1);
                updateSelectedSuggestion();
            } else if (e.key === 'ArrowUp') {
                e.preventDefault();
                selectedSuggestionIndex = Math.max(selectedSuggestionIndex - 1, -1);
                updateSelectedSuggestion();
            } else if (e.key === 'Enter') {
                e.preventDefault();
                if (selectedSuggestionIndex >= 0 && suggestions[selectedSuggestionIndex]) {
                    selectSuggestion(suggestions[selectedSuggestionIndex].textContent);
                } else {
                    clearTimeout(searchTimeout);
                    hideAutocomplete();
                    performSearch();
                }
            } else if (e.key === 'Escape') {
                hideAutocomplete();
            }
        });

        async function performSearch() {
            const query = searchInput.value.trim();
            
            if (query.length === 0) {
                resultsContainer.innerHTML = '';
                hideElements();
                return;
            }

            showLoading();

            try {
                const response = await fetch(`/api/search?q=${encodeURIComponent(query)}`);
                const results = await response.json();
                
                hideElements();
                displayResults(results);
            } catch (error) {
                console.error('Search error:', error);
                hideElements();
                showNoResults();
            }
        }

        function displayResults(results) {
            if (results.length === 0) {
                showNoResults();
                return;
            }

            resultsContainer.innerHTML = results.map(result => {
                const product = result.product || result;
                const name = product.name || product.title || 'Без названия';
                const description = product.description || 'Описание отсутствует';
                const price = product.price || 0;
                const category = product.category || 'Без категории';
                const image = product.image || product.thumbnail || '';
                const relevance = result.relevancePercentage || 0;
                
                return `
                <div class="product-card">
        
                        <img src="${image}" 
                             alt="${name}" 
                             class="product-image"
                             onerror="this.src='data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMzAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjZjhmOWZhIi8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtZmFtaWx5PSJBcmlhbCwgc2Fucy1zZXJpZiIgZm9udC1zaXplPSIxOCIgZmlsbD0iIzk5OSIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPk5vIEltYWdlPC90ZXh0Pjwvc3ZnPg=='">
     
                    <div class="product-title">${name}</div>
                    <div class="product-description">${description}</div>
                    
                    <div class="product-meta">
                        <div class="product-price">$${price.toFixed(2)}</div>
                        <div class="product-category">${category}</div>
                    </div>
                    
                    <div style="margin-top: 10px; text-align: right;">
                        <span class="relevance-score">${relevance}% совпадение</span>
                    </div>
                </div>
            `;
            }).join('');
        }

        function showLoading() {
            loadingElement.style.display = 'block';
            noResultsElement.style.display = 'none';
            resultsContainer.innerHTML = '';
        }

        function showNoResults() {
            noResultsElement.style.display = 'block';
            loadingElement.style.display = 'none';
            resultsContainer.innerHTML = '';
        }

        function hideElements() {
            loadingElement.style.display = 'none';
            noResultsElement.style.display = 'none';
        }

        async function loadAutocomplete(query) {
            try {
                const response = await fetch(`/api/autocomplete?q=${encodeURIComponent(query)}`);
                const suggestions = await response.json();
                showAutocomplete(suggestions);
            } catch (error) {
                console.error('Autocomplete error:', error);
                hideAutocomplete();
            }
        }

        function showAutocomplete(suggestions) {
            if (suggestions.length === 0) {
                hideAutocomplete();
                return;
            }

            autocompleteSuggestions.innerHTML = suggestions.map(suggestion => 
                `<div class="autocomplete-item" onclick="selectSuggestion('${suggestion}')">${suggestion}</div>`
            ).join('');
            
            autocompleteSuggestions.style.display = 'block';
            selectedSuggestionIndex = -1;
        }

        function hideAutocomplete() {
            autocompleteSuggestions.style.display = 'none';
            selectedSuggestionIndex = -1;
        }

        function updateSelectedSuggestion() {
            const suggestions = autocompleteSuggestions.children;
            
            for (let i = 0; i < suggestions.length; i++) {
                suggestions[i].classList.toggle('active', i === selectedSuggestionIndex);
            }
        }

        function selectSuggestion(suggestion) {
            searchInput.value = suggestion;
            hideAutocomplete();
            performSearch();
        }

        // Hide autocomplete when clicking outside
        document.addEventListener('click', function(e) {
            if (!searchInput.contains(e.target) && !autocompleteSuggestions.contains(e.target)) {
                hideAutocomplete();
            }
        });

        // Load some initial results
        window.addEventListener('load', function() {
            searchInput.value = 'phone';
            performSearch();
        });
    </script>
</body>
</html>
                """;
    }

    public static void main(String[] args) {
        try {
            int port = DEFAULT_PORT;
            
            // Allow port configuration via command line argument
            if (args.length > 0) {
                try {
                    port = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid port number: " + args[0] + ". Using default port " + DEFAULT_PORT);
                }
            }
            
            ProductSearchEngineApp app = new ProductSearchEngineApp(port);
            app.start();
            
            // Keep the server running
            System.out.println("📱 API endpoints:");
            System.out.println("   - GET /                     - Web interface");
            System.out.println("   - GET /api/search?q=query   - Search products");
            System.out.println("   - GET /api/autocomplete?q=  - Autocomplete suggestions");
            System.out.println("\n⏹️  Press Ctrl+C to stop the server");
            
            // Keep the application running
            Thread.currentThread().join();
            
        } catch (Exception e) {
            System.err.println("❌ Error starting search engine: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
} 