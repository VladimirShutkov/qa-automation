package io.github.vladimirshutkov.qaa.ui;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/** Page object for the SauceDemo login page. */
public final class LoginPage {
    private final Locator usernameInput;
    private final Locator passwordInput;
    private final Locator loginButton;

    public LoginPage(Page page) {
        usernameInput = page.locator("[data-test='username']");
        passwordInput = page.locator("[data-test='password']");
        loginButton = page.locator("[data-test='login-button']");
    }

    public void login(String username, String password) {
        usernameInput.fill(username);
        passwordInput.fill(password);
        loginButton.click();
    }
}
