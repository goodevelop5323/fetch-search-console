package utility;

public class StringUtil {
    // INSTANCE化禁止
    private StringUtil() {
        super();
    }

    public static boolean isEmptyOrSpace(String target) {
        if (null == target) {
            return true;
        }
        target = target.trim();
        if (target.isEmpty()) {
            return true;
        }
        return false;
    }

    public static boolean isNotEmptyOrSpace(String target) {
        return !isEmptyOrSpace(target);
    }

    public static boolean isEqualsIgnoreNullEmptySpace(String target1, String target2) {
        if (isEmptyOrSpace(target1) || isEmptyOrSpace(target2)) {
            return false;
        }
        return target1.equals(target2);
    }
}
