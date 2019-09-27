package utility;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtil {

    // INSTANCE化禁止
    private DateUtil() {
        super();
    }

    private static final SimpleDateFormat iso8601DateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static String formatCalendarToISO8601String(Calendar calendar){
        return iso8601DateFormat.format(calendar.getTime());
    }

}
