package com.pragya.pages;

import com.pragya.healing.ElementActions;
import org.openqa.selenium.By;

public class SearchPage {

    private final ElementActions actions;

    private final By searchBtn = By.xpath("//button[@id='searchButton']");

    public SearchPage(ElementActions actions) {
        this.actions = actions;
    }

    public void search() {
        actions.safeClick(searchBtn);
    }
}
