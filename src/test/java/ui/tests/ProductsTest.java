package ui.tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import ui.driver.Browser;
import ui.driver.DriverFactory;
import ui.model.Product;
import ui.model.SortType;
import ui.steps.CheckoutSteps;
import ui.steps.LoginSteps;
import ui.steps.ProductsSteps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProductsTest extends BaseTest {

    private static final int EMPTY_CART = 0;
    private static final int ONE_PRODUCT = 1;
    private static final String USERNAME = "standard_user";
    private static final String GLITCH_USERNAME = "performance_glitch_user";
    private static final String PASSWORD = "secret_sauce";
    private static final String PRODUCT_BACKPACK = "sauce-labs-backpack";
    private static final Random RANDOM = new Random();

    @Test
    public void shouldAddAndRemoveProductFromCart() {
        driver = DriverFactory.createDriver(Browser.CHROME);

        LoginSteps loginSteps = new LoginSteps(driver);
        ProductsSteps productsSteps = new ProductsSteps(driver);

        loginSteps
                .openLoginPage()
                .login(USERNAME, PASSWORD);

        productsSteps
                .addProductToCart(PRODUCT_BACKPACK);

        assertFalse(productsSteps.isAddToCartButtonVisible(PRODUCT_BACKPACK));
        assertTrue(productsSteps.isRemoveButtonVisible(PRODUCT_BACKPACK));
        assertEquals(ONE_PRODUCT, productsSteps.getProductsPageCartCounter());

        productsSteps
                .openCart()
                .removeProductFromCart(PRODUCT_BACKPACK);

        assertEquals(EMPTY_CART, productsSteps.getCartBadgeCount());
        assertEquals(EMPTY_CART, productsSteps.getCartItemsCount());
    }

    @Test
    public void shouldAddThreeRandomProductsAndRemoveOneFromCart() {
        driver = DriverFactory.createDriver(Browser.CHROME);

        LoginSteps loginSteps = new LoginSteps(driver);
        ProductsSteps productsSteps = new ProductsSteps(driver);
        int expectedCartItems = 0;

        loginSteps
                .openLoginPage()
                .login(USERNAME, PASSWORD);

        List<String> productIdList = productsSteps.getAllProductIds();
        Collections.shuffle(productIdList, RANDOM);
        List<String> randomProducts = productIdList.subList(0, 3);

        for (String randomProduct : randomProducts) {
            productsSteps.addProductToCart(randomProduct);
            assertEquals(++expectedCartItems, productsSteps.getProductsPageCartCounter());
        }

        productsSteps.openCart();

        assertEquals(expectedCartItems, productsSteps.getCartItemsCount());

        for (String productId : randomProducts) {
            assertTrue(productsSteps.isProductNameVisibleInCart(productId));
            assertTrue(productsSteps.isProductPriceVisibleInCart(productId));
            assertTrue(productsSteps.isRemoveButtonVisibleInCart(productId));
        }

        String removedProduct = randomProducts.remove(
                RANDOM.nextInt(randomProducts.size())
        );

        productsSteps.removeProductFromCart(removedProduct);

        assertEquals(expectedCartItems - 1, productsSteps.getCartItemsCount());

        productsSteps.continueShopping();

        assertTrue(productsSteps.isAddToCartButtonVisible(removedProduct));
    }

    @Test
    public void shouldCompleteCheckoutWithCorrectProducts() {
        driver = DriverFactory.createDriver(Browser.CHROME);

        LoginSteps loginSteps = new LoginSteps(driver);
        ProductsSteps productsSteps = new ProductsSteps(driver);
        CheckoutSteps checkoutSteps = new CheckoutSteps(driver);

        loginSteps
                .openLoginPage()
                .login(USERNAME, PASSWORD);

        List<String> productIdList = productsSteps.getAllProductIds();
        Collections.shuffle(productIdList, RANDOM);
        List<String> randomProducts = productIdList.subList(0, 2);

        for (String randomProduct : randomProducts) {
            productsSteps.addProductToCart(randomProduct);
        }

        productsSteps.openCart();

        for (String randomProduct : randomProducts) {
            assertTrue(productsSteps.isProductNameVisibleInCart(randomProduct));
        }

        productsSteps.checkout();

        checkoutSteps
                .fillCustomerInfo("Joe", "Doe", "123")
                .continueBtn()
                .finishBtn()
                .backToProductsBtn();

        assertEquals(EMPTY_CART, productsSteps.getCartBadgeCount());
    }

    @ParameterizedTest
    @EnumSource(SortType.class)
    public void shouldSortProducts(SortType sortType) {
        driver = DriverFactory.createDriver(Browser.CHROME);

        LoginSteps loginSteps = new LoginSteps(driver);
        ProductsSteps productsSteps = new ProductsSteps(driver);

        loginSteps
                .openLoginPage()
                .login(USERNAME, PASSWORD);

        productsSteps.selectSort(sortType.getValue());

        switch (sortType) {
            case NAME_ASC -> {
                List<String> actual = productsSteps.getProductNames();
                List<String> expected = new ArrayList<>(actual);
                Collections.sort(expected);

                assertEquals(expected, actual);
            }
            case NAME_DESC -> {
                List<String> actual = productsSteps.getProductNames();
                List<String> expected = new ArrayList<>(actual);
                expected.sort(Collections.reverseOrder());

                assertEquals(expected, actual);
            }
            case PRICE_ASC -> {
                List<Double> actual = productsSteps.getProductPrices();
                List<Double> expected = new ArrayList<>(actual);
                Collections.sort(expected);

                assertEquals(expected, actual);
            }
            case PRICE_DESC -> {
                List<Double> actual = productsSteps.getProductPrices();
                List<Double> expected = new ArrayList<>(actual);
                expected.sort(Collections.reverseOrder());

                assertEquals(expected, actual);
            }
        }
    }

    @Test
    public void shouldMatchProductCardWithDetailsPage() {
        driver = DriverFactory.createDriver(Browser.CHROME);

        LoginSteps loginSteps = new LoginSteps(driver);
        ProductsSteps productsSteps = new ProductsSteps(driver);

        loginSteps
                .openLoginPage()
                .login(USERNAME, PASSWORD);

        Product productFromList = productsSteps.getProduct(ONE_PRODUCT);
        productsSteps.openProduct(ONE_PRODUCT);
        Product productFromDetails = productsSteps.getProductDetails();

        assertAll(
                () -> assertEquals(productFromList.getName(), productFromDetails.getName()),
                () -> assertEquals(productFromList.getDescription(), productFromDetails.getDescription()),
                () -> assertEquals(productFromList.getPrice(), productFromDetails.getPrice())
        );
    }

    @Test
    public void shouldShowErrorWhenCheckoutWithEmptyRequiredFields() {
        driver = DriverFactory.createDriver(Browser.CHROME);

        LoginSteps loginSteps = new LoginSteps(driver);
        ProductsSteps productsSteps = new ProductsSteps(driver);
        CheckoutSteps checkoutSteps = new CheckoutSteps(driver);

        loginSteps
                .openLoginPage()
                .login(USERNAME, PASSWORD);

        productsSteps.addProductToCart(PRODUCT_BACKPACK);

        assertEquals(ONE_PRODUCT, productsSteps.getProductsPageCartCounter());

        productsSteps.openCart();

        assertEquals(ONE_PRODUCT, productsSteps.getCartItemsCount());
        assertTrue(productsSteps.isProductNameVisibleInCart(PRODUCT_BACKPACK));

        productsSteps.checkout();

        assertEquals(ONE_PRODUCT, productsSteps.getCartBadgeCount());

        checkoutSteps.continueBtn();

        assertTrue(checkoutSteps.isErrorContainerDisplayed());
        assertEquals("Error: First Name is required", checkoutSteps.getErrorContainerText());
        assertEquals(ONE_PRODUCT, productsSteps.getCartBadgeCount());
        assertFalse(checkoutSteps.isCheckoutOverviewPageOpened());
    }

    @ParameterizedTest
    @ValueSource(strings = {GLITCH_USERNAME, USERNAME})
    void shouldCompletePurchaseForStandardAndGlitchUsers(String name) {
        driver = DriverFactory.createDriver(Browser.CHROME);

        LoginSteps loginSteps = new LoginSteps(driver);
        ProductsSteps productsSteps = new ProductsSteps(driver);
        CheckoutSteps checkoutSteps = new CheckoutSteps(driver);

        loginSteps
                .openLoginPage()
                .login(name, PASSWORD);

        productsSteps.addProductToCart(PRODUCT_BACKPACK);
        productsSteps.openCart();

        assertTrue(productsSteps.isProductNameVisibleInCart(PRODUCT_BACKPACK));

        productsSteps.checkout();

        checkoutSteps
                .fillCustomerInfo("Joe", "Doe", "123")
                .continueBtn()
                .finishBtn();

        assertTrue(checkoutSteps.isCheckoutCompletePageOpened());
        assertEquals("Thank you for your order!", checkoutSteps.getCompleteMessageText());

        checkoutSteps.backToProductsBtn();

        assertEquals(EMPTY_CART, productsSteps.getCartBadgeCount());
    }
}
