package common;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ExceptionHandler {
    private ExceptionHandler() {
        super();
    }

    public static void handleException(Logger logger, String msg, Exception e) {
        logger.log(Level.SEVERE, msg, e);
    }

    public static void handleException(Logger logger, Exception e) {
        logger.log(Level.SEVERE, null, e);
    }
}
