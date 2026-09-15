# Architecture

The project uses a layered test-automation architecture: tests describe behavior, reusable clients and Page Objects encapsulate system interaction, and configuration/test data remain outside scenario code. This supports independent UI, API, and integration suites while retaining shared conventions and a small maintenance surface.

Production source contains framework code only. Test source contains scenarios and test support. Test resources hold data and schemas. Future CI will execute Maven and publish Allure artifacts without placing environment secrets in the repository.

## UI execution modes

`UiConfiguration` resolves UI settings from JVM properties first, then environment variables, then its defaults. The shared `UiSession` applies the resolved `headless` setting to Chromium, Firefox, and WebKit, so tests do not manage browser lifecycle or execution mode.

Maven Surefire supplies `qaa.ui.headless=true` by default. Therefore local Maven runs and future CI runs are headless:

```powershell
.\mvnw.cmd test
```

For local debugging, run an individual JUnit test with **Run Test** in an IDE or VS Code. A direct JUnit launch does not use Surefire, so `UiConfiguration` defaults to `headless=false` and the browser is visible. This behavior is IDE-independent.

The same setting can be overridden explicitly through a JVM property (or `QAA_UI_HEADLESS` environment variable), for example:

```powershell
.\mvnw.cmd "-Dtest=LoginTest" "-Dqaa.ui.headless=false" test
```

JVM properties take precedence over environment variables. Use `qaa.ui.browser` (or `QAA_UI_BROWSER`) with `CHROMIUM`, `FIREFOX`, or `WEBKIT` to select a supported browser engine.
