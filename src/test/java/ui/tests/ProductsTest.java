package ui.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import ui.model.Product;
import ui.model.SortType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Feature("Products")
@DisplayName("Тесты товаров")
public class ProductsTest extends BaseTest {

    private static final int EMPTY_CART = 0;
    private static final int ONE_PRODUCT = 1;

    private static final String USERNAME = "standard_user";
    private static final String GLITCH_USERNAME = "performance_glitch_user";
    private static final String PASSWORD = "secret_sauce";

    private static final String PRODUCT_BACKPACK = "sauce-labs-backpack";

    private static final Random RANDOM = new Random();

    @Test
    @Story("Cart management")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Добавление товара в корзину и его удаление")
    @Description("Проверка что пользователь может добавить товар в корзину, "
            + "после чего удалить его. Проверяется корректность отображения "
            + "кнопок Add/Remove и обновление счётчика корзины.")
    void shouldAddAndRemoveProductFromCart() {
        initDriver();

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
    @Story("Cart management")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Добавление трёх случайных товаров и удаление одного")
    @Description("Проверка что пользователь может добавить несколько случайных "
            + "товаров в корзину, после чего удалить один из них. Проверяется "
            + "корректность отображения товаров, цен и обновление количества "
            + "товаров в корзине.")
    void shouldAddThreeRandomProductsAndRemoveOneFromCart() {
        initDriver();

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
    @Story("Checkout")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Оформление заказа с корректными товарами")
    @Description("Проверка полного сценария оформления заказа: добавление товаров "
            + "в корзину, переход к оформлению, заполнение информации покупателя, "
            + "сверка товаров на странице overview и завершение покупки.")
    void shouldCompleteCheckoutWithCorrectProducts() {
        initDriver();

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

        List<Product> expectedProducts = productsSteps.getCartProducts();

        productsSteps.checkout();

        checkoutSteps
                .fillCustomerInfo("Joe", "Doe", "123")
                .continueBtn();

        List<Product> actualOverviewProducts = checkoutSteps.getOverviewProducts();

        assertEquals(expectedProducts, actualOverviewProducts);

        checkoutSteps
                .finishBtn()
                .backToProductsBtn();

        assertEquals(EMPTY_CART, productsSteps.getCartBadgeCount());
    }

    @ParameterizedTest
    @EnumSource(SortType.class)
    @Story("Product sorting")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Сортировка товаров")
    @Description("Проверка корректной работы сортировки товаров по имени и цене "
            + "в обоих направлениях: по возрастанию и по убыванию.")
    void shouldSortProducts(SortType sortType) {
        initDriver();

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
    @Story("Product details")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Сравнение карточки товара со страницей деталей")
    @Description("Проверка что информация о товаре в списке товаров совпадает "
            + "с информацией на странице деталей товара: название, описание и цена.")
    void shouldMatchProductCardWithDetailsPage() {
        initDriver();

        loginSteps
                .openLoginPage()
                .login(USERNAME, PASSWORD);

        Product productFromList = productsSteps.getProduct(ONE_PRODUCT);
        productsSteps.openProduct(ONE_PRODUCT);
        Product productFromDetails = productsSteps.getProductDetails();

        assertAll(
                () -> assertEquals(productFromList.name(), productFromDetails.name()),
                () -> assertEquals(productFromList.description(), productFromDetails.description()),
                () -> assertEquals(productFromList.price(), productFromDetails.price())
        );
    }

    @Test
    @Story("Checkout validation")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Ошибка при оформлении заказа без обязательных полей")
    @Description("Проверка что при попытке продолжить оформление заказа без "
            + "заполнения обязательных полей отображается сообщение об ошибке, "
            + "и пользователь не может перейти к следующему шагу оформления.")
    void shouldShowErrorWhenCheckoutWithEmptyRequiredFields() {
        initDriver();

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
    @Story("Checkout")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Оформление покупки для стандартного и glitch пользователя")
    @Description("Проверка что оформление заказа проходит успешно как для "
            + "обычного пользователя, так и для пользователя performance_glitch_user.")
    void shouldCompletePurchaseForStandardAndGlitchUsers(String name) {
        initDriver();

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
