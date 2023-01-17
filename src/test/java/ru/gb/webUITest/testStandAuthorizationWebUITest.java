package ru.gb.webUITest;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.gb.webUITesting.LoginPage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.gb.webUITesting.LoginPage.*;
import static ru.gb.webUITesting.MyPostsPage.getAssertElementBlogTitle;

public class testStandAuthorizationWebUITest {
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
        bigUserName = prop.getProperty("bigUserName");
        bigUserNamePassword = prop.getProperty("bigUserNamePassword");
    }

    @BeforeEach
    void setupBrowser() {
        driver = new ChromeDriver();
        driver.get(loginUrl);
        loginPage = new LoginPage(driver);
    }

    @DisplayName("Проверка входа в аккаунт")
    @Test
    void loginTest() {
        loginPage.login(userName, password);
        Assertions.assertAll(
                () -> assertThat(driver.getCurrentUrl()).isEqualTo(myPostsUrl),
                () -> assertThat(driver.findElement(By.xpath(getAssertElementBlogTitle())).isDisplayed()).isTrue());
    }
    @DisplayName("Проверка входа в аккаунт с невалидным паролем")
    @Test
    void loginWithInvalidPasswordTest() {
        loginPage.login(userName, "123456789");
        Assertions.assertAll(
                () -> assertThat(driver.getCurrentUrl()).isEqualTo(loginUrl),
                () -> assertThat(driver.findElement(By.xpath(getAssertErrorNumber())).isDisplayed()).isTrue(),
                () -> assertThat(driver.findElement(By.xpath(getAssertErrorNumber())).getText()).isEqualTo("401"),
                () -> assertThat(driver.findElement(By.xpath(getAssertErrorText())).isDisplayed()).isTrue(),
                () -> assertThat(driver.findElement(By.xpath(getAssertErrorText())).getText()).isEqualTo("Invalid credentials."));
    }
    @DisplayName("Проверка входа в аккаунт с валидным длинным логином")
    @Test
    void loginWithBigUserNameTest() {
        loginPage.login(bigUserName, bigUserNamePassword);
        Assertions.assertAll(
                () -> assertThat(driver.getCurrentUrl()).isEqualTo(myPostsUrl),
                () -> assertThat(driver.findElement(By.xpath(getAssertElementBlogTitle())).isDisplayed()).isTrue());
    }

    @DisplayName("Проверка валидных граничных значений логина")
    @ParameterizedTest
    @CsvSource({
            "Rob,e4310b75f2,true",
            "John,61409aa1fd,true",
            "ThisLoginHas19Symbs,93393315f5,true",
            "ThisLoginHas20Symbol,270b77da24,true",
            "Op,38c4658d53,false",
            "ThisLoginHas21Symbols,cef241082c,false",
    })
    void loginWithValidBoundaryValuesTest(String login, String password, boolean shouldPass) {
        loginPage.login(login, password);
        String currentUrl = driver.getCurrentUrl();
        if (shouldPass) {
            Assertions.assertAll(
                    () -> assertThat(driver.getCurrentUrl()).isEqualTo(myPostsUrl),
                    () -> assertThat(driver.findElement(By.xpath(getAssertElementBlogTitle())).isDisplayed()).isTrue());
        } else {
            Assertions.assertAll(
                    () -> assertThat(currentUrl).isNotEqualTo(myPostsUrl),
                    () -> assertThat(currentUrl).isEqualTo(loginUrl));
        }
    }
    @AfterEach
    void tearDown() {
        driver.quit();
    }
}

