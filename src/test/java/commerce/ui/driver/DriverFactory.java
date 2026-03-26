package commerce.ui.driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.Dimension;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class DriverFactory {

    public static WebDriver createDriver(Browser browser) {

        WebDriver driver;

        switch (browser) {

            case FIREFOX:
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver(createFirefoxOptions(false));
                break;

            case CHROME_HEADLESS:
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver(createChromeOptions(true));
                break;

            case CHROME:
            default:
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver(createChromeOptions(isCi()));
                break;
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        driver.manage().window().setSize(new Dimension(1920, 1080));

        return driver;
    }

    private static ChromeOptions createChromeOptions(boolean headless) {

        ChromeOptions options = new ChromeOptions();

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);

        options.setExperimentalOption("prefs", prefs);

        if (headless) {
            options.addArguments("--headless=new");
        }

        if (isCi()) {
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--window-size=1920,1080");
        }

        return options;
    }

    private static FirefoxOptions createFirefoxOptions(boolean headless) {

        FirefoxOptions options = new FirefoxOptions();

        if (headless || isCi()) {
            options.addArguments("-headless");
        }

        return options;
    }

    private static boolean isCi() {
        return "true".equalsIgnoreCase(System.getenv("CI"));
    }
}
