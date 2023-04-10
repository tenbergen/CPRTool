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

public class CreateDeleteCourseTest {
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
    public void CreateCourseTest() {
        // driver.get("http://moxie.cs.oswego.edu:13125/create/course");

        // WebDriverWait wait = new WebDriverWait(driver, 5);

        //create course
        driver.findElement(By.xpath("//*[@id=\"addButton\"]")).click();
        driver.findElement(By.cssSelector("#root > div > div > div > div.cpp-container > form > div:nth-child(1) > input[type=text]")).sendKeys("Math");
        driver.findElement(By.cssSelector("#root > div > div > div > div.cpp-container > form > div:nth-child(2) > div:nth-child(1) > input[type=text]")).sendKeys("MAT");
        driver.findElement(By.cssSelector("#root > div > div > div > div.cpp-container > form > div:nth-child(2) > div:nth-child(2) > input[type=text]")).sendKeys("801");
        driver.findElement(By.cssSelector("#root > div > div > div > div.cpp-container > form > div:nth-child(3) > div:nth-child(1) > input[type=text]")).sendKeys("Spring");
        driver.findElement(By.cssSelector("#root > div > div > div > div.cpp-container > form > div:nth-child(3) > div:nth-child(2) > input[type=text]")).sendKeys("2022");
        driver.findElement(By.cssSelector("#root > div > div > div > div.cpp-container > form > div:nth-child(4) > input[type=text]")).sendKeys("23216");
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div[2]/form/div[5]/button")).click();


        Assertions.assertTrue(driver.findElement(By.cssSelector("#courseList > a:nth-child(3) > li")).getText().contains("Math"));



    }

    @Test
    public void AlterTest() {

        driver.findElement(By.xpath("//*[@id=\"addButton\"]")).click();
        driver.findElement(By.cssSelector("#root > div > div > div > div.cpp-container > form > div:nth-child(1) > input[type=text]")).sendKeys("");
        driver.findElement(By.cssSelector("#root > div > div > div > div.cpp-container > form > div:nth-child(2) > div:nth-child(1) > input[type=text]")).sendKeys("");
        driver.findElement(By.cssSelector("#root > div > div > div > div.cpp-container > form > div:nth-child(2) > div:nth-child(2) > input[type=text]")).sendKeys("");
        driver.findElement(By.cssSelector("#root > div > div > div > div.cpp-container > form > div:nth-child(3) > div:nth-child(1) > input[type=text]")).sendKeys("");
        driver.findElement(By.cssSelector("#root > div > div > div > div.cpp-container > form > div:nth-child(3) > div:nth-child(2) > input[type=text]")).sendKeys("");
        driver.findElement(By.cssSelector("#root > div > div > div > div.cpp-container > form > div:nth-child(4) > input[type=text]")).sendKeys("");
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div[2]/form/div[5]/button")).click();
        Alert simpleAlert = driver.switchTo().alert();

        // simpleAlert.accept();
        Assertions.assertEquals("Fields can't be empty!", simpleAlert.getText());
        //Thread.sleep(2000);
        // driver.quit();

    }

    @Test
    //1)Sometime it shows "Error updating course. Please try again." alert instead "Course successfully updated!"
    public void DeleteCourseTest() {

        driver.findElement(By.xpath("//*[@id=\"proCourseList\"]/a[3]/li")).click();
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[1]/p[4]")).click();
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[2]/div/form/div[6]/a")).click();

        driver.findElement(By.xpath("//*[@id=\"deleteButtons\"]/button[1]")).click();

        WebDriverWait wait = new WebDriverWait(driver, 5);
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());

        // simpleAlert.accept();

        Assertions.assertEquals("Course successfully updated!", alert.getText());
        alert.accept();
        driver.navigate().to("http://moxie.cs.oswego.edu:13125/");

        try{
            if (driver.findElement(By.cssSelector("#courseList > a:nth-child(3) > li")).getText().contains("Math")){
                DeleteCourseTest();
            }

        }catch (NoSuchElementException e){
            Assertions.assertTrue(true);
        }
    }



    @Test
    public void NOTDeleteCourseTest() {

        driver.findElement(By.xpath("//*[@id=\"courseList\"]/a[1]/li")).click();
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[1]/p[4]")).click();
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[2]/div/form/div[6]/a")).click();

        driver.findElement(By.xpath("//*[@id=\"deleteButtons\"]/button[2]")).click();

        Assertions.assertEquals("http://moxie.cs.oswego.edu:13125/details/professor/CSC378-800-54266-Spring-2023", driver.getCurrentUrl());


    }
}

