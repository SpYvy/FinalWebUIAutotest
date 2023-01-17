package ru.gb.webUITesting;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class LoginPage extends AbstractPage {
    private final String userNameFieldXpath = "//*[@class=\"mdc-text-field__input\" and @type=\"text\"]";
    private final String passwordFieldXpath = "//*[@class=\"mdc-text-field__input\" and @type=\"password\"]";
    private static final String loginButtonXpath = "//*[@class=\"mdc-button__label\" and text()=\"Login\"]";
    private static final String errorNumberXpath = "//div[contains(@class, \"error-block\")]/h2";
    private static final String errorTextXpath = "//div[contains(@class, \"error-block\")]/p[1]";
    @FindBy(xpath = userNameFieldXpath)
    private WebElement userNameField;
    @FindBy(xpath = passwordFieldXpath)
    private WebElement passwordField;
    @FindBy(xpath = loginButtonXpath)
    private WebElement loginButton;
    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public MyPostsPage login(String userName, String password){
        userNameField.sendKeys(userName);
        passwordField.sendKeys(password);
        loginButton.click();
        return new MyPostsPage(driver);
    }
    static Properties properties = new Properties();
    public MyPostsPage loginAsAccount(int numberOfPosts) throws IOException {
        properties.load(new FileInputStream("src/main/resources/my.properties"));
        userNameField.sendKeys(properties.getProperty(numberOfPosts + "postUserName"));
        passwordField.sendKeys(properties.getProperty(numberOfPosts + "postUserPassword"));
        loginButton.click();
        return new MyPostsPage(driver);
    }

    public static String getLoginButtonXpath() {
        return loginButtonXpath;
    }

    public static String getAssertErrorNumber() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(errorNumberXpath)));
        return errorNumberXpath;
    }
    public static String getAssertErrorText() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(errorTextXpath)));
        return errorTextXpath;
    }
}

