# AI-Powered Locator Healing V5 — Local Ollama (Java / Selenium / TestNG)

**Real local LLM inference**, no paid API or API key. Built as a synthetic, GitHub-safe proof of concept. Based on V4, with `OllamaLocatorAdvisor` replacing the OpenAI client.

## Requirements
- Java 17+, Maven, Google Chrome (Selenium Manager downloads a driver if necessary).
- Ollama installed and running on your personal Windows laptop.
- Local model installed: `ollama pull qwen2.5-coder:3b`.
- Keep Ollama bound to localhost; do not expose port 11434 to your network.

## Open in IntelliJ
1. Unzip, open **this folder containing `pom.xml`** as a project.
2. Wait for IntelliJ Maven dependency import. Use Java 17 or later for Project SDK.
3. In PowerShell, run `ollama list` and `Invoke-RestMethod http://127.0.0.1:11434/api/tags`.
4. Open PowerShell in the project root and run `mvn clean test` (or run the TestNG test in IntelliJ).
5. Observe `[LOCATOR FAILED]`, `[OLLAMA MODEL]`, `[OLLAMA PROPOSAL]`, `[CANDIDATE VALIDATED]`, `[PATCH PROPOSED]`, `[RETRY PASSED]`.
6. Review `reports/proposed-patches.txt`. Source remains unchanged by default; every fresh run should demonstrate healing.

## Optional automatic source update (synthetic demo only)
In PowerShell, from the project root:

```powershell
$env:HEALING_MODE = "AUTO"
mvn clean test
```

The source updater creates `.java.bak` files, then edits the unique matching `By.xpath("...")` string literal in the calling `com.pragya.pages.*` Page Object. On the next Maven run, recompilation uses the corrected source. To repeat the demo, restore from backups or unzip a fresh copy.

## Architecture
- `ElementActions`: `clickElement(By)`, `safeClick(By)`, `enterInput(By, String)`.
- `DomContextCollector`: collects **up to 60** bounded clickable/input candidates (tag, id, name, role, type, aria-label, data-testid, visible text). No full page dump or input values.
- `OllamaLocatorAdvisor`: POST `http://127.0.0.1:11434/api/generate` with `model=qwen2.5-coder:3b`, `stream=false`, `format=json`. Parses model response as JSON. No mock response.
- `HealingService`: requires one visible, enabled XPath match; fails closed on uncertainty or errors.
- `SourceUpdater`: uses stack trace to locate demo Page Object source, JavaParser to edit only a unique matching `By.xpath` literal. REVIEW by default, AUTO opt-in.
- `demo-app`: 3 linked local HTML pages; 4 deliberately broken locators.

## Security and limitations
- Local Ollama inference does not need an external LLM API. Model installation requires a download. Never expose Ollama to the network.
- Candidate metadata is **not a comprehensive sensitive-data redactor**. Only use synthetic HTML. Never use confidential or healthcare DOM in a personal GitHub demo.
- AI output is untrusted. Unique/visible/enabled does **not** prove semantic identity or correct business behavior. Do not use unattended for real approval/denial flows.
- This demo supports **literal `By.xpath` fields** in `com.pragya.pages.*`, not `@FindBy`, dynamic locators, arbitrary source layouts, iframe/shadow-root context, or production-grade source resolution.
- REVIEW mode retries in the current JVM but does not persist fixes; AUTO persists source edits. No real model request or Maven test was run during project packaging. You must validate locally.
- Some failures are caused by waits, frames, navigation, or overlays rather than changed locators. The engine only attempts healing after a missing-locator failure.

## GitHub
Commit source, `pom.xml`, demo HTML, and this README. Do not commit generated reports, `.bak` files, `.env`, model files, or sensitive data.
