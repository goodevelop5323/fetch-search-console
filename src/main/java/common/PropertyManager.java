package common;

import utility.NumberUtil;
import utility.StringUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyManager {
    private static final PropertyManager INSTANCE = new PropertyManager();
    private static final int DEFAULT_BEHIND_DATE = -3;
    private static final String VM_OPTIONS_PROPERTY_FILE_KEY = "program.property.file";
    private static final IllegalArgumentException programPropertyDirNotFoundException = new IllegalArgumentException("VMオプション'program.property.dir'に、プロパティファイル格納ディレクトリのパスを入力してください。");

    private static final Properties properties = new Properties();


    private PropertyManager() {
        super();
        load();
    }

    private void load() {
        String programPropertyFilePath = System.getProperty(VM_OPTIONS_PROPERTY_FILE_KEY);
        if (StringUtil.isEmptyOrSpace(programPropertyFilePath)) {
            throw programPropertyDirNotFoundException;
        }
        File programPropertyFile = new File(programPropertyFilePath);
        if (!programPropertyFile.isFile()) {
            throw programPropertyDirNotFoundException;
        }
        try {
            properties.load(new FileInputStream(programPropertyFile));
        } catch (IOException e) {
            throw new IllegalArgumentException("VMオプション" + VM_OPTIONS_PROPERTY_FILE_KEY + "に指定したプロパティファイルを読み込めません。ファイルパス:" + programPropertyFilePath);
        }
    }

    public static PropertyManager getInstance() {
        return INSTANCE;
    }

    /**
     * プロパティ値を取得する
     *
     * @param key キー
     * @return キーが存在しない場合、デフォルト値
     * 存在する場合、値
     */
    public String getPropertyDefaultEmpty(String key) {
        String value = properties.getProperty(key, "");
        if (StringUtil.isEmptyOrSpace(value)) {
            throw new IllegalArgumentException("対象のプロパティは存在しません。key:" + key);
        }
        return properties.getProperty(key, "");
    }

    public String getSiteUrl() {
        return getPropertyDefaultEmpty("site-url");
    }

    public String getJsonFilePath() {
        return getPropertyDefaultEmpty("json-file-path");
    }

    public String getDBHost() {
        return getPropertyDefaultEmpty("db-host");
    }

    public String getDBUser() {
        return getPropertyDefaultEmpty("db-user");
    }

    public String getDBPass() {
        return getPropertyDefaultEmpty("db-pass");
    }

    public int getFetchBehindDate() {
        return NumberUtil.parseInt(getPropertyDefaultEmpty("fetch-behind-date"), DEFAULT_BEHIND_DATE);
    }
}
