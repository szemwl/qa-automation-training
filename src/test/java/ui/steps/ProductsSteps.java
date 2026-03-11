package ui.steps;

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

    public ProductsSteps addProductToCart(String productId) {
        productsPage.addProductToCart(productId);
        return this;
    }

    public ProductsSteps openCart() {
        productsPage.openCart();
        return this;
    }

    public ProductsSteps removeProductFromCart(String productId) {
        cartPage.removeProduct(productId);
        return this;
    }

    public boolean isRemoveButtonVisible(String productId) {
        return productsPage.isRemoveButtonVisible(productId);
    }

    public boolean isAddToCartButtonVisible(String productId) {
        return productsPage.isAddToCartButtonVisible(productId);
    }

    public int getProductsPageCartCounter() {
        return productsPage.getCartCounter();
    }

    public int getCartBadgeCount() {
        return cartPage.getCartCounter();
    }

    public int getCartItemsCount() {
        return cartPage.getCartItemsCount();
    }

    public List<String> getAllProductIds() {
        return productsPage.getAllProductIds();
    }

    public ProductsSteps continueShopping() {
        cartPage.continueShopping();
        return this;
    }

    public boolean isProductNameVisibleInCart(String productId) {
        return cartPage.isProductNameVisible(productId);
    }

    public boolean isProductPriceVisibleInCart(String productId) {
        return cartPage.isProductPriceVisible(productId);
    }

    public boolean isRemoveButtonVisibleInCart(String productId) {
        return cartPage.isRemoveButtonVisible(productId);
    }

    public ProductsSteps checkout() {
        cartPage.checkout();
        return this;
    }

    public ProductsSteps selectSort(String value) {
        productsPage.selectSort(value);
        return this;
    }

    public List<String> getProductNames() {
        return productsPage.getProductNames();
    }

    public List<Double> getProductPrices() {
        return productsPage.getProductPrices();
    }

    public ProductsSteps openProduct(int index) {
        productsPage.openProduct(index);
        return this;
    }

    public Product getProduct(int index) {
        return productsPage.getProduct(index);
    }

    public Product getProductDetails() {
        return productsPage.getProductDetails();
    }

    public List<Product> getCartProducts() {
        return cartPage.getCartProducts();
    }
}
