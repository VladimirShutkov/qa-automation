# Architecture

## Overview

The project separates test intent from system interaction and execution concerns:

```text
JUnit tests
  ├── UI Page Objects ──> Playwright ──> SauceDemo
  └── BookingClient ───> REST Assured ─> Restful Booker
        ↑
 configuration, models, test data, schemas, and Allure diagnostics
```

Production sources contain the reusable automation components. Test sources contain scenarios, test data factories, and the JUnit lifecycle adapter. This keeps HTTP/browser mechanics outside test methods while preserving direct, readable scenarios.

## UI Architecture

`PlaywrightExtension` is a JUnit 5 extension used by the UI tests. In `BeforeEach`, it loads `UiConfiguration` and stores a newly opened `UiSession` in the extension context. `UiSession` owns one Playwright instance, browser, browser context, and page; the context has the configured SauceDemo base URL. It can launch Chromium, Firefox, or WebKit.

The lifecycle is deliberately per test:

```text
BeforeEach → UiSession.open() → test → screenshot on failure → session.close()
```

`AfterEach` captures a page screenshot only when the test has an execution exception, attaches it to Allure, and then closes the context, browser, and Playwright resources. If screenshot capture itself fails, the extension logs a warning rather than replacing the original test failure.

`LoginPage` and `ProductsPage` encapsulate selectors and interaction flows. Tests receive a `Page` from the extension, navigate using the context base URL, and verify outcome through Playwright locator assertions. These assertions are web-first and use Playwright's auto-waiting behavior; the project does not use `Thread.sleep`.

## API Architecture

`ApiConfiguration` supplies endpoint and authentication values to `BookingClient`. The client centralizes the REST Assured request specification: JSON content type, base URI, finite HTTP connection timeout (10 seconds), finite socket timeout (30 seconds), and `AllureRestAssuredFilter`.

`BookingClient` owns the endpoint-level mechanics for booking retrieval, creation, authentication, update, and deletion. Tests retain the business assertions, including authorization behaviour and persistence checks after accepted or rejected updates. `Booking` and `BookingDates` are request/response DTOs; `BookingTestData` provides deterministic payloads for test scenarios.

Tests record booking IDs they create and, in `AfterEach`, authenticate and delete them. Cleanup accepts either a successful deletion or an already-absent resource. Unexpected cleanup responses fail the cleanup phase instead of being silently ignored.

JSON Schema resources in `src/test/resources/schemas` validate representative booking responses structurally. The schema tests additionally parse check-in and check-out fields using Java `LocalDate`, providing semantic validation of ISO dates beyond the JSON string shape.

## Configuration Model

Both configuration records resolve each setting in the same order:

```text
non-blank JVM system property → non-blank environment variable → built-in default
```

`UiConfiguration` uses `qaa.ui.base-url` / `QAA_UI_BASE_URL`, `qaa.ui.browser` / `QAA_UI_BROWSER`, and `qaa.ui.headless` / `QAA_UI_HEADLESS`. Defaults are SauceDemo, `CHROMIUM`, and `true` respectively. Thus a JVM `qaa.ui.headless` value always overrides `QAA_UI_HEADLESS`; when neither is supplied, UI runs headlessly.

`ApiConfiguration` uses `qaa.api.base-url` / `QAA_API_BASE_URL`, `qaa.api.username` / `QAA_API_USERNAME`, and `qaa.api.password` / `QAA_API_PASSWORD`. Its defaults are the Restful Booker URL and the public demo credentials `admin` / `password123` currently embedded in the configuration. Real environment credentials should be supplied externally, never committed.

Both base URLs are validated as absolute HTTP(S) URLs. API configuration's `toString()` masks its password.

## Reporting

UI diagnostics follow this flow:

```text
test failure → page screenshot → Allure attachment → UI session cleanup
```

For every API request, `AllureRestAssuredFilter` attaches a text request summary and, after execution, a response summary. It records the HTTP method, URL path, status, and bodies. The current filter masks values of configured sensitive JSON fields such as `username`, `password`, `token`, and common variants. It does not attach request headers or query parameters. This is diagnostic redaction for the current attachment format, not a universal security guarantee.

Allure JUnit results are configured to be written to `target/allure-results`.

## CI and Report Pipeline

The `CI` workflow is the test-execution pipeline:

```text
pull request to main / push to main / manual dispatch
  → Temurin JDK 21
  → Playwright Chromium installation
  → Maven Wrapper test run
  → allure-results artifact
```

The artifact upload uses `if: always()`, so results are retained when available even if the test command fails.

Report publication is separate:

```text
successful trusted CI run on this repository's main branch
  → Publish Allure Report
  → download artifact from the exact triggering run
  → generate Allure HTML
  → deploy GitHub Pages
```

The publication workflow accepts only successful `push` or `workflow_dispatch` CI runs whose head branch is `main` and whose head repository is the current repository. This prevents Pages deployment from pull-request runs, including fork-originated runs.

## Reliability Decisions

- No fixed `Thread.sleep` calls; Playwright provides auto-waiting and web-first assertions.
- A UI test receives an isolated browser context and page, with no shared static browser lifecycle.
- API requests have finite connection and socket timeouts.
- API tests clean up data they create and verify persisted state where state changes are relevant.
- Structural JSON Schema validation is complemented by strict Java date parsing.
- Screenshot-capture failures are logged without masking the original UI test exception.

## Deliberate Non-Goals

The project does not keep a shared static browser, retry failed tests to hide instability, add abstraction layers without a current use case, or inflate the suite with repetitive scenarios. The scope is a compact set of representative UI and API flows with explicit reliability and diagnostic practices.
