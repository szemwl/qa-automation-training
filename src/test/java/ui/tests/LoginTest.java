package ui.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import ui.driver.Browser;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@Feature("Login")
@DisplayName("Тесты авторизации")
public class LoginTest extends BaseTest {

    @ParameterizedTest
    @EnumSource(value = Browser.class, names = {"FIREFOX", "CHROME"})
    @Story("Login")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Успешная авторизация пользователя")
    @Description("Проверка логина с валидными данными")
    void loginTest(Browser browser) {
        initDriver(browser);

        loginSteps
                .openLoginPage()
                .login("standard_user", "secret_sauce");

        assertTrue(driver.getCurrentUrl().contains("inventory"));
    }

    @Test
    @Story("Login")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Неуспешная авторизация в headless режиме")
    @Description("Намеренно падающий тест для проверки скриншотов Allure")
    void shouldFailHeadlessTest() {
        initDriver(Browser.CHROME_HEADLESS);

        loginSteps
                .openLoginPage()
                .login("wrong_username", "wrong_password");

        fail("Намеренный сбой теста для проверки вложения скриншота в Allure");
    }

    @Test
    @Disabled
    @Story("Login")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Преднамеренно пропущенный тест")
    @Description("Намеренно пропущенный тест для проверки Allure")
    void shouldSkippedHeadlessTest() {
        initDriver(Browser.CHROME_HEADLESS);

        loginSteps
                .openLoginPage()
                .login("wrong_username", "wrong_password");

        fail("Намеренный сбой теста для проверки Allure");
    }
}
