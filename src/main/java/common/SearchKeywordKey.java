package common;

import java.util.Objects;

public class SearchKeywordKey {
    private final String keyword;
    private final String url;

    public SearchKeywordKey(String keyword, String url){
        this.keyword = keyword;
        this.url = url;
    }

    public String getKeyword(){
        return keyword;
    }

    public String getUrl(){
        return url;
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof SearchKeywordKey){
            SearchKeywordKey key = (SearchKeywordKey) obj;
            return this.keyword.equals(key.keyword) && this.url.equals(key.url);
        }
        return false;
    }

    @Override
    public int hashCode(){
        return Objects.hash(keyword, url);
    }

    @Override
    public String toString(){
        return "Keyword:" + keyword + ", URL:" + url;
    }
}
