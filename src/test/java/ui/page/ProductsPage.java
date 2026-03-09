package ui.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public class ProductsPage extends BasePage {

    private final By cartBadge = By.className("shopping_cart_badge");
    private final By cartLink = By.className("shopping_cart_link");

    public ProductsPage(WebDriver driver) {
        super(driver);
    }

    public void addProductToCart(String productId) {
        click(addToCartButton(productId));
    }

    private By addToCartButton(String productId) {
        return By.id("add-to-cart-" + productId);
    }

    private By removeButton(String productId) {
        return By.id("remove-" + productId);
    }

    public boolean isRemoveButtonVisible(String productId) {
        return !findAll(removeButton(productId)).isEmpty();
    }

    public boolean isAddToCartButtonVisible(String productId) {
        return !findAll(addToCartButton(productId)).isEmpty();
    }

    public int getCartCounter() {
        if (findAll(cartBadge).isEmpty()) {
            return 0;
        }
        return Integer.parseInt(find(cartBadge).getText());
    }

    public void openCart() {
        click(cartLink);
    }

    public List<String> getAllProductIds() {
        List<WebElement> buttons = findAll(By.cssSelector("button[id^='add-to-cart-']"));
        return buttons.stream()
                .map(e -> e.getAttribute("id").replace("add-to-cart-", ""))
                .collect(Collectors.toList());
    }
}
