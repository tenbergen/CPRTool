import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginAndMultiBrowser {

    private static WebDriver cDriver;
    private static WebDriver fDriver;

    @BeforeAll
    public static void initSetup(){
        // initialize the chrome driver
        System.setProperty("webdriver.chrome.driver", "src/test/WebDrivers/chromedriver.exe");
        System.setProperty("webdriver.gecko.driver", "src/test/WebDrivers/geckodriver.exe");
        cDriver = new ChromeDriver();
        fDriver = new FirefoxDriver();
    }

    @Test
    public void LoginTest(){

        // enter the front end app
        cDriver.get("http://129.3.168.61:13129/");
        // maximize the window
        cDriver.manage().window().maximize();

        // click the login button
        WebDriverWait wait = new WebDriverWait(cDriver,10);
        WebElement loginButton = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector(".googleButton > button:nth-child(1)")));
        loginButton.click();

    }

    @Test
    public void openEditCourseTest(){

        cDriver.get("http://129.3.168.61:13129/");
        cDriver.manage().window().maximize();
        WebDriverWait wait = new WebDriverWait(cDriver,10);
        WebElement loginButton = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector(".googleButton > button:nth-child(1)")));
        loginButton.click();

        // wait for our desired element to appear
        // in this case, that's the first course element
        WebElement createCourseButton = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("#courseList > ul:nth-child(1) > a:nth-child(1) > li:nth-child(1)")));

        // click course link
        createCourseButton.click();
    }

    @Test
    public void LoginTestFireFox(){

        // enter the front end app
        fDriver.get("http://129.3.168.61:13129/");
        // maximize the window
        fDriver.manage().window().maximize();

        // click the login button
        WebDriverWait wait = new WebDriverWait(fDriver,10);
        WebElement loginButton = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector(".googleButton > button:nth-child(1)")));
        loginButton.click();

    }

    @Test
    public void openEditCourseTestFireFox(){

        fDriver.get("http://129.3.168.61:13129/");
        fDriver.manage().window().maximize();
        WebDriverWait wait = new WebDriverWait(fDriver,10);
        WebElement loginButton = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector(".googleButton > button:nth-child(1)")));
        loginButton.click();

        // wait for our desired element to appear
        // in this case, that's the first course element
        WebElement createCourseButton = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("#courseList > ul:nth-child(1) > a:nth-child(1) > li:nth-child(1)")));

        // click course link
        createCourseButton.click();
    }

    @AfterAll
    public static void teardown(){
        cDriver.quit();
        fDriver.quit();
    }
}
