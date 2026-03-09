package ui.tests;

import org.junit.jupiter.api.Test;
import ui.driver.Browser;
import ui.driver.DriverFactory;
import ui.steps.LoginSteps;
import ui.steps.ProductsSteps;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProductsTest extends BaseTest {

    @Test
    public void shouldAddAndRemoveProductFromCart() {
        driver = DriverFactory.createDriver(Browser.CHROME);

        LoginSteps loginSteps = new LoginSteps(driver);
        ProductsSteps productsSteps = new ProductsSteps(driver);

        String productId = "sauce-labs-backpack";

        loginSteps
                .openLoginPage()
                .login("standard_user", "secret_sauce");

        productsSteps
                .addProductToCart(productId);

        assertFalse(productsSteps.isAddToCartButtonVisible(productId));
        assertTrue(productsSteps.isRemoveButtonVisible(productId));
        assertEquals(1, productsSteps.getProductsPageCartCounter());

        productsSteps
                .openCart()
                .removeProductFromCart(productId);

        assertEquals(0, productsSteps.getCartPageCartCounter());
        assertEquals(0, productsSteps.getCartItemsCount());
    }

    @Test
    public void shouldAddThreeRandomProductsAndRemoveOneFromCart() {
        driver = DriverFactory.createDriver(Browser.CHROME);

        LoginSteps loginSteps = new LoginSteps(driver);
        ProductsSteps productsSteps = new ProductsSteps(driver);
        int expectedCounter = 0;

        loginSteps
                .openLoginPage()
                .login("standard_user", "secret_sauce");

        List<String> productIdList = productsSteps.getAllProductIds();

        Collections.shuffle(productIdList);

        List<String> randomProducts = productIdList.subList(0, 3);

        for (String randomProduct : randomProducts) {
            productsSteps.addProductToCart(randomProduct);
            assertEquals(++expectedCounter, productsSteps.getProductsPageCartCounter());
        }

        productsSteps.openCart();

        assertEquals(expectedCounter, productsSteps.getCartItemsCount());

        for (String productId : randomProducts) {
            assertTrue(productsSteps.isProductNameVisibleInCart(productId));
            assertTrue(productsSteps.isProductPriceVisibleInCart(productId));
            assertTrue(productsSteps.isRemoveButtonVisibleInCart(productId));
        }

        String removedProduct = randomProducts.get(0);

        productsSteps.removeProductFromCart(removedProduct);

        assertEquals(expectedCounter - 1, productsSteps.getCartItemsCount());

        productsSteps.continueShopping();

        assertTrue(productsSteps.isAddToCartButtonVisible(removedProduct));


    }
}
