package ui.steps;

import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import ui.model.Product;
import ui.page.CartPage;
import ui.page.ProductsPage;

import java.util.List;

public class ProductsSteps {

    private final ProductsPage productsPage;
    private final CartPage cartPage;

    public ProductsSteps(WebDriver driver) {
        this.productsPage = new ProductsPage(driver);
        this.cartPage = new CartPage(driver);
    }

    @Step("Добавить в корзину товар: {productId}")
    public ProductsSteps addProductToCart(String productId) {
        productsPage.addProductToCart(productId);
        return this;
    }

    @Step("Открыть корзину товаров")
    public ProductsSteps openCart() {
        productsPage.openCart();
        return this;
    }

    @Step("Удалить товар из корзины: {productId}")
    public ProductsSteps removeProductFromCart(String productId) {
        cartPage.removeProduct(productId);
        return this;
    }

    @Step("Проверить отображение кнопки удаления товара: {productId}")
    public boolean isRemoveButtonVisible(String productId) {
        return productsPage.isRemoveButtonVisible(productId);
    }

    @Step("Проверить отображение кнопки 'Add to cart' для товара: {productId}")
    public boolean isAddToCartButtonVisible(String productId) {
        return productsPage.isAddToCartButtonVisible(productId);
    }

    @Step("Получить количество товаров в корзине")
    public int getProductsPageCartCounter() {
        return productsPage.getCartCounter();
    }

    @Step("Получить количество товаров в иконке корзины")
    public int getCartBadgeCount() {
        return cartPage.getCartCounter();
    }

    @Step("Получить количество товаров в счётчике корзины")
    public int getCartItemsCount() {
        return cartPage.getCartItemsCount();
    }

    @Step("Получить список всех товаров")
    public List<String> getAllProductIds() {
        return productsPage.getAllProductIds();
    }

    @Step("Нажать кнопку продолжить покупки")
    public ProductsSteps continueShopping() {
        cartPage.continueShopping();
        return this;
    }

    @Step("Проверить что название товара: {productId} - отображается")
    public boolean isProductNameVisibleInCart(String productId) {
        return cartPage.isProductNameVisible(productId);
    }

    @Step("Проверить что цена товара: {productId} - отображается")
    public boolean isProductPriceVisibleInCart(String productId) {
        return cartPage.isProductPriceVisible(productId);
    }

    @Step("Проверить что кнопка удаления товара: {productId} - отображается")
    public boolean isRemoveButtonVisibleInCart(String productId) {
        return cartPage.isRemoveButtonVisible(productId);
    }

    @Step("Нажать кнопку оформить покупку")
    public ProductsSteps checkout() {
        cartPage.checkout();
        return this;
    }

    @Step("Нажать кнопку сортировки товара")
    public ProductsSteps selectSort(String value) {
        productsPage.selectSort(value);
        return this;
    }

    @Step("Получить список всех названий товаров")
    public List<String> getProductNames() {
        return productsPage.getProductNames();
    }

    @Step("Получить список всех цен товаров")
    public List<Double> getProductPrices() {
        return productsPage.getProductPrices();
    }

    @Step("Открыть карточку товара: {index}")
    public ProductsSteps openProduct(int index) {
        productsPage.openProduct(index);
        return this;
    }

    @Step("Получить данные карточки товара по индексу: {index}")
    public Product getProduct(int index) {
        return productsPage.getProduct(index);
    }

    @Step("Получить детали открытого товара")
    public Product getProductDetails() {
        return productsPage.getProductDetails();
    }

    @Step("Получить список всех товаров")
    public List<Product> getCartProducts() {
        return cartPage.getCartProducts();
    }
}
