package io.github.vladimirshutkov.qaa.ui;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Page object for the SauceDemo products page. */
public final class ProductsPage {
    private static final Logger LOGGER = LoggerFactory.getLogger("UI");
    private final Locator pageTitle;

    public ProductsPage(Page page) {
        pageTitle = page.locator("[data-test='title']");
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
}
