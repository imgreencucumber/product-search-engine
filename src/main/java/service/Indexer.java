
package service;

import index.InvertedIndex;
import index.Trie;
import model.Product;

import java.util.Collection;

public class Indexer {
    private InvertedIndex invertedIndex;
    private Trie trie;

    public Indexer(InvertedIndex invertedIndex, Trie trie) {
        this.invertedIndex = invertedIndex;
        this.trie = trie;
    }

    public void indexProducts(Collection<Product> products) {
        for (Product product : products) {
            // Index for keyword search
            invertedIndex.addDocument(product.getName(), product.getId());
            invertedIndex.addDocument(product.getDescription(), product.getId());
            invertedIndex.addDocument(product.getCategory(), product.getId());

            // Index for autocomplete
            String[] nameWords = product.getName().toLowerCase().split("\\W+");
            for (String word : nameWords) {
                if (!word.isEmpty()) {
                    trie.insert(word);
                }
            }
            String[] descriptionWords = product.getDescription().toLowerCase().split("\\W+");
            for (String word : descriptionWords) {
                if (!word.isEmpty()) {
                    trie.insert(word);
                }
            }
            String[] categoryWords = product.getCategory().toLowerCase().split("\\W+");
            for (String word : categoryWords) {
                if (!word.isEmpty()) {
                    trie.insert(word);
                }
            }
        }
    }
}

