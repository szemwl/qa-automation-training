package ui.steps;

import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import ui.model.Product;
import ui.page.CheckoutPage;

import java.util.List;

public class CheckoutSteps {
    private final CheckoutPage checkoutPage;

    public CheckoutSteps(WebDriver driver) {
        this.checkoutPage = new CheckoutPage(driver);
    }

    @Step("Заполнить данные покупателя: имя={0}, фамилия={1}, почтовый индекс={2}")
    public CheckoutSteps fillCustomerInfo(String name, String surname, String zip) {
        checkoutPage.fillCustomerInfo(name, surname, zip);
        return this;
    }

    @Step("Нажать кнопку Continue")
    public CheckoutSteps continueBtn() {
        checkoutPage.continueBtn();
        return this;
    }

    @Step("Нажать кнопку Finish")
    public CheckoutSteps finishBtn() {
        checkoutPage.finishBtn();
        return this;
    }

    @Step("Нажать кнопку Back to Products")
    public CheckoutSteps backToProductsBtn() {
        checkoutPage.backToProductsBtn();
        return this;
    }

    @Step("Проверить отображение контейнера ошибки")
    public boolean isErrorContainerDisplayed() {
        return checkoutPage.isErrorContainerDisplayed();
    }

    @Step("Получить текст сообщения об ошибке")
    public String getErrorContainerText() {
        return checkoutPage.getErrorContainerText();
    }

    @Step("Проверить, что открыта страница Checkout Overview")
    public boolean isCheckoutOverviewPageOpened() {
        return checkoutPage.isCheckoutOverviewPageOpened();
    }

    @Step("Проверить, что открыта страница Checkout Complete")
    public boolean isCheckoutCompletePageOpened() {
        return checkoutPage.isCheckoutCompletePageOpened();
    }

    @Step("Получить текст сообщения о завершении заказа")
    public String getCompleteMessageText() {
        return checkoutPage.getCompleteMessageText();
    }

    @Step("Получить список товаров на странице Overview")
    public List<Product> getOverviewProducts() {
        return checkoutPage.getOverviewProducts();
    }
}
