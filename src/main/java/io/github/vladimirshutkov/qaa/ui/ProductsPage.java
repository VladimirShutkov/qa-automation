package io.github.vladimirshutkov.qaa.ui;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/** Page object for the SauceDemo products page. */
public final class ProductsPage {
    private static final Logger LOGGER = LoggerFactory.getLogger("UI");
    private final Locator pageTitle;
    private final Locator menuButton;
    private final Locator logoutLink;
    private final Locator sauceLabsBackpackAddToCartButton;
    private final Locator shoppingCartLink;
    private final Locator cartBadge;
    private final Locator cartItemName;

    public ProductsPage(Page page) {
        pageTitle = page.locator("[data-test='title']");
        menuButton = page.locator("#react-burger-menu-btn");
        logoutLink = page.locator("#logout_sidebar_link");
        sauceLabsBackpackAddToCartButton = page.locator("[data-test='add-to-cart-sauce-labs-backpack']");
        shoppingCartLink = page.locator("[data-test='shopping-cart-link']");
        cartBadge = page.locator("[data-test='shopping-cart-badge']");
        cartItemName = page.locator("[data-test='inventory-item-name']");
    }

    public void assertOpened() {
        LOGGER.info("Verifying Products page visibility");
        assertThat(pageTitle).isVisible();
    }

    public void assertTitle(String expectedTitle) {
        assertThat(pageTitle).hasText(expectedTitle);
    }

    public void openApplicationMenu() {
        LOGGER.info("Opening application menu");
        menuButton.click();
    }

    public void logout() {
        LOGGER.info("Logging out");
        logoutLink.click();
    }

    public void addSauceLabsBackpackToCart() {
        LOGGER.info("Adding Sauce Labs Backpack to cart");
        sauceLabsBackpackAddToCartButton.click();
    }

    public void assertCartBadgeCount(String expectedCount) {
        assertThat(cartBadge).hasText(expectedCount);
    }

    public void openCart() {
        LOGGER.info("Opening shopping cart");
        shoppingCartLink.click();
    }

    public void assertCartContainsItem(String expectedItemName) {
        assertThat(cartItemName).hasText(expectedItemName);
    }
}
