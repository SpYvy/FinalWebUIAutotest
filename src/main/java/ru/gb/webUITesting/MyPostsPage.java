package ru.gb.webUITesting;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class MyPostsPage extends AbstractPage {
    private static final String blogTitleXpath = "//h1[contains(text(), \"Blog\")]";
    private static final String postsXpath = "//a[contains(@class,\"post\")]";
    private static final String nextPageXpath = "//a[contains(text(), \"Next Page\")]";
    private static final String nextPageEnabledXpath = "//a[contains(text(), \"Next Page\") and not(contains(@class, \"disabled\"))]";
    private static final String nextPageDisabledXpath = "//a[contains(text(), \"Next Page\") and contains(@class, \"disabled\")]";
    private static final String previousPageXpath = "//a[contains(text(), \"Previous Page\")]";
    private static final String previousPageEnabledXpath = "//a[contains(text(), \"Previous Page\") and not(contains(@class, \"disabled\"))]";
    private static final String previousPageDisabledXpath = "//a[contains(text(), \"Previous Page\") and contains(@class, \"disabled\")]";
    public MyPostsPage(WebDriver driver) {
        super(driver);
    }
    @FindBy(xpath = blogTitleXpath)
    private WebElement blogTitle;

    public static String getAssertElementBlogTitle() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(blogTitleXpath)));
        return blogTitleXpath;
    }
    public PostPage openPost(int number){
        if(number < 1 || number > 4) {
            throw new IllegalArgumentException("Номер поста должен быть от 1 до 4");
        } else {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(postsXpath)));
            List<WebElement> posts = driver.findElements(By.xpath(postsXpath));
            posts.get(number-1).click();
        return new PostPage(driver);
        }
    }
    public boolean isNextPageButtonActive() {
        List<WebElement> elementList = driver.findElements(By.xpath(nextPageEnabledXpath));
        if (elementList.size() > 0) {
            return true;
        } else {
            return false;
        }
    }
    public boolean isPreviousPageButtonActive() {
        List<WebElement> elementList = driver.findElements(By.xpath(previousPageEnabledXpath));
        if (elementList.size() > 0) {
            return true;
        } else {
            return false;
        }
    }
    public MyPostsPage nextPageClick(){
        if(isNextPageButtonActive()) {
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath(nextPageEnabledXpath)));
            driver.findElement(By.xpath(nextPageEnabledXpath)).click();
        } else {
            System.out.println("Похоже, что следующая страница отсутствует");
        }
        return this;
    }
    public MyPostsPage previousPageClick(){
        if(isPreviousPageButtonActive()) {
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath(previousPageEnabledXpath)));
            driver.findElement(By.xpath(previousPageEnabledXpath)).click();
        } else {
            System.out.println("Похоже, что предыдущая страница отсутствует");
        }
        return this;
    }
    public List<WebElement> getPosts() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(postsXpath)));
            return driver.findElements(By.xpath(postsXpath));
        } catch (NoSuchElementException e) {
            return null;
        }
    }
    //Сохраняем все посты пользователя в лист, проверяем что посты содержат изображения, заголовки и описание
    public LoginPage checkAllPostsHasImgNameDescription(int numberOfPosts){
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(postsXpath)));
        List<WebElement> posts = driver.findElements(By.xpath(postsXpath));
        //если кнопка следующей страницы активна, то кликаем по ней и сохраняем посты на странице
        //try catch нужен, чтобы не падал тест, если кнопка следующей страницы не активна
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(nextPageXpath)));
        if(numberOfPosts>4){
            while (isNextPageButtonActive()) {
                nextPageClick();
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(postsXpath)));
                posts.addAll(driver.findElements(By.xpath(postsXpath)));
            }
        }
        //Проверяем что все посты содержат изображения, заголовки и описание
        for (WebElement post : posts) {
            assertThat(post.findElement(By.tagName("img")).isDisplayed(), is(true));
            assertThat(post.findElement(By.tagName("h2")).isDisplayed(), is(true));
            assertThat(post.findElement(By.className("description")).isDisplayed(), is(true));
        }
        return new LoginPage(driver);
    }
    public void checkNoPosts() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(blogTitleXpath)));
        List<WebElement> posts = driver.findElements(By.xpath(postsXpath));
        assertThat(posts.size(), equalTo(0));
    }
}
