\# AI-Powered Self-Healing Selenium Framework



An AI-assisted test automation framework built using \*\*Java, Selenium WebDriver, TestNG, JavaParser, and Ollama\*\* to detect broken XPath locators, recommend replacements using a local Large Language Model (LLM), and support automated locator recovery.



\## Overview



UI automation scripts often fail after application updates because element attributes, IDs, or XPath locators change.



This project explores an AI-assisted self-healing approach that helps reduce manual locator maintenance.



When an element cannot be located, the framework:



1\. Detects the Selenium locator failure.

2\. Captures relevant DOM context.

3\. Sends the failed XPath and DOM information to a locally running LLM.

4\. Receives a suggested replacement XPath.

5\. Validates the suggested locator against the current page.

6\. Retries the failed element interaction.

7\. Generates a proposed source-code update or applies the change, depending on the configured mode.



\## Technology Stack



| Technology | Purpose |

|---|---|

| Java | Framework implementation |

| Selenium WebDriver | Browser automation |

| TestNG | Test execution |

| Ollama | Local LLM inference |

| Qwen2.5-Coder 3B | AI-based XPath recommendations |

| JavaParser | Java source-code modification |

| Maven | Dependency and build management |



\## Self-Healing Workflow



```text

Selenium Test Execution

&#x20;         |

&#x20;         v

&#x20;  Locate Web Element

&#x20;         |

&#x20;         v

&#x20;  Locator Failed?

&#x20;      /      \\

&#x20;    No        Yes

&#x20;    |          |

&#x20;Continue   Capture DOM Context

&#x20;               |

&#x20;               v

&#x20;       Send Context to Ollama

&#x20;               |

&#x20;               v

&#x20;       Generate Candidate XPath

&#x20;               |

&#x20;               v

&#x20;        Validate Candidate

&#x20;               |

&#x20;         +-----+-----+

&#x20;         |           |

&#x20;       Valid       Invalid

&#x20;         |           |

&#x20;         v           v

&#x20;   Retry Action   Report Failure

&#x20;         |

&#x20;         v

&#x20;   Review or Apply

&#x20;    Source Update

```



\## Key Features



\### AI-Assisted Locator Recovery



Uses a locally hosted coding-focused LLM to suggest replacement XPath expressions based on the current DOM structure.



\### DOM-Based Validation



Checks whether the proposed locator:



\- Matches exactly one element.

\- Identifies a displayed element.

\- Identifies an enabled element.



These checks reduce the risk of applying an unusable locator, but they do not guarantee that the replacement represents the intended business element.



\### Source-Code Update Support



Uses JavaParser to identify and modify XPath declarations in Java page classes.



\### Configurable Healing Modes



\*\*REVIEW mode:\*\* Generates proposed changes for developer review.



\*\*AUTO mode:\*\* Applies validated locator changes to the local Java source code.



AUTO mode is intended for controlled demonstrations and experimentation. Human review remains recommended before committing generated changes.



\### Local AI Processing



Runs the LLM through Ollama, without requiring a paid external LLM API.



\## Project Structure



```text

demo-app/

&#x20; login.html

&#x20; search.html

&#x20; review.html



src/main/java/com/pragya/healing/

&#x20; DomContextCollector.java

&#x20; ElementActions.java

&#x20; HealingService.java

&#x20; OllamaLocatorAdvisor.java

&#x20; SourceUpdater.java



src/test/java/com/pragya/pages/

&#x20; LoginPage.java

&#x20; SearchPage.java

&#x20; ReviewPage.java



src/test/java/com/pragya/tests/

&#x20; EndToEndHealingTest.java



pom.xml

README.md

```



\## Prerequisites



\- Java Development Kit (JDK), compatible with the project's Maven configuration

\- Maven

\- Google Chrome

\- Ollama



Download Ollama from https://ollama.com.



Install the model:



```bash

ollama pull qwen2.5-coder:3b

```



Ensure Ollama is running before executing AI-assisted healing tests.



\## Running the Framework



1\. Clone this repository:



```bash

git clone https://github.com/Pragya-johri/AI-Powered-Self-Healing-Selenium-Framework.git

```



2\. Open the project in IntelliJ IDEA.



3\. Allow Maven to download the required dependencies.



4\. Start Ollama and ensure the configured model is available.



5\. Configure and serve the local demo application as required by the test setup.



6\. Run the TestNG demonstration test from IntelliJ or use the project's Maven test configuration.



The demo application contains a three-page workflow: Login, Search, and Review.



\## Demonstration Scenario



The demonstration simulates UI changes that cause existing XPath locators to fail.



For example:



\*\*Original XPath\*\*



```xpath

//button\[@id='loginBtn']

```



\*\*Updated DOM XPath\*\*



```xpath

//button\[@id='loginButton']

```



The framework detects the failure, requests an alternative locator from the local LLM, validates the suggestion, and retries the interaction.



When source updates are enabled, it can also modify the corresponding Java locator declaration.



\## Limitations and Future Improvements



This project is a proof of concept rather than a production-certified self-healing system.



Potential improvements include:



\- Stronger semantic validation of replacement elements.

\- Confidence scoring and multiple candidate ranking.

\- Human approval workflows for locator changes.

\- More robust handling of complex DOM structures.

\- Integration with CI/CD pipelines.

\- Automated reporting of healing attempts and outcomes.



\## Author



\*\*Pragya Johri\*\*



Java | Selenium | TestNG | AI-Assisted Test Automation



GitHub: https://github.com/Pragya-johri



\---



\*An independent learning and portfolio project demonstrating practical AI integration into UI test automation.\*

