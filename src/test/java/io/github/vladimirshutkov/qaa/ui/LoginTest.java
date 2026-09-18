package io.github.vladimirshutkov.qaa.ui;

import com.microsoft.playwright.Page;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.github.vladimirshutkov.qaa.support.PlaywrightExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(PlaywrightExtension.class)
@Epic("UI Automation")
@Feature("Authentication")
@DisplayName("Authentication UI Tests")
class LoginTest {
    private static final Logger ASSERT_LOGGER = LoggerFactory.getLogger("ASSERT");
    private static final String STANDARD_USER = "standard_user";
    private static final String PASSWORD = "secret_sauce";
    private static final String PRODUCTS_TITLE = "Products";
    private static final String INVALID_USERNAME = "invalid_user";
    private static final String INVALID_PASSWORD = "invalid_password";
    private static final String INVALID_CREDENTIALS_MESSAGE =
            "Epic sadface: Username and password do not match any user in this service";
    private static final String LOCKED_OUT_USER = "locked_out_user";
    private static final String LOCKED_OUT_USER_MESSAGE = "Epic sadface: Sorry, this user has been locked out.";

    @Test
    @DisplayName("User can log in with valid credentials")
    void shouldOpenProductsPageWhenStandardUserLogsIn(Page page) {
        page.navigate("/");
        LoginPage loginPage = new LoginPage(page);

        loginPage.login(STANDARD_USER, PASSWORD);

        ProductsPage productsPage = new ProductsPage(page);
        ASSERT_LOGGER.info("Verifying Products page is visible");
        productsPage.assertOpened();
        ASSERT_LOGGER.info("Verifying Products page title is '{}'", PRODUCTS_TITLE);
        productsPage.assertTitle(PRODUCTS_TITLE);
    }

    @Test
    @DisplayName("User can log out")
    void shouldReturnToLoginPageWhenUserLogsOut(Page page) {
        page.navigate("/");
        LoginPage loginPage = new LoginPage(page);
        loginPage.login(STANDARD_USER, PASSWORD);

        ProductsPage productsPage = new ProductsPage(page);
        ASSERT_LOGGER.info("Verifying Products page is visible");
        productsPage.assertOpened();

        productsPage.openApplicationMenu();
        productsPage.logout();

        ASSERT_LOGGER.info("Verifying Login page is visible after logout");
        loginPage.assertOpened();
    }

    @Test
    @DisplayName("Invalid credentials show login error")
    void shouldShowLoginErrorForInvalidCredentials(Page page) {
        page.navigate("/");
        LoginPage loginPage = new LoginPage(page);

        loginPage.login(INVALID_USERNAME, INVALID_PASSWORD);

        ASSERT_LOGGER.info("Verifying invalid credentials error message");
        loginPage.assertLoginErrorMessage(INVALID_CREDENTIALS_MESSAGE);
    }

    @Test
    @DisplayName("Locked out user sees login error")
    void shouldShowLockedOutUserError(Page page) {
        page.navigate("/");
        LoginPage loginPage = new LoginPage(page);

        loginPage.login(LOCKED_OUT_USER, PASSWORD);

        ASSERT_LOGGER.info("Verifying locked out user error message");
        loginPage.assertLoginErrorMessage(LOCKED_OUT_USER_MESSAGE);
    }
}
