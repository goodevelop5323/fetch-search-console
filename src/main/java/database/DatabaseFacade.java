package database;

import common.*;

import java.sql.Date;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

public class DatabaseFacade {
    Logger logger = LoggerFactory.getInstance().getLogger(this.getClass());

    private static final DatabaseFacade INSTANCE = new DatabaseFacade();

    private static final String SELECT_MAX_SEARCH_DATE_SQL = "SELECT date from search_date";
    private static final String INSERT_SEARCH_DATE_SQL = "INSERT INTO search_date (date) VALUES (?)";

    private static final String SELECT_SEARCH_KEYWORD_SQL = "SELECT id, search_keyword_ from search_date";

    private static final String SELECT_KEY_ID_SQL = "SELECT id from keyword where keyword = ? and url = ?";
    private static final String INSERT_SEARCH_CONSOLE_DATA_SQL = "INSERT INTO search_console_data VALUES (?, ?, ?, ?, ?, ?)";

    private final Map<SearchKeywordKey, Integer> keywordKeyMap = new HashMap<>();
    private int dateId = 0;
    private Connection connection = null;

    public static DatabaseFacade getInstance() {
        return INSTANCE;
    }

    private DatabaseFacade() {
        super();
        initialize();
    }

    private void initialize() {
        PropertyManager property = PropertyManager.getInstance();
        String host = property.getDBHost();
        String user = property.getDBUser();
        String pass = property.getDBPass();
        try {
            connection = DriverManager.getConnection(host, user, pass);
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            String msg = "コネクションの確率に失敗しました。プロパティのDB接続情報を見直してください。host=" + host + ", user=" + user + ", pass=" + pass;
            ExceptionHandler.handleException(logger, msg, e);
        }
    }

    public void closeConnection() {
        if (null == connection) {
            return;
        }
        try {
            connection.commit();
            connection.close();
        } catch (SQLException e) {
            String msg = "SQLコネクションクローズ時にエラーが発生しました。コミットが行われていない可能性がありますので、DBを確認してください。";
            ExceptionHandler.handleException(logger, msg, e);
        }
    }

    public void insertAndSelectSearchDate(Calendar calendar) {
        try (PreparedStatement insertStatement = connection.prepareStatement(INSERT_SEARCH_DATE_SQL);
             Statement selectStatement = connection.createStatement()) {
            insertStatement.setDate(1, new Date(calendar.getTimeInMillis()));
            insertStatement.execute();
            connection.commit();
            ResultSet resultSet = selectStatement.executeQuery(SELECT_MAX_SEARCH_DATE_SQL);
            while (resultSet.next()) {
                dateId = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            String msg = "DBへの日付の挿入・取得に失敗しました。";
            ExceptionHandler.handleException(logger, msg, e);
        }
    }

    public Set<SearchKeywordKey> fetchKeywordKeySet() {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(SELECT_SEARCH_KEYWORD_SQL);
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String keyword = resultSet.getString(2);
                String url = resultSet.getString(3);
                SearchKeywordKey keywordKey = new SearchKeywordKey(keyword, url);
                keywordKeyMap.put(keywordKey, id);
            }
        } catch (SQLException e) {
            String msg = "DBからキーワード一覧を取得できませんでした。";
            ExceptionHandler.handleException(logger, msg, e);
        }
        return keywordKeyMap.keySet();
    }

    public void insertConsoleData(List<SearchConsoleResult> searchResultList) {
        if (null == searchResultList || searchResultList.isEmpty()) {
            return;
        }
        for (SearchConsoleResult searchResult : searchResultList) {
            int keywordId = keywordKeyMap.get(searchResult.getKeywordKey());
            try (PreparedStatement statement = connection.prepareStatement(INSERT_SEARCH_CONSOLE_DATA_SQL)) {
                statement.setInt(1, keywordId);
                statement.setDouble(2, searchResult.getClicks());
                statement.setDouble(3, searchResult.getCtr());
                statement.setDouble(4, searchResult.getImpressions());
                statement.setDouble(5, searchResult.getPosition());
                statement.setInt(6, dateId);
                statement.execute();
            } catch (SQLException e) {
                String msg = "DBにレコードを挿入できませんでした。keyword_Id=" + keywordId + ", date_id=" + dateId;
                ExceptionHandler.handleException(logger, msg, e);
            }
        }
    }

}
