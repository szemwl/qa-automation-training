package ui.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import ui.model.Product;

import java.util.List;
import java.util.stream.Collectors;

public class ProductsPage extends BasePage {

    private final By cartBadge = By.className("shopping_cart_badge");
    private final By cartLink = By.className("shopping_cart_link");
    private final By sortDropdown = By.className("product_sort_container");

    private final By productName = By.className("inventory_item_name");
    private final By productPrice = By.className("inventory_item_price");
    private final By productDescription = By.className("inventory_item_desc");

    private final By productDetailsName = By.className("inventory_details_name");
    private final By productDetailsPrice = By.className("inventory_details_price");
    private final By productDetailsDescription = By.className("inventory_details_desc");

    private final By productCard = By.className("inventory_item");

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
        return Integer.parseInt(waitVisible(cartBadge).getText());
    }

    public void openCart() {
        click(cartLink);
    }

    public List<String> getAllProductIds() {
        List<WebElement> items = findAll(By.className("inventory_item"));
        return items.stream()
                .map(item -> item.findElement(By.cssSelector("button"))
                        .getAttribute("id")
                        .replace("add-to-cart-", "")
                        .replace("remove-", ""))
                .collect(Collectors.toList());
    }

    public void selectSort(String value) {
        new Select(find(sortDropdown)).selectByValue(value);
    }

    public List<String> getProductNames() {
        return findAll(productName).stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public List<Double> getProductPrices() {
        return findAll(productPrice).stream()
                .map(WebElement::getText)
                .map(text -> text.replace("$", ""))
                .map(Double::parseDouble)
                .collect(Collectors.toList());
    }

    public void openProduct(int index) {
        findAll(By.className("inventory_item_name"))
                .get(index)
                .click();
    }

    public Product getProduct(int index) {
        WebElement item = findAll(productCard).get(index);

        String name = item.findElement(productName).getText();
        String desc = item.findElement(productDescription).getText();
        String price = item.findElement(productPrice).getText();

        return new Product(name, desc, price);
    }

    public Product getProductDetails() {
        String name = find(productDetailsName).getText();
        String desc = find(productDetailsDescription).getText();
        String price = find(productDetailsPrice).getText();

        return new Product(name, desc, price);
    }
}
