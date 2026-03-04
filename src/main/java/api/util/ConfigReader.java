package api.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    public static String get(String key) {

        Properties prop = new Properties();
        try (InputStream inputStream = ConfigReader.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {

            prop.load(inputStream);
            return prop.getProperty(key);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
