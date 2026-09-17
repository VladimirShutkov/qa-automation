package io.github.vladimirshutkov.qaa.ui;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Page object for the SauceDemo products page. */
public final class ProductsPage {
    private static final Logger LOGGER = LoggerFactory.getLogger("UI");
    private final Locator pageTitle;
    private final Locator menuButton;
    private final Locator logoutLink;
    private final Locator sauceLabsBackpackAddToCartButton;
    private final Locator cartBadge;

    public ProductsPage(Page page) {
        pageTitle = page.locator("[data-test='title']");
        menuButton = page.locator("#react-burger-menu-btn");
        logoutLink = page.locator("#logout_sidebar_link");
        sauceLabsBackpackAddToCartButton = page.locator("[data-test='add-to-cart-sauce-labs-backpack']");
        cartBadge = page.locator("[data-test='shopping-cart-badge']");
    }

    public boolean isOpened() {
        LOGGER.info("Checking Products page visibility");
        return pageTitle.isVisible();
    }

    public void waitUntilOpened() {
        LOGGER.info("Waiting for Products page");
        pageTitle.waitFor();
    }

    public String title() {
        LOGGER.info("Getting Products page title");
        return pageTitle.textContent();
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

    public String cartBadgeCount() {
        LOGGER.info("Getting cart badge count");
        return cartBadge.textContent();
    }
}
