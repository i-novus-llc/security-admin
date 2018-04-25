package net.n2oapp.security.admin.sql.util;



import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;


/**
 * Сервис считывания sql-запросов из файлов
 */
public class SqlUtil {

    public static String getResourceFileAsString(String pathToFile) {
        InputStream is = SqlUtil.class.getClassLoader().getResourceAsStream(pathToFile);
        if (is != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
        return null;
    }
}
