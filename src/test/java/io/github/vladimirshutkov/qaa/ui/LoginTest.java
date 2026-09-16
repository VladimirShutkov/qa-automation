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
class LoginTest {
    private static final Logger ASSERT_LOGGER = LoggerFactory.getLogger("ASSERT");
    private static final String STANDARD_USER = "standard_user";
    private static final String PASSWORD = "secret_sauce";
    private static final String PRODUCTS_TITLE = "Products";

    @Test
    @DisplayName("User can log in with valid credentials")
    void shouldOpenProductsPageWhenStandardUserLogsIn(Page page) {
        page.navigate("/");
        LoginPage loginPage = new LoginPage(page);

        loginPage.login(STANDARD_USER, PASSWORD);

        ProductsPage productsPage = new ProductsPage(page);
        productsPage.waitUntilOpened();
        ASSERT_LOGGER.info("Verifying Products page is visible");
        assertTrue(productsPage.isOpened(), "Products page should be opened after a successful login.");
        ASSERT_LOGGER.info("Verifying Products page title is '{}'", PRODUCTS_TITLE);
        assertEquals(PRODUCTS_TITLE, productsPage.title(), "Products page should have the expected title.");
    }
}
