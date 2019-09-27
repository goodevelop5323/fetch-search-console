package utility;

import java.io.File;

public class FileUtil {
    private static final String FILENAME_EXTENSION_DELIMITER = ".";

    private FileUtil() {
        super();
    }

    public static String getExtension(File file) throws IllegalArgumentException {
        if (null == file || file.isDirectory()) {
            throw new IllegalArgumentException("ファイルを指定してください。");
        }
        String filename = file.getName();
        String[] filenameArray = filename.split(FILENAME_EXTENSION_DELIMITER);
        if (filenameArray.length == 0) {
            return "";
        }
        String extension = filenameArray[filenameArray.length - 1];
        if (StringUtil.isEmptyOrSpace(extension)) {
            return "";
        }
        return extension;

    }
}
