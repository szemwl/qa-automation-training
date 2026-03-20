package ui.driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class DriverFactory {

    public static WebDriver createDriver(Browser browser) {

        WebDriver driver;

        switch (browser) {

            case FIREFOX:
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
                break;

            case CHROME:
            default:
                WebDriverManager.chromedriver().setup();

                ChromeOptions options = new ChromeOptions();

                Map<String, Object> prefs = new HashMap<>();
                prefs.put("credentials_enable_service", false);
                prefs.put("profile.password_manager_enabled", false);
                prefs.put("profile.password_manager_leak_detection", false);
                options.setExperimentalOption("prefs", prefs);

                driver = new ChromeDriver(options);
                break;
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        driver.manage().window().maximize();

        return driver;
    }
}
