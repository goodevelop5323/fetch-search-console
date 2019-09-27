package utility;

public class NumberUtil {
    private NumberUtil() {
        super();
    }

    public static int parseInt(String str, int defaultInt) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return defaultInt;
        }
    }
}
