import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class CreateDeleteAssignmentTest {
    private static WebDriver driver;

    @BeforeAll
    public static void setupClass() {
        System.setProperty("webdriver.chrome.driver", "src/test/WebDrivers/chromedriver.exe");
        driver = new ChromeDriver();
        // driver.get("http://moxie.cs.oswego.edu:13125/");
        // driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);


        //driver.get("http://moxie.cs.oswego.edu:13125/");
        driver.get("http://localhost:3000/");
        driver.manage().window().maximize();

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

        driver.findElement(By.xpath("//*[@id=\"proCourseList\"]/a[4]/li")).click();
        driver.findElement(By.xpath("//*[@id=\"assAddButton\"]")).click();

        driver.findElement(By.cssSelector("#root > div > div > div > div > div.ccp-container > div.pcp-components > div > form > div:nth-child(1) > div.field-content > div.input-field.cap-input-field > input[type=text]")).sendKeys("Assignment2");
        driver.findElement(By.cssSelector("#root > div > div > div > div > div.ccp-container > div.pcp-components > div > form > div:nth-child(1) > div.field-content > div.input-field.cap-instructions > textarea")).sendKeys("HIz");
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div/form/div[1]/div[2]/div[3]/input")).sendKeys("C:\\Week 9.pdf");


        driver.findElement(By.cssSelector("#root > div > div > div > div > div.ccp-container > div.pcp-components > div > form > div:nth-child(1) > div.field-content > div.input-field.cap-assignment-info > input[type=date]:nth-child(2)")).sendKeys("03312022");
        driver.findElement(By.cssSelector("#root > div > div > div > div > div.ccp-container > div.pcp-components > div > form > div:nth-child(1) > div.field-content > div.input-field.cap-assignment-info > input[type=number]:nth-child(4)")).sendKeys("40");
        driver.findElement(By.cssSelector("#root > div > div > div > div > div.ccp-container > div.pcp-components > div > form > div:nth-child(2) > div.field-content > div.input-field.cap-instructions > textarea")).sendKeys("Bye");


        //driver.findElement(By.cssSelector("#root > div > div > div > div > div.ccp-container > div.pcp-components > div")).sendKeys(Keys.CONTROL, Keys.END);


        driver.findElement(By.cssSelector("#root > div > div > div > div > div.ccp-container > div.pcp-components > div > form > div:nth-child(2) > div.field-content > div.input-field.cap-assignment-info > input[type=date]:nth-child(2)")).sendKeys("03312022");
        driver.findElement(By.cssSelector("#root > div > div > div > div > div.ccp-container > div.pcp-components > div > form > div:nth-child(2) > div.field-content > div.input-field.cap-assignment-info > input[type=number]:nth-child(4)")).sendKeys("50");
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div/form/div[2]/div[2]/div[2]/input[1]")).sendKeys("C:\\base.pdf");
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div/form/div[2]/div[2]/div[2]/input[2]")).sendKeys("C:\\map.pdf");

        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div/form/div[3]/button")).click();


        Assertions.assertTrue(driver.findElement(By.cssSelector("#teacherAssList")).getText().contains("Assignment2"));
    }

    @Test
    public void DeleteAssignmentTest() {
        driver.findElement(By.xpath("//*[@id=\"proCourseList\"]/a[4]/li")).click();
        driver.findElement(By.xpath("//*[@id=\"teacherAssList\"]/div[5]/div/div[2]/div[2]/span[2]")).click();


        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());

        Assertions.assertEquals("Are you sure to delete this assignment?", alert.getText());
        alert.accept();

        try {
            if (driver.findElement(By.xpath("//*[@id=\"teacherAssList\"]/div[3]/a/li/div")).getText().contains("Assignment2")) {
                DeleteAssignmentTest();
            }

        } catch (NoSuchElementException e) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    public void NoDeleteAssignmentTest() {
        driver.findElement(By.xpath("//*[@id=\"proCourseList\"]/a[4]/li")).click();
        driver.findElement(By.xpath("//*[@id=\"teacherAssList\"]/div[5]/div/div[2]/div[2]/span[2]")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());

        Assertions.assertEquals("Are you sure to delete this assignment?", alert.getText());
        alert.dismiss();
        Assertions.assertTrue(driver.findElement(By.xpath("//*[@id=\"teacherAssList\"]/div[3]/a/li/div")).getText().contains("Assignment2"));


    }


}
