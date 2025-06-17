
package service;

import algorithm.BoyerMoore;
import algorithm.LevenshteinDistance;
import index.InvertedIndex;
import index.Trie;
import model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SearchCore {
    private InvertedIndex invertedIndex;
    private Trie trie;
    private DataManager dataManager;

    public SearchCore(InvertedIndex invertedIndex, Trie trie, DataManager dataManager) {
        this.invertedIndex = invertedIndex;
        this.trie = trie;
        this.dataManager = dataManager;
    }

    public List<Product> search(String query) {
        Set<Integer> productIds = invertedIndex.search(query);
        List<Product> results = productIds.stream()
                                        .map(dataManager::getProductById)
                                        .collect(Collectors.toList());
        return results;
    }

    public List<Product> searchPhrase(String phrase) {
        List<Product> phraseResults = new ArrayList<>();
        for (Product product : dataManager.getAllProducts().values()) {
            if (BoyerMoore.search(product.getName().toLowerCase(), phrase.toLowerCase()) != -1 ||
                BoyerMoore.search(product.getDescription().toLowerCase(), phrase.toLowerCase()) != -1) {
                phraseResults.add(product);
            }
        }
        return phraseResults;
    }

    public List<Product> fuzzySearch(String query, int maxDistance) {
        List<Product> fuzzyResults = new ArrayList<>();
        for (Product product : dataManager.getAllProducts().values()) {
            // Check if any word in the product name or description is within the distance
            String[] nameWords = product.getName().toLowerCase().split("\\W+");
            String[] descWords = product.getDescription().toLowerCase().split("\\W+");
            
            boolean found = false;
            for (String word : nameWords) {
                if (LevenshteinDistance.calculate(word, query.toLowerCase()) <= maxDistance) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                for (String word : descWords) {
                    if (LevenshteinDistance.calculate(word, query.toLowerCase()) <= maxDistance) {
                        found = true;
                        break;
                    }
                }
            }
            if (found) {
                fuzzyResults.add(product);
            }
        }
        return fuzzyResults;
    }

    public List<String> autocomplete(String prefix) {
        return trie.autocomplete(prefix.toLowerCase());
    }
}

