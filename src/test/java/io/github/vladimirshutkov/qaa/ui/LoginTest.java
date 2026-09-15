package io.github.vladimirshutkov.qaa.ui;

import com.microsoft.playwright.Page;
import io.github.vladimirshutkov.qaa.support.PlaywrightExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(PlaywrightExtension.class)
class LoginTest {
    private static final String STANDARD_USER = "standard_user";
    private static final String PASSWORD = "secret_sauce";
    private static final String PRODUCTS_TITLE = "Products";

    @Test
    void shouldOpenProductsPageWhenStandardUserLogsIn(Page page) {
        page.navigate("/");
        LoginPage loginPage = new LoginPage(page);

        loginPage.login(STANDARD_USER, PASSWORD);

        ProductsPage productsPage = new ProductsPage(page);
        productsPage.waitUntilOpened();
        assertTrue(productsPage.isOpened(), "Products page should be opened after a successful login.");
        assertEquals(PRODUCTS_TITLE, productsPage.title(), "Products page should have the expected title.");
    }
}
