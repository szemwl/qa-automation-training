package api.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    public static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream inputStream = ConfigReader.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {

            PROPERTIES.load(inputStream);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }
}
