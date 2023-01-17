package ru.gb.webUITest;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.gb.webUITesting.LoginPage;
import ru.gb.webUITesting.MyPostsPage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class myPostsWebUITest {
    static Properties prop = new Properties();
    private static String loginUrl;
    private static String myPostsUrl;
    private static String userName;
    private static String password;
    private static String bigUserName;
    private static String bigUserNamePassword;
    WebDriver driver;
    LoginPage loginPage;
    WebDriverWait wait;
    @BeforeAll
    static void registerDriver() throws IOException {
        WebDriverManager.chromedriver().setup();

        InputStream configFile = new FileInputStream("src/main/resources/my.properties");
        prop.load(configFile);

        loginUrl = prop.getProperty("loginUrl");
        myPostsUrl = prop.getProperty("myPostsUrl");
        userName = prop.getProperty("userName");
        password = prop.getProperty("password");
        //bigUserName имеет 10 постов
        bigUserName = prop.getProperty("bigUserName");
        bigUserNamePassword = prop.getProperty("bigUserNamePassword");

    }

    @BeforeEach
    void setupBrowser() {
        driver = new ChromeDriver();
        driver.get(loginUrl);
        loginPage = new LoginPage(driver);
    }

    @DisplayName("Проверка что на одной странице 10 постов")
    @Test
    void checkPostsOnPage() throws IOException, InterruptedException {
        MyPostsPage myPostsPage = loginPage.loginAsAccount(9);
        List<WebElement> posts = myPostsPage.getPosts();
        assertThat(posts.size(), is(10));
    }

    @DisplayName("Проверка, что пользователь может перейти на следующую и предыдущую страницы своих постов.")
    @Test
    void checkUserCanNavigateToNextAndPreviousPagesOfTheirPosts() throws IOException {
        MyPostsPage myPostsPage = loginPage.loginAsAccount(9);
        List<WebElement> posts = myPostsPage.getPosts();
        while (myPostsPage.isNextPageButtonActive()) {
            myPostsPage.nextPageClick();
            posts.addAll(myPostsPage.getPosts());
        }
        assertThat(posts.get(0).getAttribute("href").replace("/posts/", ""),
                lessThan(posts.get(0).getAttribute("href").replace("/posts/", "")));
        while (myPostsPage.isPreviousPageButtonActive()) {
            myPostsPage.previousPageClick();
            }
        assertThat(posts.get(0).getAttribute("href").replace("/posts/", ""),
                greaterThan(posts.get(0).getAttribute("href").replace("/posts/", "")));
        }

    @DisplayName("Проверка, что каждый пост пользователя имеет изображение, название и описание.")
    @ParameterizedTest
    @CsvSource({
            "0, false",
            "1, true",
            "4, true",
            "9, true",
            })
    void checkUserPostsDisplayedWhenUserHasAtLeastOnePost(int numberOfPosts, boolean notEmpty) throws IOException {
        if(notEmpty) {
            loginPage.loginAsAccount(numberOfPosts)
                    //Метод checkAllPosts(1) проверит наличие у каждого поста изображения, названия и описания
                    .checkAllPostsHasImgNameDescription(numberOfPosts);
        } else {
            loginPage.loginAsAccount(numberOfPosts)
                    .checkNoPosts();
        }
    }
    @AfterEach
    void tearDown() {
        driver.quit();
    }
}
