package common;

public class SearchConsoleResult {
    private final SearchKeywordKey keywordKey;
    private final double clicks;
    private final double ctr;
    private final double impressions;
    private final double position;

    public SearchConsoleResult(SearchKeywordKey keywordKey, double clicks, double ctr, double impressions, double position) {
        this.keywordKey = keywordKey;
        this.clicks = clicks;
        this.ctr = ctr;
        this.impressions = impressions;
        this.position = position;
    }

    public SearchKeywordKey getKeywordKey() {
        return keywordKey;
    }

    public double getClicks() {
        return clicks;
    }

    public double getCtr() {
        return ctr;
    }

    public double getImpressions() {
        return impressions;
    }

    public double getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return keywordKey.toString();
    }
}