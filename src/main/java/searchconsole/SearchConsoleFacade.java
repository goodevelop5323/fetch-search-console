package searchconsole;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.webmasters.Webmasters;
import com.google.api.services.webmasters.WebmastersScopes;
import com.google.api.services.webmasters.model.*;
import common.*;
import utility.DateUtil;
import utility.StringUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SearchConsoleFacade {
    private static final SearchConsoleFacade INSTANCE = new SearchConsoleFacade();

    private final Logger logger = LoggerFactory.getInstance().getLogger(this.getClass());
    private Webmasters webmasters;

    private SearchConsoleFacade() {
        super();
        buildWebmasters();
    }

    public static SearchConsoleFacade getInstance() {
        return INSTANCE;
    }

    public List<SearchConsoleResult> fetchSearchConsoleResultList(Calendar calendar, Set<SearchKeywordKey> searchKeywordKeySet) {
        List<SearchConsoleResult> searchConsoleResultList = new ArrayList<SearchConsoleResult>();

        if (null == webmasters) {
            logger.log(Level.SEVERE, "Webmasterが初期化できていません。");
            return searchConsoleResultList;
        }

        String siteUrl = PropertyManager.getInstance().getSiteUrl();
        if (StringUtil.isEmptyOrSpace(siteUrl)) {
            logger.log(Level.SEVERE, "サイトURLが入力されていません。設定ファイルを見直してください。");
            return searchConsoleResultList;
        }

        for (SearchKeywordKey searchKeywordKey : searchKeywordKeySet) {
            SearchAnalyticsQueryRequest query = buildSearchAnalyticsQueryRequest(calendar, searchKeywordKey);
            try {
                SearchAnalyticsQueryResponse searchAnalyticsQueryResponse = webmasters.searchanalytics().query(siteUrl, query).execute();
                if (null == searchAnalyticsQueryResponse.getRows()) {
                    logger.log(Level.WARNING, buildKeywordErrorMessage("SearchConsoleでキーワードのデータが見つかりませんでした。", searchKeywordKey));
                    continue;
                }
                for (ApiDataRow row : searchAnalyticsQueryResponse.getRows()) {
                    SearchConsoleResult result = new SearchConsoleResult(searchKeywordKey, row.getClicks(), row.getCtr(), row.getImpressions(), row.getPosition());
                    searchConsoleResultList.add(result);
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, buildKeywordErrorMessage("SearchConsole検索中にエラーがおきました。", searchKeywordKey));
            }
        }
        return searchConsoleResultList;
    }

    private SearchAnalyticsQueryRequest buildSearchAnalyticsQueryRequest(Calendar calendar, SearchKeywordKey searchKeywordKey) {
        SearchAnalyticsQueryRequest query = new SearchAnalyticsQueryRequest();
        String targetDateStr = DateUtil.formatCalendarToISO8601String(calendar);
        query.setStartDate(targetDateStr);
        query.setEndDate(targetDateStr);
        query.setSearchType("web");
        query.setRowLimit(1);
        query.setStartRow(0);
        query.setDimensions(Collections.singletonList("query"));
        ApiDimensionFilterGroup apiDimensionFilterGroups = createApiDimensionFilterGroup(searchKeywordKey);
        query.setDimensionFilterGroups(Collections.singletonList(apiDimensionFilterGroups));
        return query;
    }

    private ApiDimensionFilterGroup createApiDimensionFilterGroup(SearchKeywordKey searchKeywordKey) {
        ArrayList<ApiDimensionFilter> filters = new ArrayList<>();

        ApiDimensionFilter keywordFilter = new ApiDimensionFilter();
        keywordFilter.setDimension("query");
        keywordFilter.setExpression(searchKeywordKey.getKeyword());
        keywordFilter.setOperator("equals");
        filters.add(keywordFilter);

        ApiDimensionFilter urlFilter = new ApiDimensionFilter();
        urlFilter.setDimension("page");
        urlFilter.setExpression(searchKeywordKey.getUrl());
        urlFilter.setOperator("contains");
        filters.add(urlFilter);

        ApiDimensionFilterGroup apiDimensionJavaFilterGroup = new ApiDimensionFilterGroup();
        apiDimensionJavaFilterGroup.setFilters(filters);
        apiDimensionJavaFilterGroup.setGroupType("and");
        return apiDimensionJavaFilterGroup;
    }

    private void buildWebmasters() {
        HttpTransport httpTransport = null;
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException e) {
            ExceptionHandler.handleException(logger, e);
            return;
        } catch (IOException e) {
            ExceptionHandler.handleException(logger, e);
            return;
        }
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        GoogleCredential credential = null;
        String jsonKeyFilePath = PropertyManager.getInstance().getJsonFilePath();
        try {
            credential = GoogleCredential.fromStream(new FileInputStream(jsonKeyFilePath)).createScoped(Collections.singleton(WebmastersScopes.WEBMASTERS));
        } catch (IOException e) {
            String msg = "JSONキーファイルが取得できませんでした。 filepath=" + jsonKeyFilePath;
            ExceptionHandler.handleException(logger, msg, e);
            return;
        }
        // Create a new authorized API client
        this.webmasters = new Webmasters.Builder(httpTransport, jsonFactory, credential).setApplicationName("Search Console Cli").build();
    }

    private String buildKeywordErrorMessage(String message, SearchKeywordKey keywordKey) {
        StringBuilder sb = new StringBuilder();
        sb.append(message).append(" ").append(keywordKey);
        return sb.toString();
    }
}
