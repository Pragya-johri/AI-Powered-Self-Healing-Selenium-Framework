package com.pragya.healing;

import org.openqa.selenium.*;
import java.util.*;

/** AI-first healing. Fail closed if no key, uncertain model output, or unsafe XPath. */
public final class HealingService {
    private HealingService() {}
    public record Result(By by, String oldXpath, String newXpath) {}
    public static Result heal(WebDriver driver, By failed, String action, StackTraceElement[] trace) {
        String old=failed.toString();
        if (!old.startsWith("By.xpath: ")) throw new IllegalArgumentException("Only By.xpath is supported: "+old);
        old=old.substring("By.xpath: ".length());
        System.out.println("[LOCATOR FAILED] "+action+" "+old);
        String candidates=DomContextCollector.collect(driver,action);
        if (candidates.isBlank()) throw new IllegalStateException("No DOM candidates found");
        OllamaLocatorAdvisor.Suggestion proposed;
        try { proposed=OllamaLocatorAdvisor.suggest(old,action,candidates); }
        catch (Exception e) { throw new IllegalStateException("Ollama unavailable; no source changes made",e); }
        System.out.println("[LLM RESPONSE] "+proposed.explanation());
        if (proposed.uncertain() || proposed.xpath().isBlank()) throw new IllegalStateException("LLM uncertain; manual review required");
        String xpath=proposed.xpath();
        if (xpath.length()>350 || !(xpath.startsWith("//") || xpath.startsWith(".//")))
            throw new IllegalStateException("Rejected unsupported proposed XPath");
        By replacement=By.xpath(xpath);
        List<WebElement> found=driver.findElements(replacement);
        if (found.size()!=1 || !found.get(0).isDisplayed() || !found.get(0).isEnabled())
            throw new IllegalStateException("Proposed XPath failed uniqueness/visibility/enabled checks");
        // This does not prove semantic equivalence. Review patches before use in consequential flows.
        System.out.println("[CANDIDATE VALIDATED] "+xpath);
        try { SourceUpdater.update(old,xpath,trace); }
        catch (Exception e) { throw new IllegalStateException("Source update refused",e); }
        return new Result(replacement,old,xpath);
    }
}
