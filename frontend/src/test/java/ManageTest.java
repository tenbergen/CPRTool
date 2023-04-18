import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ManageTest {
    private static WebDriver driver;

    @BeforeAll
    public static void setupClass() {
        System.setProperty("webdriver.chrome.driver", "src/test/WebDrivers/chromedriver.exe");
        driver = new ChromeDriver();
        // driver.get("http://moxie.cs.oswego.edu:13125/");
        // driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);


        //driver.get("http://moxie.cs.oswego.edu:13125/");
        driver.get("http://localhost:3000/");

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
    //1)after update there is nothing in the Roster. Even if it does not change anything and just hit the save button it does not show anything in the Roster
    public void UpdateCourseTest() {

        driver.findElement(By.xpath("//*[@id=\"proCourseList\"]/a[1]/li")).click();

        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[1]/p[4]")).click();
        //clear
        driver.findElement(By.cssSelector("#root > div > div > div > div > div.pcp-container > div.pcp-components > div:nth-child(2) > div > form > div.ecc-input-field > input[type=text]")).sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
        driver.findElement(By.cssSelector("#root > div > div > div > div > div.pcp-container > div.pcp-components > div:nth-child(2) > div > form > div:nth-child(2) > div:nth-child(1) > input[type=text]")).sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
        driver.findElement(By.cssSelector("#root > div > div > div > div > div.pcp-container > div.pcp-components > div:nth-child(2) > div > form > div:nth-child(2) > div:nth-child(2) > input[type=text]")).sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
        driver.findElement(By.cssSelector("#root > div > div > div > div > div.pcp-container > div.pcp-components > div:nth-child(2) > div > form > div:nth-child(3) > div:nth-child(1) > input[type=text]")).sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
        driver.findElement(By.cssSelector("#root > div > div > div > div > div.pcp-container > div.pcp-components > div:nth-child(2) > div > form > div:nth-child(3) > div:nth-child(2) > input[type=text]")).sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
        driver.findElement(By.cssSelector("#root > div > div > div > div > div.pcp-container > div.pcp-components > div:nth-child(2) > div > form > div:nth-child(4) > div:nth-child(1) > input[type=text]")).sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));


        //new information
        driver.findElement(By.cssSelector("#root > div > div > div > div > div.pcp-container > div.pcp-components > div:nth-child(2) > div > form > div.ecc-input-field > input[type=text]")).sendKeys("Cognitive Science");
        driver.findElement(By.cssSelector("#root > div > div > div > div > div.pcp-container > div.pcp-components > div:nth-child(2) > div > form > div:nth-child(2) > div:nth-child(1) > input[type=text]")).sendKeys("COG");
        driver.findElement(By.cssSelector("#root > div > div > div > div > div.pcp-container > div.pcp-components > div:nth-child(2) > div > form > div:nth-child(2) > div:nth-child(2) > input[type=text]")).sendKeys("800");
        driver.findElement(By.cssSelector("#root > div > div > div > div > div.pcp-container > div.pcp-components > div:nth-child(2) > div > form > div:nth-child(3) > div:nth-child(1) > input[type=text]")).sendKeys("Spring");
        driver.findElement(By.cssSelector("#root > div > div > div > div > div.pcp-container > div.pcp-components > div:nth-child(2) > div > form > div:nth-child(3) > div:nth-child(2) > input[type=text]")).sendKeys("2023");
        driver.findElement(By.cssSelector("#root > div > div > div > div > div.pcp-container > div.pcp-components > div:nth-child(2) > div > form > div:nth-child(4) > div:nth-child(1) > input[type=text]")).sendKeys("21235");
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[2]/div/form/div[6]/button")).click();



        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        Assertions.assertEquals("Course successfully updated!", alert.getText());
        alert.accept();

        driver.findElement(By.xpath("//*[@id=\"sidebar\"]/div/div[1]/div/div[1]/a/nav")).click();

        Assertions.assertTrue(driver.findElement(By.cssSelector("#courseList > a:nth-child(1) > li")).getText().contains("Cognitive Science"));




    }

    @Test
    public void CSVfileUploadTest(){
        driver.findElement(By.xpath("//*[@id=\"courseList\"]/a[1]/li")).click();
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[1]/p[4]")).click();


        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[2]/div/form/div[5]/input")).sendKeys("C:\\classlist-11098-redact.csv");
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[2]/div/form/div[6]/button")).click();


        int retries = 2;

        while (retries > 0) {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());

            if(alert.getText() == "Course successfully updated!") {
                Assertions.assertEquals("Course successfully updated!", alert.getText());
                alert.accept();
            }

            else {
                alert.accept();
            }


            retries--;

        }

        //driver.navigate().to("http://moxie.cs.oswego.edu:13125/");
        driver.navigate().to("http://localhost:3000/");

        driver.findElement(By.xpath("//*[@id=\"courseList\"]/a[1]/li")).click();
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[1]/p[3]")).click();


        Assertions.assertTrue(driver.findElement(By.cssSelector("#roster > table > tr:nth-child(2) > th:nth-child(1)")).getText().contains("esonnevi"));

    }

}