import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SeleniumTests {

    private static WebDriver cDriver;

    @BeforeAll
    public static void initSetup(){
        // initialize the chrome driver
        System.setProperty("webdriver.chrome.driver", "src/test/WebDrivers/chromedriver.exe");
        cDriver = new ChromeDriver();
    }

    @Test
    public void LoginTest(){

        // enter the front end app
        cDriver.get("http://lawnboy:13129");

        // maximize the window
        cDriver.manage().window().maximize();

        // click the login button
        cDriver.findElement(By.className("google")).click();

    }

    @Test
    public void openEditCourseTest(){

        cDriver.get("http://lawnboy:13129");
        cDriver.manage().window().maximize();
        cDriver.findElement(By.className("google")).click();

        // wait for our desired element to appear
        // in this case, that's the first course element
        WebDriverWait wait = new WebDriverWait(cDriver,10);
        WebElement createCourseButton = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("#courseList > ul:nth-child(1) > a:nth-child(1) > li:nth-child(1)")));

        // click course link
        createCourseButton.click();
    }

    @AfterAll
    public static void teardown(){
        cDriver.quit();
    }
}
