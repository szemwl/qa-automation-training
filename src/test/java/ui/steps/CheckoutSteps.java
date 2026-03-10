package ui.steps;

import org.openqa.selenium.WebDriver;
import ui.page.CheckoutPage;

public class CheckoutSteps {
    private final CheckoutPage checkoutPage;

    public CheckoutSteps(WebDriver driver) {
        this.checkoutPage = new CheckoutPage(driver);
    }

    public CheckoutSteps fillCustomerInfo(String name, String surname, String zip) {
        checkoutPage.fillCustomerInfo(name, surname, zip);
        return this;
    }

    public CheckoutSteps continueBtn() {
        checkoutPage.continueBtn();
        return this;
    }

    public CheckoutSteps finishBtn() {
        checkoutPage.finishBtn();
        return this;
    }

    public CheckoutSteps backToProductsBtn() {
        checkoutPage.backToProductsBtn();
        return this;
    }
}
