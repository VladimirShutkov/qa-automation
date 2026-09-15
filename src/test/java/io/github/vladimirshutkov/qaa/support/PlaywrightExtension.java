package io.github.vladimirshutkov.qaa.support;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import io.github.vladimirshutkov.qaa.config.UiConfiguration;
import io.github.vladimirshutkov.qaa.ui.UiSession;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

/** JUnit 5 lifecycle adapter that creates an isolated browser context for every test. */
public final class PlaywrightExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {
    private static final ExtensionContext.Namespace NAMESPACE =
            ExtensionContext.Namespace.create(PlaywrightExtension.class);
    private static final String SESSION_KEY = "uiSession";

    @Override
    public void beforeEach(ExtensionContext context) {
        context.getStore(NAMESPACE).put(SESSION_KEY, UiSession.open(UiConfiguration.load()));
    }

    @Override
    public void afterEach(ExtensionContext context) {
        UiSession session = context.getStore(NAMESPACE).remove(SESSION_KEY, UiSession.class);
        if (session != null) {
            session.close();
        }
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
