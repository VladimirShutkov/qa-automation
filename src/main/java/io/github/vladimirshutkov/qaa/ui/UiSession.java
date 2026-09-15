package io.github.vladimirshutkov.qaa.ui;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import io.github.vladimirshutkov.qaa.config.UiConfiguration;

/** Owns the Playwright resources required for one isolated UI test execution. */
public final class UiSession implements AutoCloseable {
    private final Playwright playwright;
    private final Browser browser;
    private final BrowserContext context;
    private final Page page;

    private UiSession(Playwright playwright, Browser browser, BrowserContext context, Page page) {
        this.playwright = playwright;
        this.browser = browser;
        this.context = context;
        this.page = page;
    }

    public static UiSession open(UiConfiguration configuration) {
        Playwright playwright = Playwright.create();
        try {
            Browser browser = launchBrowser(playwright, configuration);
            BrowserContext context = browser.newContext(
                    new Browser.NewContextOptions().setBaseURL(configuration.baseUrl())
            );
            return new UiSession(playwright, browser, context, context.newPage());
        } catch (RuntimeException exception) {
            playwright.close();
            throw exception;
        }
    }

    public Page page() {
        return page;
    }

    public BrowserContext context() {
        return context;
    }

    @Override
    public void close() {
        RuntimeException failure = null;
        try {
            context.close();
        } catch (RuntimeException exception) {
            failure = exception;
        }
        try {
            browser.close();
        } catch (RuntimeException exception) {
            if (failure != null) {
                failure.addSuppressed(exception);
            } else {
                failure = exception;
            }
        }
        try {
            playwright.close();
        } catch (RuntimeException exception) {
            if (failure != null) {
                failure.addSuppressed(exception);
            } else {
                failure = exception;
            }
        }
        if (failure != null) {
            throw failure;
        }
    }

    private static Browser launchBrowser(Playwright playwright, UiConfiguration configuration) {
        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions().setHeadless(configuration.headless());
        return switch (configuration.browserName()) {
            case CHROMIUM -> playwright.chromium().launch(options);
            case FIREFOX -> playwright.firefox().launch(options);
            case WEBKIT -> playwright.webkit().launch(options);
        };
    }
}
