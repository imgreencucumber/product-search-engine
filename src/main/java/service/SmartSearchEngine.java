package service;

import algorithm.BoyerMoore;
import algorithm.LevenshteinDistance;
import index.InvertedIndex;
import index.Trie;
import model.Product;
import model.SearchResult;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Smart Search Engine that combines multiple search algorithms
 * into a unified intelligent system with ranking and relevance scoring
 */
public class SmartSearchEngine {
    private InvertedIndex invertedIndex;
    private Trie trie;
    private DataManager dataManager;
    private SearchCore searchCore;
    private QueryAnalyzer queryAnalyzer;
    
    // Configuration parameters
    private static final int MAX_FUZZY_DISTANCE = 2;
    private static final double EXACT_MATCH_BOOST = 2.0;
    private static final double PHRASE_MATCH_BOOST = 1.5;
    private static final double FUZZY_MATCH_PENALTY = 0.5;
    private static final int MAX_RESULTS = 20;

    public SmartSearchEngine(InvertedIndex invertedIndex, Trie trie, DataManager dataManager) {
        this.invertedIndex = invertedIndex;
        this.trie = trie;
        this.dataManager = dataManager;
        this.searchCore = new SearchCore(invertedIndex, trie, dataManager);
        this.queryAnalyzer = new QueryAnalyzer();
    }

    /**
     * Main search method that intelligently combines multiple algorithms
     */
    public List<SearchResult> smartSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }

        QueryIntent intent = queryAnalyzer.analyzeQuery(query);
        Map<Product, Double> relevanceScores = new HashMap<>();
        
        // Apply different search strategies based on query analysis
        if (intent.isExactPhrase()) {
            addPhraseSearchResults(query, relevanceScores, PHRASE_MATCH_BOOST);
        }
        
        if (intent.hasKeywords()) {
            addKeywordSearchResults(query, relevanceScores, 1.0);
        }
        
        if (intent.allowsFuzzySearch()) {
            addFuzzySearchResults(query, relevanceScores, FUZZY_MATCH_PENALTY);
        }
        
        // Add exact matches with highest boost
        addExactMatchResults(query, relevanceScores, EXACT_MATCH_BOOST);
        
        // Convert to SearchResult objects and sort by relevance
        return relevanceScores.entrySet().stream()
                .map(entry -> new SearchResult(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> Double.compare(b.getRelevanceScore(), a.getRelevanceScore()))
                .limit(MAX_RESULTS)
                .collect(Collectors.toList());
    }

    /**
     * Provides intelligent search suggestions with autocomplete
     */
    public List<String> getSearchSuggestions(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<String> suggestions = trie.autocomplete(prefix.toLowerCase());
        
        // Enhance suggestions with smart recommendations
        if (suggestions.size() < 5) {
            suggestions.addAll(getFuzzyAutocompleteSuggestions(prefix));
        }
        
        return suggestions.stream()
                .distinct()
                .limit(10)
                .collect(Collectors.toList());
    }

    private void addExactMatchResults(String query, Map<Product, Double> scores, double boost) {
        String queryLower = query.toLowerCase();
        for (Product product : dataManager.getAllProducts().values()) {
            double score = 0.0;
            
            if (product.getName().toLowerCase().contains(queryLower)) {
                score += boost * 2; // Name matches are more important
            }
            if (product.getDescription().toLowerCase().contains(queryLower)) {
                score += boost;
            }
            if (product.getCategory().toLowerCase().contains(queryLower)) {
                score += boost * 0.5;
            }
            
            if (score > 0) {
                scores.merge(product, score, Double::sum);
            }
        }
    }

    private void addKeywordSearchResults(String query, Map<Product, Double> scores, double boost) {
        List<Product> keywordResults = searchCore.search(query);
        for (Product product : keywordResults) {
            double score = calculateKeywordRelevance(query, product) * boost;
            scores.merge(product, score, Double::sum);
        }
    }

    private void addPhraseSearchResults(String query, Map<Product, Double> scores, double boost) {
        List<Product> phraseResults = searchCore.searchPhrase(query);
        for (Product product : phraseResults) {
            double score = boost;
            scores.merge(product, score, Double::sum);
        }
    }

    private void addFuzzySearchResults(String query, Map<Product, Double> scores, double penalty) {
        List<Product> fuzzyResults = searchCore.fuzzySearch(query, MAX_FUZZY_DISTANCE);
        for (Product product : fuzzyResults) {
            double score = calculateFuzzyRelevance(query, product) * penalty;
            scores.merge(product, score, Double::sum);
        }
    }

    private double calculateKeywordRelevance(String query, Product product) {
        String[] queryWords = query.toLowerCase().split("\\W+");
        double relevance = 0.0;
        int matchedWords = 0;
        
        for (String word : queryWords) {
            if (product.getName().toLowerCase().contains(word)) {
                relevance += 2.0; // Name matches are more valuable
                matchedWords++;
            }
            if (product.getDescription().toLowerCase().contains(word)) {
                relevance += 1.0;
                matchedWords++;
            }
            if (product.getCategory().toLowerCase().contains(word)) {
                relevance += 0.5;
                matchedWords++;
            }
        }
        
        // Boost if multiple words match
        if (matchedWords > 1) {
            relevance *= (1.0 + 0.2 * matchedWords);
        }
        
        return relevance;
    }

    private double calculateFuzzyRelevance(String query, Product product) {
        String[] queryWords = query.toLowerCase().split("\\W+");
        double totalRelevance = 0.0;
        
        for (String queryWord : queryWords) {
            double bestMatch = 0.0;
            
            // Check against product name words
            String[] nameWords = product.getName().toLowerCase().split("\\W+");
            for (String nameWord : nameWords) {
                int distance = LevenshteinDistance.calculate(queryWord, nameWord);
                if (distance <= MAX_FUZZY_DISTANCE) {
                    double similarity = 1.0 - (double) distance / Math.max(queryWord.length(), nameWord.length());
                    bestMatch = Math.max(bestMatch, similarity * 2.0); // Name matches are more valuable
                }
            }
            
            // Check against description words
            String[] descWords = product.getDescription().toLowerCase().split("\\W+");
            for (String descWord : descWords) {
                int distance = LevenshteinDistance.calculate(queryWord, descWord);
                if (distance <= MAX_FUZZY_DISTANCE) {
                    double similarity = 1.0 - (double) distance / Math.max(queryWord.length(), descWord.length());
                    bestMatch = Math.max(bestMatch, similarity);
                }
            }
            
            totalRelevance += bestMatch;
        }
        
        return totalRelevance;
    }

    private List<String> getFuzzyAutocompleteSuggestions(String prefix) {
        Set<String> suggestions = new HashSet<>();
        
        // Get all words from product data
        for (Product product : dataManager.getAllProducts().values()) {
            String[] words = (product.getName() + " " + product.getDescription() + " " + product.getCategory())
                    .toLowerCase().split("\\W+");
            
            for (String word : words) {
                if (word.length() >= prefix.length() && 
                    LevenshteinDistance.calculate(prefix.toLowerCase(), word.substring(0, Math.min(prefix.length(), word.length()))) <= 1) {
                    suggestions.add(word);
                }
            }
        }
        
        return new ArrayList<>(suggestions);
    }

    /**
     * Get search statistics and analytics
     */
    public SearchAnalytics getSearchAnalytics(String query) {
        QueryIntent intent = queryAnalyzer.analyzeQuery(query);
        
        int keywordResults = searchCore.search(query).size();
        int phraseResults = searchCore.searchPhrase(query).size();
        int fuzzyResults = searchCore.fuzzySearch(query, MAX_FUZZY_DISTANCE).size();
        int suggestions = getSearchSuggestions(query).size();
        
        return new SearchAnalytics(intent, keywordResults, phraseResults, fuzzyResults, suggestions);
    }
} 