package ui.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CartPage extends BasePage {

    private final By cartItems = By.className("cart_item");
    private final By cartBadge = By.className("shopping_cart_badge");
    private final By continueShopping = By.id("continue-shopping");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    private By removeButton(String productId) {
        return By.id("remove-" + productId);
    }

    public void removeProduct(String productId) {
        By button = removeButton(productId);
        click(removeButton(productId));
        waitForInvisibility(button);
    }

    public int getCartItemsCount() {
        return findAll(cartItems).size();
    }

    public int getCartCounter() {
        if (findAll(cartBadge).isEmpty()) {
            return 0;
        }
        return Integer.parseInt(find(cartBadge).getText());
    }

    public void continueShopping() {
        click(continueShopping);
    }

    private By itemName(String productId) {
        return By.xpath("//button[@id='remove-"
                + productId
                + "']/ancestor::div[@class='cart_item']//div[@class='inventory_item_name']");
    }

    private By itemPrice(String productId) {
        return By.xpath("//button[@id='remove-"
                + productId
                + "']/ancestor::div[@class='cart_item']//div[@class='inventory_item_price']");
    }

    public boolean isProductNameVisible(String productId) {
        return !findAll(itemName(productId)).isEmpty();
    }

    public boolean isProductPriceVisible(String productId) {
        return !findAll(itemPrice(productId)).isEmpty();
    }

    public boolean isRemoveButtonVisible(String productId) {
        return !findAll(removeButton(productId)).isEmpty();
    }
}
