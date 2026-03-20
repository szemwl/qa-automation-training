package ui.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import ui.utils.ConfigReader;

public class LoginPage extends BasePage {

    private static final String SAUCE_URL = ConfigReader.get("sauce.url");

    private final By username = By.id("user-name");
    private final By password = By.id("password");
    private final By loginButton = By.id("login-button");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        driver.get(SAUCE_URL);
    }

    public void enterUsername(String user) {
        type(username, user);
    }

    public void enterPassword(String pass) {
        type(password, pass);
    }

    public void clickLogin() {
        click(loginButton);
    }
}
