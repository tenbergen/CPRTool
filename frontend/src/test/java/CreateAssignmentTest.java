import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class CreateAssignmentTest {
    private static WebDriver driver;

    @BeforeAll
    public static void setupClass() {
        System.setProperty("webdriver.chrome.driver", "src/test/WebDrivers/chromedriver.exe");
        driver = new ChromeDriver();
        // driver.get("http://moxie.cs.oswego.edu:13125/");
        // driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);


        //driver.get("http://moxie.cs.oswego.edu:13125/");
        driver.get("http://localhost:3000");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#box > button")));
        driver.findElement(By.cssSelector("#box > button")).click();

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

//Get all the window handles in a set
        Set<String> handles = driver.getWindowHandles();
        Iterator<String> it = handles.iterator();
//iterate through your windows
        while (it.hasNext()) {
            String parent = it.next();
            String newwin = it.next();
            driver.switchTo().window(newwin);
//perform actions on new window
            driver.findElement(By.xpath("//*[@id=\"identifierId\"]")).sendKeys("id");
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
            driver.findElement(By.cssSelector("#identifierNext > div > button > span")).click();

            driver.findElement(By.xpath("//*[@id=\"password\"]/div[1]/div/div[1]/input")).sendKeys("password");
            driver.findElement(By.cssSelector("#passwordNext > div > button > span")).click();
            // driver.close();
            driver.switchTo().window(parent);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        }
    }

@Test
    public void CreateAssignmentTest() {

        driver.findElement(By.xpath("//*[@id=\"courseList\"]/a[1]/li")).click();
        driver.findElement(By.xpath("//*[@id=\"assAddButton\"]")).click();

        driver.findElement(By.cssSelector("#root > div > div > div > div.cap-container > form > div.cap-input-field > input[type=text]")).sendKeys("Assignment5");
        driver.findElement(By.cssSelector("#root > div > div > div > div.cap-container > form > div:nth-child(2) > input[type=text]")).sendKeys("HI");
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div[2]/form/div[3]/input")).sendKeys("C:\\Week 9.pdf");


        driver.findElement(By.cssSelector("#root > div > div > div > div.cap-container > form > div:nth-child(4) > input[type=date]:nth-child(2)")).sendKeys("033120228");
        driver.findElement(By.cssSelector("#root > div > div > div > div.cap-container > form > div:nth-child(4) > input[type=number]:nth-child(4)")).sendKeys("50");
        driver.findElement(By.cssSelector("#root > div > div > div > div.cap-container > form > div:nth-child(5) > input[type=text]")).sendKeys("Liam");

        // driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div[2]/form/div[6]/input")).sendKeys("C\\Week 9.pdf");
        driver.findElement(By.cssSelector("#root > div > div > div > div.cap-container > form > div:nth-child(7) > input[type=date]:nth-child(2)")).sendKeys("03312022");
        driver.findElement(By.cssSelector("#root > div > div > div > div.cap-container > form > div:nth-child(7) > input[type=number]:nth-child(4)")).sendKeys("50");

        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div[2]/form/div[8]/button")).click();

    Assertions.assertTrue(driver.findElement(By.cssSelector("#assList > li")).getText().contains("Assignment5"));
    }


}
