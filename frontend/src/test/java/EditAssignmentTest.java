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

public class EditAssignmentTest {
    private static WebDriver driver;
    private static String downloadPath = "C:\\Users\\태영\\Downloads\\";

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
    public void EditAssignmentTest() {
        driver.findElement(By.xpath("//*[@id=\"proCourseList\"]/a[4]/li")).click();
        driver.findElement(By.xpath("//*[@id=\"teacherAssList\"]/div[5]/div/div[2]/div[1]/span[1]/span")).click();
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[1]/p[3]")).click();


        driver.findElement(By.cssSelector("#root > div > div > div > div > div.scp-container > div.scp-component > div:nth-child(2) > div > form > div.eac-input-field > input[type=text]")).sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
        driver.findElement(By.cssSelector("#root > div > div > div > div > div.scp-container > div.scp-component > div:nth-child(2) > div > form > div.eac-input-field > input[type=text]")).sendKeys("NewAssignment");
        driver.findElement(By.cssSelector("#root > div > div > div > div > div.scp-container > div.scp-component > div:nth-child(2) > div > form > div:nth-child(2) > textarea")).sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
        driver.findElement(By.cssSelector("#root > div > div > div > div > div.scp-container > div.scp-component > div:nth-child(2) > div > form > div:nth-child(2) > textarea")).sendKeys("Bye");
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[2]/div/form/div[4]/input")).sendKeys("C:\\new.pdf");
        driver.findElement(By.cssSelector(" #root > div > div > div > div > div.scp-container > div.scp-component > div:nth-child(2) > div > form > div:nth-child(5) > input[type=date]:nth-child(2)")).sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
        driver.findElement(By.cssSelector(" #root > div > div > div > div > div.scp-container > div.scp-component > div:nth-child(2) > div > form > div:nth-child(5) > input[type=date]:nth-child(2)")).sendKeys("04042022");
        driver.findElement(By.cssSelector("#root > div > div > div > div > div.scp-container > div.scp-component > div:nth-child(2) > div > form > div:nth-child(5) > input[type=number]:nth-child(4)")).sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
        driver.findElement(By.cssSelector("#root > div > div > div > div > div.scp-container > div.scp-component > div:nth-child(2) > div > form > div:nth-child(5) > input[type=number]:nth-child(4)")).sendKeys("40");
        driver.findElement(By.cssSelector("#root > div > div > div > div > div.scp-container > div.scp-component > div:nth-child(2) > div > form > div:nth-child(6) > textarea")).sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
        driver.findElement(By.cssSelector("#root > div > div > div > div > div.scp-container > div.scp-component > div:nth-child(2) > div > form > div:nth-child(6) > textarea")).sendKeys("do you're best");

        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[2]/div/form/div[7]/div[1]/div[2]/input")).sendKeys("C:\\base.pdf");
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[2]/div/form/div[7]/div[2]/div[2]/input")).sendKeys("C:\\map.pdf");
        driver.findElement(By.cssSelector("#root > div > div > div > div > div.scp-container > div.scp-component > div:nth-child(2) > div > form > div:nth-child(8) > input[type=date]:nth-child(2)")).sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
        driver.findElement(By.cssSelector("#root > div > div > div > div > div.scp-container > div.scp-component > div:nth-child(2) > div > form > div:nth-child(8) > input[type=date]:nth-child(2)")).sendKeys("04062022");
        driver.findElement(By.cssSelector("#root > div > div > div > div > div.scp-container > div.scp-component > div:nth-child(2) > div > form > div:nth-child(8) > input[type=number]:nth-child(4)")).sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
        driver.findElement(By.cssSelector("#root > div > div > div > div > div.scp-container > div.scp-component > div:nth-child(2) > div > form > div:nth-child(8) > input[type=number]:nth-child(4)")).sendKeys("60");

        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[2]/div/form/div[9]/button")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());

        Assertions.assertEquals("Successfully updated assignment!", alert.getText());
        alert.accept();

        //driver.navigate().to("http://moxie.cs.oswego.edu:13125/");
        driver.navigate().to("http://localhost:3000/");

        driver.findElement(By.xpath("//*[@id=\"proCourseList\"]/a[4]/li")).click();
        Assertions.assertTrue(driver.findElement(By.cssSelector("#teacherAssList > div:nth-child(1) > a > li > div")).getText().contains("NewAssignment"));
    }

    @Test
    public void DownloadassignmentTest() {
        driver.findElement(By.xpath("//*[@id=\"proCourseList\"]/a[4]/li")).click();
        driver.findElement(By.xpath("//*[@id=\"teacherAssList\"]/div[5]/div/div[2]/div[1]/span[1]/span")).click();
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[1]/p[3]")).click();
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[2]/div/form/div[3]/span[1]")).click();
        Assertions.assertEquals(downloadPath + driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[2]/div/form/div[3]/span[1]")).getText(), "C:\\Users\\태영\\Downloads\\new.pdf");
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[2]/div/form/div[7]/div[1]/div[1]/span[1]")).click();
        Assertions.assertEquals(downloadPath + driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[2]/div/form/div[7]/div[1]/div[1]/span[1]")).getText(), "C:\\Users\\태영\\Downloads\\base.pdf");
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[2]/div/form/div[7]/div[2]/div[1]/span[1]")).click();
        Assertions.assertEquals(downloadPath + driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[2]/div/form/div[7]/div[2]/div[1]/span[1]")).getText(), "C:\\Users\\태영\\Downloads\\map.pdf");

    }
}

//    @Test
//    public void DeleteFiletTest() {
//        driver.findElement(By.xpath("//*[@id=\"proCourseList\"]/a[4]/li")).click();
//        driver.findElement(By.xpath("//*[@id=\"teacherAssList\"]/div[5]/div/div[2]/div[1]/span[1]/span")).click();
//        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[1]/p[3]")).click();
//        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[2]/div/form/div[3]/span[2]")).click();
//        Assertions.assertEquals("",driver.findElement(By.cssSelector("#root > div > div > div > div > div.scp-container > div.scp-component > div:nth-child(2) > div > form > div:nth-child(3) > span.eac-file-name")).getText());
//        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[2]/div/form/div[7]/div[1]/div[1]/span[2]")).click();
//        Assertions.assertEquals("",driver.findElement(By.cssSelector("#root > div > div > div > div > div.scp-container > div.scp-component > div:nth-child(2) > div > form > div.eac-assignment-files-multiple > div:nth-child(1) > div:nth-child(1) > span.eac-file-name")).getText());
//        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[2]/div/form/div[7]/div[2]/div[1]/span[2]")).click();
//        Assertions.assertEquals("",driver.findElement(By.cssSelector("#root > div > div > div > div > div.scp-container > div.scp-component > div:nth-child(2) > div > form > div.eac-assignment-files-multiple > div:nth-child(2) > div:nth-child(1) > span:nth-child(1)")).getText());
//
//    }
//}