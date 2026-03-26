package commerce.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigLoader {

    private static final String BASE_CONFIG = "application.properties";
    private static final String TEST_CONFIG_PROPERTY = "config.properties";

    private static AppConfig appConfig;

    private ConfigLoader() {
    }

    public static AppConfig load() {
        if (appConfig == null) {
            Properties properties = new Properties();
            loadFromClasspath(BASE_CONFIG, properties);

            String testConfig = System.getProperty(TEST_CONFIG_PROPERTY);
            if (testConfig != null && !testConfig.isBlank()) {
                loadFromClasspath(testConfig, properties);
            }

            appConfig = new AppConfig(
                    StorageType.valueOf(properties.getProperty("storage.type").toUpperCase()),
                    properties.getProperty("db.url"),
                    properties.getProperty("db.username"),
                    properties.getProperty("db.password")
            );
        }

        return appConfig;
    }

    public static void reset() {
        appConfig = null;
    }

    private static void loadFromClasspath(String fileName, Properties target) {
        try (InputStream inputStream = ConfigLoader.class
                .getClassLoader()
                .getResourceAsStream(fileName)) {

            if (inputStream == null) {
                throw new IllegalArgumentException("Не найден файл конфига: " + fileName);
            }

            target.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить конфиг: " + fileName, e);
        }
    }
}
