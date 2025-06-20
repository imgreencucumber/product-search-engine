package service;

import java.util.*;
import java.util.regex.Pattern;


// Анализирует запросы пользователей для определения намерения и стратегии поиска

public class QueryAnalyzer {
    
    // Общие шаблоны для различных типов запросов
    private static final Pattern QUOTED_PHRASE = Pattern.compile("\"([^\"]+)\"");
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
        "и", "в", "на", "с", "для", "от", "до", "по", "за", "из", "к", "о", "об", "про",
        "the", "a", "an", "and", "or", "but", "in", "on", "at", "to", "for", "of", "with"
    ));
    
    public QueryIntent analyzeQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new QueryIntent(false, false, false, QueryType.EMPTY);
        }
        
        String cleanQuery = query.trim().toLowerCase();
        
        // Проверка на цитированные фразы
        boolean isExactPhrase = QUOTED_PHRASE.matcher(query).find();
        
        // Проверка, есть ли в запросе значимые слова
        boolean hasKeywords = hasValidKeywords(cleanQuery);
        
        // Определение, является ли нечеткий поиск полезным
        boolean allowsFuzzySearch = shouldUseFuzzySearch(cleanQuery);
        
        // Определение типа запроса
        QueryType queryType = determineQueryType(cleanQuery, isExactPhrase);
        
        return new QueryIntent(isExactPhrase, hasKeywords, allowsFuzzySearch, queryType);
    }
    
    private boolean hasValidKeywords(String query) {
        String[] words = query.split("\\W+");
        for (String word : words) {
            if (word.length() > 2 && !STOP_WORDS.contains(word)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean shouldUseFuzzySearch(String query) {
        String[] words = query.split("\\W+");
        
        // Использование нечеткого поиска для коротких запросов или одиночных слов
        if (words.length <= 2) {
            return true;
        }
        
        // Использование нечеткого поиска, если запрос содержит потенциальные опечатки (повторяющиеся символы, нетипичные шаблоны)
        for (String word : words) {
            if (word.length() > 3 && hasRepeatedChars(word)) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean hasRepeatedChars(String word) {
        for (int i = 0; i < word.length() - 1; i++) {
            if (word.charAt(i) == word.charAt(i + 1)) {
                return true;
            }
        }
        return false;
    }
    
    private QueryType determineQueryType(String query, boolean isExactPhrase) {
        if (isExactPhrase) {
            return QueryType.EXACT_PHRASE;
        }
        
        String[] words = query.split("\\W+");
        
        if (words.length == 1) {
            return QueryType.SINGLE_KEYWORD;
        } else if (words.length <= 3) {
            return QueryType.MULTI_KEYWORD;
        } else {
            return QueryType.COMPLEX_QUERY;
        }
    }
}


// Представляет намерение пользователя и характеристики запроса поиска

class QueryIntent {
    private final boolean exactPhrase;
    private final boolean hasKeywords;
    private final boolean allowsFuzzySearch;
    private final QueryType queryType;
    
    public QueryIntent(boolean exactPhrase, boolean hasKeywords, boolean allowsFuzzySearch, QueryType queryType) {
        this.exactPhrase = exactPhrase;
        this.hasKeywords = hasKeywords;
        this.allowsFuzzySearch = allowsFuzzySearch;
        this.queryType = queryType;
    }
    
    public boolean isExactPhrase() { return exactPhrase; }
    public boolean hasKeywords() { return hasKeywords; }
    public boolean allowsFuzzySearch() { return allowsFuzzySearch; }
    public QueryType getQueryType() { return queryType; }
    
    @Override
    public String toString() {
        return String.format("QueryIntent{type=%s, exactPhrase=%s, hasKeywords=%s, allowsFuzzy=%s}",
                queryType, exactPhrase, hasKeywords, allowsFuzzySearch);
    }
}

// Enum представляющий различные типы запросов поиска

enum QueryType {
    EMPTY,
    SINGLE_KEYWORD,
    MULTI_KEYWORD,
    EXACT_PHRASE,
    COMPLEX_QUERY
} 