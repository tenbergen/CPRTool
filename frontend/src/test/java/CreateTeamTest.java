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
import java.util.concurrent.TimeUnit;

public class CreateTeamTest {
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


    //stud
    @Test
    public void CreateTeam() {
        driver.findElement(By.xpath("//*[@id=\"teacher\"]/div[1]/div/button")).click();
        driver.findElement(By.xpath("//*[@id=\"courseList\"]/a[2]/li")).click();
        driver.findElement(By.xpath("//*[@id=\"createTeamButton\"]/button")).click();

        int retries = 2;

        while (retries > 0) {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());

            if (alert.getText().contains("Enter team name: ")) {
                alert.sendKeys("MyTeam");
                alert.accept();
            } else {
                alert.dismiss();
            }


            retries--;

        }
        driver.findElement(By.cssSelector("#root > div > div > div > div > div.scp-container > div.scp-component > div > div.scp-component-links > p:nth-child(3)")).click();
        Assertions.assertEquals("MyTeam",driver.findElement(By.cssSelector("#root > div > div > div > div > div.scp-container > div.scp-component > div > div:nth-child(2) > div > div > div.team-name-container > div > p")).getText());
        Assertions.assertEquals("Tae Young Park",driver.findElement(By.cssSelector("#root > div > div > div > div > div.scp-container > div.scp-component > div > div:nth-child(2) > div > div > div.members-name-container > div > p")).getText());
    }

    //stud
    @Test
    public void JointheTeam() {
        driver.findElement(By.xpath("//*[@id=\"teacher\"]/div[1]/div/button")).click();
        driver.findElement(By.xpath("//*[@id=\"courseList\"]/a[2]/li")).click();
        driver.findElement(By.xpath("//*[@id=\"teamListItem\"]")).click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5L));
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();

        driver.findElement(By.cssSelector("#root > div > div > div > div > div.scp-container > div.scp-component > div > div.scp-component-links > p:nth-child(3)")).click();
        Assertions.assertEquals("Rangers",driver.findElement(By.cssSelector("#root > div > div > div > div > div.scp-container > div.scp-component > div > div:nth-child(2) > div > div > div.team-name-container > div > p")).getText());
        Assertions.assertEquals("Tae Young Park",driver.findElement(By.cssSelector("#root > div > div > div > div > div.scp-container > div.scp-component > div > div:nth-child(2) > div > div > div.members-name-container > div > p")).getText());


//        driver.navigate().to("http://moxie.cs.oswego.edu:13125/");
//        driver.findElement(By.xpath("//*[@id=\"student\"]/div[1]/div[2]/button")).click();
//        driver.findElement(By.xpath("//*[@id=\"proCourseList\"]/a[1]/li")).click();
//        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[1]/p[4]")).click();
//        driver.findElement(By.cssSelector("#root > div > div > div > div > div.pcp-container > div.pcp-components > div:nth-child(2) > div > div > div > div > div.accordionTitle > div > text")).click();
//        Assertions.assertTrue(driver.findElement(By.cssSelector("#root > div > div > div > div > div.pcp-container > div.pcp-components > div:nth-child(2) > div > div > div > div > div.accordionContent")).getText().contains("tpark"));

    }

    //prof
    @Test
    public void DeleteTeamMember() {
        driver.findElement(By.xpath("//*[@id=\"proCourseList\"]/a[1]/li")).click();
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[1]/p[4]")).click();
        driver.findElement(By.cssSelector("#root > div > div > div > div > div.pcp-container > div.pcp-components > div:nth-child(2) > div > div > div > div > div.accordionTitle > div > text")).click();
        driver.findElement(By.cssSelector("#root > div > div > div > div > div.pcp-container > div.pcp-components > div:nth-child(2) > div > div > div > div > div.accordionContent > div:nth-child(3) > span")).click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[1]/p[4]")).click();
        driver.findElement(By.cssSelector("#root > div > div > div > div > div.pcp-container > div.pcp-components > div:nth-child(2) > div > div > div > div > div.accordionTitle > div > text")).click();


        if (driver.findElement(By.cssSelector("#root > div > div > div > div > div.pcp-container > div.pcp-components > div:nth-child(2) > div > div > div > div > div.accordionContent")).getText().contains("tpark")) {
            Assertions.assertTrue(false);
        } else {
            Assertions.assertTrue(true);
        }
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[1]/p[3]")).click();
        if(driver.findElement(By.cssSelector("#roster > table > tr:nth-child(2) > th:nth-child(3)")).getText().contains("")){
            Assertions.assertTrue(true);
        }else {
            Assertions.assertTrue(false);
        }


    }
    //prof
    @Test
    public void DeleteTeam() {
        driver.findElement(By.xpath("//*[@id=\"proCourseList\"]/a[1]/li")).click();
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[1]/p[4]")).click();
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[2]/div/div/div/div/div/div/span")).click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());

        if(alert.getText().contains("Successfully removed team.")){
            alert.accept();
        }
        if (driver.findElement(By.cssSelector("#root > div > div > div > div > div.pcp-container > div.pcp-components > div:nth-child(2) > div > div")).getText().contains("MyTeam")){
            Assertions.assertTrue(false);
        }else {
            Assertions.assertTrue(true);
        }
    }



        //prof
    @Test
     public void AddTeamMember () {

        driver.findElement(By.xpath("//*[@id=\"proCourseList\"]/a[2]/li")).click();
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[1]/p[4]")).click();
        driver.findElement(By.cssSelector("#root > div > div > div > div > div.pcp-container > div.pcp-components > div:nth-child(2) > div > div > div > div > div.accordionTitle > div > text")).click();
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[2]/div/div/div/div/div[2]/input")).click();
        driver.findElement(By.cssSelector("#root > div > div > div > div > div.pcp-container > div.pcp-components > div:nth-child(2) > div > div > div > div > div.accordionContent > div.teamMember > input")).sendKeys("tpark");
        driver.findElement(By.cssSelector("#root > div > div > div > div > div.pcp-container > div.pcp-components > div:nth-child(2) > div > div > div > div > div.accordionContent > div.teamMember > a")).click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());

        if(alert.getText().contains("Successfully added student.")) {
            alert.accept();


            driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[1]/p[4]")).click();
            driver.findElement(By.cssSelector("#root > div > div > div > div > div.pcp-container > div.pcp-components > div:nth-child(2) > div > div > div > div > div.accordionTitle > div > text")).click();
            if (driver.findElement(By.cssSelector("#root > div > div > div > div > div.pcp-container > div.pcp-components > div:nth-child(2) > div > div > div > div > div.accordionContent")).getText().contains("tpark")) {
                Assertions.assertTrue(true);
            } else {
                Assertions.assertTrue(false);
            }
            driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[1]/p[3]")).click();
            if (driver.findElement(By.cssSelector("#roster > table > tr:nth-child(2) > th:nth-child(3)")).getText().contains("rangers")) {

                Assertions.assertTrue(true);
            } else {
                Assertions.assertTrue(false);
            }
        }
        else if(alert.getText().contains("Error adding student.")) {
            alert.dismiss();
            Assertions.assertFalse(false);
        }
    }


    }



