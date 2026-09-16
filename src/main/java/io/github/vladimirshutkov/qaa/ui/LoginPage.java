package io.github.vladimirshutkov.qaa.ui;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Page object for the SauceDemo login page. */
public final class LoginPage {
    private static final Logger LOGGER = LoggerFactory.getLogger("UI");
    private final Locator usernameInput;
    private final Locator passwordInput;
    private final Locator loginButton;
    private final Locator loginError;

    public LoginPage(Page page) {
        usernameInput = page.locator("[data-test='username']");
        passwordInput = page.locator("[data-test='password']");
        loginButton = page.locator("[data-test='login-button']");
        loginError = page.locator("[data-test='error']");
    }

    public void login(String username, String password) {
        LOGGER.info("Starting login flow");
        LOGGER.info("Entering username: {}", username);
        usernameInput.fill(username);
        LOGGER.info("Entering password (value redacted)");
        passwordInput.fill(password);
        LOGGER.info("Clicking Login button");
        loginButton.click();
    }

    public boolean isOpened() {
        LOGGER.info("Checking Login page visibility");
        return usernameInput.isVisible() && loginButton.isVisible();
    }

    public boolean isLoginErrorVisible() {
        return loginError.isVisible();
    }

    public String loginErrorMessage() {
        return loginError.textContent();
    }
}
