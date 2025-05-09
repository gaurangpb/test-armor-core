# 🛡️ test-armor-core

[![Java](https://img.shields.io/badge/java-8%2B-blue.svg)](https://www.oracle.com/java/technologies/javase-downloads.html)

[![License: MIT](https://img.shields.io/badge/license-MIT-yellow.svg)](https://chatgpt.com/c/LICENSE)

[![TestNG](https://img.shields.io/badge/TestNG-supported-orange)](https://testng.org/)

[![Cucumber](https://img.shields.io/badge/Cucumber-BDD-23d96c.svg)](https://cucumber.io/)

[![Selenium](https://img.shields.io/badge/Selenium-Automation-43B02A)](https://www.selenium.dev/)

## 📖 Project Overview

`test-armor-core` is a Java-based test automation framework designed for web application testing. It leverages  **TestNG** ,  **Cucumber** , and **Selenium WebDriver** to provide a robust and scalable solution for behavior-driven development (BDD).

---

## ✨ Features

* 🧱 **Page Object Model (POM):** Clean separation of concerns for maintainability.
* 🥒 **Cucumber Integration:** Write expressive test scenarios in Gherkin syntax.
* 🧪 **TestNG Support:** Flexible test execution and parallel runs.
* 🌐 **Cross-Browser Testing:** Seamless integration with  **Sauce Labs** .
* 🧰 **Utilities:** Built-in tools for CSV/JSON parsing and test context management.
* 📊 **Reporting:** Generates Extent and Cucumber HTML reports.

---

## 🗂️ Project Structure

```
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── core/
│   │   │   │   ├── base/                # Base classes
│   │   │   │   ├── config/              # Configuration management
│   │   │   │   ├── drivers/             # WebDriver & Sauce Labs integration
│   │   │   │   ├── hooks/               # Setup & teardown logic
│   │   │   │   ├── reports/             # Reporting utilities
│   │   │   │   └── util/                # Common utilities
│   ├── test/
│   │   ├── java/
│   │   │   ├── example/
│   │   │   │   ├── hooks/               # Test-specific hooks
│   │   │   │   ├── pages/               # Page Object classes
│   │   │   │   ├── runners/             # Test runners
│   │   │   │   └── stepdefs/            # Step definitions
│   │   └── resources/
│   │       ├── features/                # Cucumber feature files
│   │       ├── core-config.properties
│   │       ├── cucumber-report.properties
│   │       ├── extent.properties
│   │       └── testng.xml
```

---

## 🚀 Getting Started

### ✅ Prerequisites

* Java 8+
* Maven
* Chrome/Firefox browser
* IDE (e.g., IntelliJ IDEA, Eclipse)

### ⚙️ Setup

```bash
git clone https://github.com/gaurangpb/test-armor-core
cd test-armor-core
mvn clean install
```

---

## 🧲 Running Tests

### Run all TestNG tests

```bash
mvn test
```

### Generate reports

```bash
mvn verify
```

---

## 🧱 Tech Stack

* ☕ **Java**
* 🧪 **TestNG**
* 🥒 **Cucumber**
* 🕸️ **Selenium WebDriver**
* ☁️ **Sauce Labs**
* 🛠️ **Extent Reports**

---

## 🤝 Contributing

Contributions are welcome! Follow the steps:

1. 🍚 Fork the repository
2. 🌱 Create a new branch
3. 📀 Make changes and commit
4. 🚀 Submit a Pull Request

---

## 📄 License

This project is licensed under the [MIT License](https://chatgpt.com/c/LICENSE).

---

## 🔗 Useful Links

* [Selenium Documentation](https://www.selenium.dev/documentation/)
* [Cucumber Docs](https://cucumber.io/docs/)
* [TestNG Guide](https://testng.org/doc/)
* [Extent Reports](https://extentreports.com/docs/versions/4/java/)
* [Sauce Labs](https://saucelabs.com/)
