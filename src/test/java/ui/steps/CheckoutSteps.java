package ui.steps;

import org.openqa.selenium.WebDriver;
import ui.model.Product;
import ui.page.CheckoutPage;

import java.util.List;

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

    public boolean isErrorContainerDisplayed() {
        return checkoutPage.isErrorContainerDisplayed();
    }

    public String getErrorContainerText() {
        return checkoutPage.getErrorContainerText();
    }

    public boolean isCheckoutOverviewPageOpened() {
        return checkoutPage.isCheckoutOverviewPageOpened();
    }

    public boolean isCheckoutCompletePageOpened() {
        return checkoutPage.isCheckoutCompletePageOpened();
    }

    public String getCompleteMessageText() {
        return checkoutPage.getCompleteMessageText();
    }

    public List<Product> getOverviewProducts() {
        return checkoutPage.getOverviewProducts();
    }
}
