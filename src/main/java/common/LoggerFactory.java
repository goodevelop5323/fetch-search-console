package common;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class LoggerFactory {

    private static final LoggerFactory INSTANCE = new LoggerFactory();
    private static final String VM_OPTIONS_LOGGING_PROPERTY_FILE_KEY = "logging.property.path";

    private LoggerFactory() {
        super();
        load();
    }

    public static LoggerFactory getInstance() {
        return INSTANCE;
    }

    private void load() {
        LogManager manager = LogManager.getLogManager();
        String loggerPropertyFilePath = System.getProperty(VM_OPTIONS_LOGGING_PROPERTY_FILE_KEY);
        try (FileInputStream fileInputStream = new FileInputStream(loggerPropertyFilePath)) {
            manager.readConfiguration(fileInputStream);
        } catch (IOException e) {
            throw new IllegalArgumentException("VMオプション" + VM_OPTIONS_LOGGING_PROPERTY_FILE_KEY + "に指定したプロパティファイルを読み込めません。ファイルパス:" + loggerPropertyFilePath);
        }
    }

    public Logger getLogger(Class clazz) {
        Logger logger = Logger.getLogger(clazz.getName());
        return logger;
    }


}
