package io.github.vladimirshutkov.qaa.support;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import io.github.vladimirshutkov.qaa.config.UiConfiguration;
import io.github.vladimirshutkov.qaa.ui.UiSession;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;

/** JUnit 5 lifecycle adapter that creates an isolated browser context for every test. */
public final class PlaywrightExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {
    private static final Logger TEST_LOGGER = LoggerFactory.getLogger("TEST");
    private static final Logger SETUP_LOGGER = LoggerFactory.getLogger("SETUP");
    private static final ExtensionContext.Namespace NAMESPACE =
            ExtensionContext.Namespace.create(PlaywrightExtension.class);
    private static final String SESSION_KEY = "uiSession";

    @Override
    public void beforeEach(ExtensionContext context) {
        TEST_LOGGER.info("Starting: {}", testName(context));
        SETUP_LOGGER.info("Opening isolated Playwright session");
        try {
            context.getStore(NAMESPACE).put(SESSION_KEY, UiSession.open(UiConfiguration.load()));
        } catch (RuntimeException exception) {
            TEST_LOGGER.error("FAILED: {}", testName(context), exception);
            throw exception;
        }
    }

    @Override
    public void afterEach(ExtensionContext context) {
        UiSession session = context.getStore(NAMESPACE).remove(SESSION_KEY, UiSession.class);
        if (context.getExecutionException().isPresent() && session != null) {
            attachFailureScreenshot(session, context);
        }
        try {
            if (session != null) {
                SETUP_LOGGER.info("Closing isolated Playwright session");
                session.close();
            }
        } catch (RuntimeException exception) {
            TEST_LOGGER.error("FAILED: {}", testName(context), exception);
            throw exception;
        }

        context.getExecutionException().ifPresent(
                exception -> TEST_LOGGER.error("FAILED: {}", testName(context), exception)
        );
    }

    private static void attachFailureScreenshot(UiSession session, ExtensionContext context) {
        try {
            byte[] screenshot = session.page().screenshot();
            Allure.addAttachment("Failure screenshot", "image/png", new ByteArrayInputStream(screenshot), ".png");
        } catch (RuntimeException exception) {
            TEST_LOGGER.warn("Could not capture failure screenshot for {}", testName(context), exception);
        }
    }

    private static String testName(ExtensionContext context) {
        return context.getRequiredTestClass().getSimpleName()
                + "."
                + context.getRequiredTestMethod().getName();
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        Class<?> type = parameterContext.getParameter().getType();
        return type == UiSession.class || type == Page.class || type == BrowserContext.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        UiSession session = extensionContext.getStore(NAMESPACE).get(SESSION_KEY, UiSession.class);
        if (session == null) {
            throw new ParameterResolutionException("UI session is not initialized for this test.");
        }

        Class<?> type = parameterContext.getParameter().getType();
        if (type == UiSession.class) {
            return session;
        }
        if (type == Page.class) {
            return session.page();
        }
        if (type == BrowserContext.class) {
            return session.context();
        }
        throw new ParameterResolutionException("Unsupported UI parameter type: " + type.getName());
    }
}
