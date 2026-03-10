package ui.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CartPage extends BasePage {

    private final By cartItems = By.className("cart_item");
    private final By cartBadge = By.className("shopping_cart_badge");
    private final By continueShoppingBtn = By.id("continue-shopping");
    private final By checkoutBtn = By.id("checkout");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    private By removeButton(String productId) {
        return By.id("remove-" + productId);
    }

    public void removeProduct(String productId) {
        By item = cartItem(productId);
        click(removeButton(productId));
        waitForInvisibility(item);
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
        click(continueShoppingBtn);
    }

    private By cartItem(String productId) {
        return By.xpath("//button[@id='remove-"
                + productId
                + "']/ancestor::div[@class='cart_item']");
    }

    public boolean isProductNameVisible(String productId) {
        WebElement item = find(cartItem(productId));
        return !item.findElements(By.className("inventory_item_name")).isEmpty();
    }

    public boolean isProductPriceVisible(String productId) {
        WebElement item = find(cartItem(productId));
        return !item.findElements(By.className("inventory_item_price")).isEmpty();
    }

    public boolean isRemoveButtonVisible(String productId) {
        WebElement item = find(cartItem(productId));
        return !item.findElements(removeButton(productId)).isEmpty();
    }

    public void checkout() {
        click(checkoutBtn);
    }
}
