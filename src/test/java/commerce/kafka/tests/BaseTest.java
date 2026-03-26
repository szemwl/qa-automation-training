package commerce.kafka.tests;

import commerce.config.AppConfig;
import commerce.config.ConfigLoader;
import commerce.store.OrderStore;
import commerce.store.ProcessedEventStore;
import commerce.store.StoreFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class BaseTest {

    protected OrderStore orderStore;
    protected ProcessedEventStore processedEventStore;

    @BeforeEach
    void setUpStores() {
        String testConfigFile = loadTestConfigFileName();

        System.setProperty("config.properties", testConfigFile);
        ConfigLoader.reset();

        AppConfig config = ConfigLoader.load();
        StoreFactory storeFactory = new StoreFactory(config);

        orderStore = storeFactory.createOrderStore();
        processedEventStore = storeFactory.createProcessedEventStore();
    }

    @AfterEach
    void tearDownConfig() {
        System.clearProperty("config.properties");
        ConfigLoader.reset();
    }

    private String loadTestConfigFileName() {
        Properties properties = new Properties();

        try (InputStream inputStream = getClass()
                .getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (inputStream == null) {
                throw new IllegalArgumentException("Не найден test config: config.properties");
            }

            properties.load(inputStream);
            return properties.getProperty("test.config.file");
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить config.properties", e);
        }
    }
}
