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

    private final By errorContainer = By.cssSelector("[data-test='error']");

    private final By pageTitle = By.className("title");
    private final By completeMessage = By.className("complete-header");

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

    public boolean isErrorContainerDisplayed() {
        return !findAll(errorContainer).isEmpty();
    }

    public String getErrorContainerText() {
        return find(errorContainer).getText();
    }

    public boolean isCheckoutOverviewPageOpened() {
        return driver.getCurrentUrl().contains("checkout-step-two.html");
    }

    public boolean isCheckoutCompletePageOpened() {
        return driver.getCurrentUrl().contains("checkout-complete.html")
                && find(pageTitle).getText().equals("Checkout: Complete!")
                && find(completeMessage).isDisplayed();
    }

    public String getCompleteMessageText() {
        return find(completeMessage).getText();
    }
}
