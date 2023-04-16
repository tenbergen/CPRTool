import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Iterator;
import java.util.Set;

public class AllSubmissionTest {

    private static WebDriver driver;
    //private static String downloadPath = "C:\\Users\\scarl\\Downloads\\";

    @BeforeAll
    public static void setupClass() {
        System.setProperty("webdriver.chrome.driver", "src/test/WebDrivers/chromedriver.exe");
        driver = new ChromeDriver();
        // driver.get("http://moxie.cs.oswego.edu:13125/");
        // driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);


        //driver.get("https://moxie.cs.oswego.edu:13125/");
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

    //Change Number of teams to review
    @Test
    public void DistributePeerReviews() {
        driver.findElement(By.xpath("//*[@id=\"proCourseList\"]/a[4]/li")).click();
        driver.findElement(By.xpath("//*[@id=\"teacherAssList\"]/div[1]/div/div[2]/div[1]/span[1]/span")).click();
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[1]/p[1]")).click();

        if(driver.findElement(By.cssSelector("#assList > div:nth-child(1) > div > div.outfit-16.ass-tile-title > span")).getText().contains("Assignment")
        && driver.findElement(By.cssSelector("#assList > div:nth-child(2) > div > div.outfit-16.ass-tile-title > span")).getText().contains("Assignment")){
            driver.findElement(By.cssSelector("#root > div > div > div > div > div.scp-container > div.scp-component > div:nth-child(2) > div > div > div.input-field > input[type=number]")).sendKeys("1");
            driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[2]/div/div/div[2]/button")).click();
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());

            Assertions.assertEquals("Assignments successfully distributed for peer review!", alert.getText());
            alert.accept();
        }

    }

    @Test
    public void CheckGradeFeedback(){
        driver.findElement(By.xpath("//*[@id=\"proCourseList\"]/a[5]/li")).click();
        driver.findElement(By.xpath("//*[@id=\"teacherAssList\"]/div[1]/div/div[2]/div[1]/span[1]/span")).click();
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[1]/p[1]")).click();

        driver.findElement(By.xpath("//*[@id=\"assList\"]/div[1]/div/div[2]/div/span[1]/span")).click();

        if(driver.findElement(By.cssSelector("#root > div > div > div > div > div.scp-container > div.scp-component > div > div > div > div:nth-child(5) > div")).getText().contains("View feedback")) {
            Assertions.assertEquals("90", driver.findElement(By.cssSelector("#root > div > div > div > div > div.scp-container > div.scp-component > div > div > div > div:nth-child(5) > div > div > li > div > span:nth-child(1)")).getText());
            driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div/div/div/div[3]/div/div/li/div/span[2]")).click();
            Assertions.assertEquals("C:\\Users\\태영\\Downloads\\yankee.pdf", driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div/div/div/div[2]/div[3]/span[2]")).getText() );
        }else
        {
            System.out.println("No peer review");
        }

    }
}

