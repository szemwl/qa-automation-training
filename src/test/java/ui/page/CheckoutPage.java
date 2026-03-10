package ui.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CheckoutPage extends BasePage {

    private final By firstName = By.id("first-name");
    private final By lastName = By.id("last-name");
    private final By postalCode = By.id("postal-code");

    private final By continueBtn = By.id("continue");
    private final By finishBtn = By.id("finish");
    private final By backToProductsBtn = By.id("back-to-products");

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    public void fillCustomerInfo(String name, String surname, String zip) {
        type(firstName, name);
        type(lastName, surname);
        type(postalCode, zip);
    }

    public void continueBtn() {
        click(continueBtn);
    }

    public void finishBtn() {
        click(finishBtn);
    }

    public void backToProductsBtn() {
        click(backToProductsBtn);
    }
}
