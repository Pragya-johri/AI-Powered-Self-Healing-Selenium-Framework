package com.pragya.healing;

import org.openqa.selenium.*;
import java.util.*;

/** A bounded, sanitized candidate summary, not an unrestricted page dump. */
public final class DomContextCollector {
    private DomContextCollector() {}
    public static String collect(WebDriver driver, String action) {
        String selector = action.equals("SEND_KEYS")
            ? "input,textarea,[contenteditable='true'],[role='textbox']"
            : "button,a,input[type='button'],input[type='submit'],[role='button']";
        List<WebElement> elements = driver.findElements(By.cssSelector(selector));
        StringBuilder out = new StringBuilder();
        int count=0;
        for (WebElement element : elements) {
            if (++count > 60 || out.length() > 12000) break;
            try {
                // Only allowlisted metadata. Never send values, pageSource, cookies or full outerHTML.
                String tag=element.getTagName();
                String id=safe(element.getAttribute("id"));
                String name=safe(element.getAttribute("name"));
                String role=safe(element.getAttribute("role"));
                String type=safe(element.getAttribute("type"));
                String aria=safe(element.getAttribute("aria-label"));
                String testId=safe(element.getAttribute("data-testid"));
                String text=safe(element.getText());
                out.append("candidate ").append(count).append(": tag=").append(tag)
                   .append(" id=").append(id).append(" name=").append(name)
                   .append(" role=").append(role).append(" type=").append(type)
                   .append(" aria-label=").append(aria).append(" data-testid=").append(testId)
                   .append(" text=").append(text).append("\n");
            } catch (StaleElementReferenceException ignored) { }
        }
        return out.toString();
    }
    private static String safe(String value) {
        if (value==null) return "";
        // Demo-only minimization, not a comprehensive PHI/PII redactor.
        String cleaned=value.replaceAll("[\\r\\n\\t]", " ").replaceAll("[<>]", "");
        return cleaned.substring(0, Math.min(100, cleaned.length()));
    }
}
