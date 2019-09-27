import common.PropertyManager;
import common.SearchConsoleResult;
import common.SearchKeywordKey;
import database.DatabaseFacade;
import searchconsole.SearchConsoleFacade;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

public class Main {

    /**
     * main method
     *
     * @param args
     */
    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, PropertyManager.getInstance().getFetchBehindDate());

        DatabaseFacade.getInstance().insertAndSelectSearchDate(calendar);
        Set<SearchKeywordKey> keywordKeySet = DatabaseFacade.getInstance().fetchKeywordKeySet();
        if (null == keywordKeySet || keywordKeySet.isEmpty()) {
            return;
        }

        List<SearchConsoleResult> resultList = SearchConsoleFacade.getInstance().fetchSearchConsoleResultList(calendar, keywordKeySet);

        DatabaseFacade.getInstance().insertConsoleData(resultList);
        DatabaseFacade.getInstance().closeConnection();

    }
}