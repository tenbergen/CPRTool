import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class RosterTest {
    private static WebDriver driver;

    @BeforeAll
    public static void setupClass() {
        System.setProperty("webdriver.chrome.driver", "src/test/WebDrivers/chromedriver.exe");
        driver = new ChromeDriver();
        // driver.get("http://moxie.cs.oswego.edu:13125/");
        // driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);


        driver.get("http://moxie.cs.oswego.edu:13125/");

        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#box > button")));
        driver.findElement(By.cssSelector("#box > button")).click();

        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

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
            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
            driver.findElement(By.cssSelector("#identifierNext > div > button > span")).click();

            driver.findElement(By.xpath("//*[@id=\"password\"]/div[1]/div/div[1]/input")).sendKeys("password");
            driver.findElement(By.cssSelector("#passwordNext > div > button > span")).click();
            // driver.close();
            driver.switchTo().window(parent);
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        }
    }



    @Test
    //1)name = email?
    public void RosteraddTest() {

        driver.findElement(By.xpath("//*[@id=\"courseList\"]/a[1]/li")).click();

        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[1]/p[3]")).click();
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[2]/div/button/img")).click();
        driver.findElement(By.cssSelector("#addStudentDiv > input:nth-child(2)")).sendKeys("Taeyoung");
        driver.findElement(By.cssSelector("#addStudentDiv > input:nth-child(4)")).sendKeys("tpark@oswego.edu");

        driver.findElement(By.xpath("//*[@id=\"addStudentButton\"]")).click();

        driver.navigate().to("http://moxie.cs.oswego.edu:13125/");

        driver.findElement(By.xpath("//*[@id=\"courseList\"]/a[1]/li")).click();
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[1]/p[3]")).click();


        Assertions.assertEquals("tpark", driver.findElement(By.cssSelector("#roster > table > tr:nth-child(2) > th:nth-child(1)")).getText());

    }


    @Test
    public void RosterAlertTest() {

        driver.findElement(By.xpath("//*[@id=\"courseList\"]/a[1]/li")).click();

        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[1]/p[3]")).click();
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[2]/div/button/img")).click();
        driver.findElement(By.cssSelector("#addStudentDiv > input:nth-child(2)")).sendKeys("");
        driver.findElement(By.cssSelector("#addStudentDiv > input:nth-child(4)")).sendKeys("");

        driver.findElement(By.xpath("//*[@id=\"addStudentButton\"]")).click();

        // driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[1]/p[4]")).click();
        // driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[1]/p[3]")).click();

        Alert simpleAlert = driver.switchTo().alert();

        // simpleAlert.accept();
        Assertions.assertEquals("Please enter both name and email for the student!", simpleAlert.getText());

    }

    @Test
    //1)pending?
    public void RosterDeleteTest() {
        driver.findElement(By.xpath("//*[@id=\"courseList\"]/a[1]/li")).click();

        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[1]/p[3]")).click();
        driver.findElement(By.xpath("//*[@id=\"roster\"]/table/tr[2]/span")).click();

        driver.navigate().to("http://moxie.cs.oswego.edu:13125/");
        driver.findElement(By.xpath("//*[@id=\"courseList\"]/a[1]/li")).click();
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[1]/p[3]")).click();
//
//        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[1]/div/a[4]/tr/td/p")).click();
//        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[1]/div/a[5]/tr/td/p")).click();
//        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[1]/p[3]")).click();

       // Assertions.assertFalse(driver.findElement(By.cssSelector("#roster > table > tr:nth-child(2) > th:nth-child(1)")).getText().contains("tpark"));
        try{
            if (driver.findElement(By.cssSelector("#roster > table > tr:nth-child(2) > th:nth-child(1)")).getText().contains("tpark")){
                RosterDeleteTest();
            }

        }catch (NoSuchElementException e){
            Assertions.assertTrue(true);
        }
    }
}
