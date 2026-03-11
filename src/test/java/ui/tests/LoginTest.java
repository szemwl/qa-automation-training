package ui.tests;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import ui.driver.Browser;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginTest extends BaseTest {

    @ParameterizedTest
    @EnumSource(Browser.class)
    void loginTest(Browser browser) {
        initDriver(browser);

        loginSteps
                .openLoginPage()
                .login("standard_user", "secret_sauce");

        assertTrue(driver.getCurrentUrl().contains("inventory"));
    }
}
