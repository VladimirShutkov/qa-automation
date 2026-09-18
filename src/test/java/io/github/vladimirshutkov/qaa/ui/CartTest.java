package io.github.vladimirshutkov.qaa.ui;

import com.microsoft.playwright.Page;
import io.github.vladimirshutkov.qaa.support.PlaywrightExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(PlaywrightExtension.class)
@DisplayName("Shopping Cart UI Tests")
class CartTest {
    private static final Logger ASSERT_LOGGER = LoggerFactory.getLogger("ASSERT");
    private static final String STANDARD_USER = "standard_user";
    private static final String PASSWORD = "secret_sauce";
    private static final String SAUCE_LABS_BACKPACK = "Sauce Labs Backpack";

    @Test
    @DisplayName("User can add Backpack to cart")
    void shouldAddSauceLabsBackpackToCart(Page page) {
        page.navigate("/");
        LoginPage loginPage = new LoginPage(page);
        loginPage.login(STANDARD_USER, PASSWORD);

        ProductsPage productsPage = new ProductsPage(page);
        ASSERT_LOGGER.info("Verifying Products page is visible");
        productsPage.assertOpened();

        productsPage.addSauceLabsBackpackToCart();

        ASSERT_LOGGER.info("Verifying cart badge count is '1'");
        productsPage.assertCartBadgeCount("1");
        productsPage.openCart();
        ASSERT_LOGGER.info("Verifying cart contains '{}'", SAUCE_LABS_BACKPACK);
        productsPage.assertCartContainsItem(SAUCE_LABS_BACKPACK);
    }
}
