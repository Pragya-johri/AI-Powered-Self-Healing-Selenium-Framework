package com.pragya.pages;

import com.pragya.healing.ElementActions;
import org.openqa.selenium.By;

public class ReviewPage {

    private final ElementActions actions;

    private final By confirmBtn = By.xpath("//button[@id='confirmButton']");

    public ReviewPage(ElementActions actions) {
        this.actions = actions;
    }

    public void confirm() {
        actions.clickElement(confirmBtn);
    }
}
