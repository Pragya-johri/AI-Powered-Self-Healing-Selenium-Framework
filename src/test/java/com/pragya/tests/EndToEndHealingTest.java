package com.pragya.tests;
import com.pragya.healing.ElementActions;
import com.pragya.pages.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.nio.file.Paths;

public class EndToEndHealingTest {
    @Test
    public void multiPageFlowWithFourBrokenLocators() {
        WebDriver driver=new ChromeDriver();
        try {
            ElementActions actions=new ElementActions(driver);
            driver.get(Paths.get("demo-app/login.html").toAbsolutePath().toUri().toString());
            new LoginPage(actions).login();
            Assert.assertTrue(driver.getCurrentUrl().contains("search.html"));
            new SearchPage(actions).search();
            Assert.assertTrue(driver.getCurrentUrl().contains("review.html"));
            new ReviewPage(actions).confirm();
            Assert.assertEquals(driver.findElement(By.id("result")).getText(),"SUCCESS");
            System.out.println("[TEST PASS] Three pages, local Ollama locator proposals");
        } finally { driver.quit(); }
    }
}
