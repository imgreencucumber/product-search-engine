package service;


// Предоставляет статистику и аналитику поиска для отладки

public class SearchAnalytics {
    private final QueryIntent queryIntent;
    private final int keywordMatches;
    private final int phraseMatches;
    private final int fuzzyMatches;
    private final int suggestions;
    
    public SearchAnalytics(QueryIntent queryIntent, int keywordMatches, int phraseMatches, 
                          int fuzzyMatches, int suggestions) {
        this.queryIntent = queryIntent;
        this.keywordMatches = keywordMatches;
        this.phraseMatches = phraseMatches;
        this.fuzzyMatches = fuzzyMatches;
        this.suggestions = suggestions;
    }
    
    public QueryIntent getQueryIntent() {
        return queryIntent;
    }
    
    public int getKeywordMatches() {
        return keywordMatches;
    }
    
    public int getPhraseMatches() {
        return phraseMatches;
    }
    
    public int getFuzzyMatches() {
        return fuzzyMatches;
    }
    
    public int getSuggestions() {
        return suggestions;
    }
    
    public int getTotalMatches() {
        return keywordMatches + phraseMatches + fuzzyMatches;
    }
    
    public String getSearchStrategy() {
        if (queryIntent.isExactPhrase()) {
            return "Phrase-focused search";
        } else if (queryIntent.getQueryType() == QueryType.SINGLE_KEYWORD) {
            return "Single keyword + fuzzy search";
        } else if (queryIntent.hasKeywords()) {
            return "Multi-algorithm hybrid search";
        } else {
            return "Basic search";
        }
    }
    
    public String getPerformanceReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== Search Analytics Report ===\n");
        report.append("Query Intent: ").append(queryIntent.toString()).append("\n");
        report.append("Search Strategy: ").append(getSearchStrategy()).append("\n");
        report.append("Results:\n");
        report.append("  - Keyword matches: ").append(keywordMatches).append("\n");
        report.append("  - Phrase matches: ").append(phraseMatches).append("\n");
        report.append("  - Fuzzy matches: ").append(fuzzyMatches).append("\n");
        report.append("  - Total matches: ").append(getTotalMatches()).append("\n");
        report.append("  - Autocomplete suggestions: ").append(suggestions).append("\n");
        return report.toString();
    }
    
    @Override
    public String toString() {
        return String.format("SearchAnalytics{strategy='%s', total=%d, keyword=%d, phrase=%d, fuzzy=%d}",
                getSearchStrategy(), getTotalMatches(), keywordMatches, phraseMatches, fuzzyMatches);
    }
} 