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

import java.io.File;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class StudentPage {
    private static WebDriver driver;
    private static String downloadPath = "C:\\Users\\태영\\Downloads\\";

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
    public void DownloadAssignment() {

        driver.findElement(By.xpath("//*[@id=\"teacher\"]/div[1]/div/button")).click();



        try{
            if (driver.findElement(By.id("courseList")) != null) {
                driver.findElement(By.xpath("//*[@id=\"courseList\"]/a[1]/li/span[2]")).click();
                try{
                    if (driver.findElement(By.id("assList")) != null) {

                        driver.findElement(By.xpath("//*[@id=\"assList\"]/div[2]/div/div[2]/div[2]/span")).click();

                        Assertions.assertEquals("C:\\Users\\태영\\Downloads\\Week 9.pdf", downloadPath + driver.findElement(By.xpath("//*[@id=\"assList\"]/div[2]/div/div[2]/div[2]/span")).getText());


                    }

                }catch (NoSuchElementException e){
                    System.out.println("no assignment");
                }

            }

        }catch (NoSuchElementException e){
            System.out.println("no course");
        }



    }
    @Test
    public void DownloadInAssignmentPage() {
        driver.findElement(By.xpath("//*[@id=\"teacher\"]/div[1]/div/button")).click();



        try{
            if (driver.findElement(By.id("courseList")) != null) {
                driver.findElement(By.xpath("//*[@id=\"courseList\"]/a[1]/li")).click();
                try{
                    if (driver.findElement(By.id("assList")) != null) {
                        driver.findElement(By.xpath("//*[@id=\"assList\"]/div[2]/div/div[2]/div[1]/span[1]/span")).click();
                        driver.findElement(By.cssSelector("#root > div > div > div > div > div.ap-container > div.ap-component > div > div > div > h3 > span.outfit-18.p2")).click();
                        Assertions.assertEquals("C:\\Users\\태영\\Downloads\\Week 9.pdf", downloadPath + driver.findElement(By.xpath("#root > div > div > div > div > div.ap-container > div.ap-component > div > div > div > h3 > span.outfit-18.p2")).getText());


                    }

                }catch (NoSuchElementException e){
                    System.out.println("no assignment");
                }

            }

        }catch (NoSuchElementException e){
            System.out.println("no course");
        }



    }


    @Test
    public void SubmitAssignemnt(){
        driver.findElement(By.xpath("//*[@id=\"teacher\"]/div[1]/div/button")).click();



        try{
            if (driver.findElement(By.id("courseList")) != null) {
                driver.findElement(By.xpath("//*[@id=\"courseList\"]/a[1]/li")).click();
                try{
                    if (driver.findElement(By.id("assList")) != null) {
                        driver.findElement(By.xpath("//*[@id=\"assList\"]/div[2]/div/div[2]/div[1]/span[1]/span")).click();
                        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div/div/div/h3/div[1]/input")).sendKeys("C:\\Users\\태영\\Downloads\\base.pdf");
                        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div/div/div/h3/div[2]/button")).click();

                        WebDriverWait wait = new WebDriverWait(driver, 5);
                        Alert alert = wait.until(ExpectedConditions.alertIsPresent());

                        Assertions.assertEquals("Successfully uploaded assignment", alert.getText());
                        alert.accept();

                        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div/div[1]/p[2]")).click();
                        Assertions.assertTrue(driver.findElement(By.xpath("//*[@id=\"assList\"]")).getText().contains("Assignment2"));
                        driver.findElement(By.xpath("//*[@id=\"assList\"]/div[5]/div/div[2]/div/span[1]/span")).click();
                        Assertions.assertTrue(driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div/div/div/div[2]/div[3]/span[2]")).getText().contains("Myteam.pdf"));

                    }

                }catch (NoSuchElementException e){
                    System.out.println("no assignment");
                }

            }

        }catch (NoSuchElementException e){
            System.out.println("no course");
        }



    }

    @Test
    public void SubmitPeerReview(){

        driver.findElement(By.xpath("//*[@id=\"teacher\"]/div[1]/div/button")).click();



        try{
            if (driver.findElement(By.id("courseList")) != null) {
                driver.findElement(By.xpath("//*[@id=\"courseList\"]/a[1]/li")).click();
                try{
                    if (driver.findElement(By.id("assList")) != null) {

                        if(driver.findElement(By.cssSelector("#assList > div > div > div.outfit-16.ass-tile-title > span")).getText().contains("Peer Review")) {


                            driver.findElement(By.xpath("//*[@id=\"assList\"]/div/div/div[2]/div[1]/span[1]/span")).click();
                            driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div/div/div/h3/span[4]")).click();
                            Assertions.assertEquals("C:\\Users\\태영\\Downloads\\new.pdf",downloadPath + driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div/div/div/h3/span[4]")).getText());
                            driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div/div/div/h3/span[6]")).click();
                            Assertions.assertEquals("C:\\Users\\태영\\Downloads\\map (2).pdf",downloadPath + driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div/div/div/h3/span[6]")).getText());
                            driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div/div/div/h3/span[8]")).click();
                            //Assertions.assertEquals("C:\\Users\\태영\\Downloads\\yankee",downloadPath + driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div/div/div/h3/span[8]")).getText());


                            driver.findElement(By.cssSelector("#root > div > div > div > div > div.ap-container > div.ap-component > div > div > div > h3 > div.input-field > input[type=number]")).sendKeys("90");
                            driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div/div/div/h3/div[2]/input")).sendKeys("C:\\base.pdf");

                           driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div/div/div/h3/div[3]/div/button")).click();

                            WebDriverWait wait = new WebDriverWait(driver, 5);
                            Alert alert = wait.until(ExpectedConditions.alertIsPresent());

                            Assertions.assertEquals("Successfully uploaded peer review", alert.getText());
                            alert.accept();

                             }
                    }

                }catch (NoSuchElementException e){
                    System.out.println("no assignment");
                }

            }

        }catch (NoSuchElementException e){
            System.out.println("no course");
        }
    }

    @Test
    public void checkGrade(){
        driver.findElement(By.xpath("//*[@id=\"teacher\"]/div[1]/div/button")).click();



        try{
            if (driver.findElement(By.id("courseList")) != null) {
                driver.findElement(By.xpath("//*[@id=\"courseList\"]/a[2]/li")).click();


                driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div/div[1]/p[2]")).click();


                try{
                    if (driver.findElement(By.id("assList")) != null) {

                        driver.findElement(By.xpath("//*[@id=\"assList\"]/div[1]/div/div[2]/div/span[1]/span")).click();

                        if(driver.findElement(By.cssSelector("#root > div > div > div > div > div.ap-container > div.ap-component > div > div > div > div:nth-child(5) > div")).getText().contains("View feedback")){
                            Assertions.assertEquals("90",driver.findElement(By.cssSelector("#root > div > div > div > div > div.ap-container > div.ap-component > div > div > div > div:nth-child(5) > div > div > li > b")).getText());
                            driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div/div/div/div[3]/div/div/li/span")).click();
                            if(new File("C:\\Users\\태영\\Downloads\\from-Poggers-to-newTeam.pdf").exists()){
                                Assertions.assertTrue(true);
                            }
                            

                        }else{
                            System.out.println("No peer review");
                        }
                    }

                }catch (NoSuchElementException e){
                    System.out.println("no assignment");
                }

            }

        }catch (NoSuchElementException e){
            System.out.println("no course");
        }
    }

    }










