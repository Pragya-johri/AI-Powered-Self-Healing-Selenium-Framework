package com.pragya.healing;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;

/** Centralized By-only actions. Original signatures preserved conceptually. */
public class ElementActions {
    private final WebDriver driver;
    public ElementActions(WebDriver driver) { this.driver=driver; }
    public void clickElement(By by) {
        try { driver.findElement(by).click(); }
        catch (NoSuchElementException ex) {
            By healed=HealingService.heal(driver,by,"CLICK",Thread.currentThread().getStackTrace()).by();
            driver.findElement(healed).click();
            System.out.println("[RETRY PASSED] clickElement");
        }
    }
    public void enterInput(By by, String key) {
        try { driver.findElement(by).sendKeys(key); }
        catch (NoSuchElementException ex) {
            By healed=HealingService.heal(driver,by,"SEND_KEYS",Thread.currentThread().getStackTrace()).by();
            driver.findElement(healed).sendKeys(key);
            System.out.println("[RETRY PASSED] enterInput");
        }
    }
    public void safeClick(By locator) {
        By loader=By.cssSelector("div.uitk-busyindicator-overlay");
        WebDriverWait wait=new WebDriverWait(driver,Duration.ofSeconds(10));
        By current=locator;
        boolean healingAttempted=false;
        for (int i=0;i<4;i++) {
            try {
                wait.until(ExpectedConditions.elementToBeClickable(current)).click();
                System.out.println("[CLICK PASSED] safeClick");
                return;
            } catch (ElementClickInterceptedException ex) {
                System.out.println("[LOADER RETRY] attempt " +(i+1));
                wait.until(d->d.findElements(loader).stream().noneMatch(WebElement::isDisplayed));
            } catch (TimeoutException ex) {
                // A present but non-clickable element is not a locator failure.
                if (!driver.findElements(current).isEmpty() || healingAttempted) throw ex;
                healingAttempted=true;
                current=HealingService.heal(driver,current,"CLICK",Thread.currentThread().getStackTrace()).by();
            }
        }
        throw new IllegalStateException("safeClick failed after 4 attempts");
    }
}
