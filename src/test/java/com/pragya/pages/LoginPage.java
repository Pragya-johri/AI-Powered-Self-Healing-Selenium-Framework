package com.pragya.pages;

import com.pragya.healing.ElementActions;
import org.openqa.selenium.By;

public class LoginPage {

    private final ElementActions actions;

    private final By userInput = By.xpath("//input[@id='userInputV2']");

    private final By loginBtn = By.xpath("//button[@id='loginButton']");

    public LoginPage(ElementActions actions) {
        this.actions = actions;
    }

    public void login() {
        actions.enterInput(userInput, "demo-user");
        actions.clickElement(loginBtn);
    }
}
