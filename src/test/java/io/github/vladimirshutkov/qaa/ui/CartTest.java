package io.github.vladimirshutkov.qaa.ui;

import com.microsoft.playwright.Page;
import io.github.vladimirshutkov.qaa.support.PlaywrightExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(PlaywrightExtension.class)
@DisplayName("Shopping Cart UI Tests")
class CartTest {
    private static final Logger ASSERT_LOGGER = LoggerFactory.getLogger("ASSERT");
    private static final String STANDARD_USER = "standard_user";
    private static final String PASSWORD = "secret_sauce";

    @Test
    @DisplayName("User can add Backpack to cart")
    void shouldAddSauceLabsBackpackToCart(Page page) {
        page.navigate("/");
        LoginPage loginPage = new LoginPage(page);
        loginPage.login(STANDARD_USER, PASSWORD);

        ProductsPage productsPage = new ProductsPage(page);
        productsPage.waitUntilOpened();
        ASSERT_LOGGER.info("Verifying Products page is visible");
        assertTrue(productsPage.isOpened(), "Products page should be opened after a successful login.");

        productsPage.addSauceLabsBackpackToCart();

        ASSERT_LOGGER.info("Verifying cart badge count is '1'");
        assertEquals("1", productsPage.cartBadgeCount(), "Cart badge should show one added product.");
    }
}
