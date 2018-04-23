package net.n2oapp.security.admin.sql.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


/**
 * Сервис считывания sql-запросов из файлов
 */

public class SqlUtil {

    protected static final Logger log = LoggerFactory.getLogger(SqlUtil.class);

    public static String readFileSql(String pathToFile) {
        StringBuilder sb = null;
        ClassLoader classLoader = SqlUtil.class.getClassLoader();
        File file = new File(classLoader.getResource(pathToFile).getFile());

        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
        } catch (FileNotFoundException e) {
            log.error("Cannot find file "+ pathToFile + " ", e);
        } catch (IOException e) {
            log.error("Resource " + pathToFile + "don't close", e);
        }
        return sb.toString();
    }
}
