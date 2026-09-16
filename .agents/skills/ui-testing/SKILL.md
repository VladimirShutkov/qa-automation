---
name: ui-testing
description: Использовать при работе с UI-тестами Playwright, Page Objects, UI-компонентами, локаторами, ожиданиями и UI test failures.
---

# UI-тестирование

- Использовать Page Objects и переиспользуемые UI-компоненты.
- Не размещать raw selectors непосредственно в тестах.
- Предпочитать локаторы: role, label, test id, стабильный semantic CSS.
- Избегать XPath и positional selectors без необходимости.
- Один сценарий должен проверять одно наблюдаемое поведение.
- Тесты должны быть изолированными и по возможности безопасными для параллельного запуска.
- Использовать Playwright auto-waiting.
- Не использовать Thread.sleep и произвольные retry loops.
- Использовать condition-based waits только когда auto-waiting недостаточно.
- Assertions должны проверять результат поведения, а не внутреннюю реализацию.
- При падении проверить locator, состояние элемента, timing/waits, test data и состояние страницы.