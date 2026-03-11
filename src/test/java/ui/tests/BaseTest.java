package ui.tests;

import org.junit.jupiter.api.AfterEach;
import org.openqa.selenium.WebDriver;
import ui.driver.Browser;
import ui.driver.DriverFactory;
import ui.steps.CheckoutSteps;
import ui.steps.LoginSteps;
import ui.steps.ProductsSteps;

public class BaseTest {

    protected WebDriver driver;

    protected LoginSteps loginSteps;
    protected ProductsSteps productsSteps;
    protected CheckoutSteps checkoutSteps;

    protected void initDriver() {
        initDriver(Browser.CHROME);
    }

    protected void initDriver(Browser browser) {
        driver = DriverFactory.createDriver(browser);
        loginSteps = new LoginSteps(driver);
        productsSteps = new ProductsSteps(driver);
        checkoutSteps = new CheckoutSteps(driver);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
