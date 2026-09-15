package io.github.vladimirshutkov.qaa.ui;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/** Page object for the SauceDemo products page. */
public final class ProductsPage {
    private final Locator pageTitle;

    public ProductsPage(Page page) {
        pageTitle = page.locator("[data-test='title']");
    }

    public boolean isOpened() {
        return pageTitle.isVisible();
    }

    public void waitUntilOpened() {
        pageTitle.waitFor();
    }

    public String title() {
        return pageTitle.textContent();
    }
}
