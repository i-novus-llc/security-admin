package net.n2oapp.security.admin.sql.util;

import java.io.*;

/**
 * Сервис считывания sql-запросов из файлов
 */

public class SqlUtil {

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
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
