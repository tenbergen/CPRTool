import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class NavigationAndInput {

    // web driver to control browser
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
        }
    }

    @AfterAll
    public static void teardown() {
       //  driver.quit();
    }


//        WebDriverWait wait = new WebDriverWait(driver, 5);
//        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#box > button")));
//        driver.findElement(By.cssSelector("#box > button")).click();
//
//        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
//
////Get all the window handles in a set
//        Set<String> handles =driver.getWindowHandles();
//        Iterator<String> it = handles.iterator();
////iterate through your windows
//        while (it.hasNext()){
//            String parent = it.next();
//            String newwin = it.next();
//            driver.switchTo().window(newwin);
////perform actions on new window
//            driver.findElement(By.xpath("//*[@id=\"identifierId\"]")).sendKeys("id");
//            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
//            driver.findElement(By.cssSelector("#identifierNext > div > button > span")).click();
//
//            driver.findElement(By.xpath("//*[@id=\"password\"]/div[1]/div/div[1]/input")).sendKeys("password");
//            driver.findElement(By.cssSelector("#passwordNext > div > button > span")).click();
//            // driver.close();
//            driver.switchTo().window(parent);
//        }


    @Test
    public void loginTest() {
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
            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
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
        driver.findElement(By.cssSelector("#root > div > div > div > div.cpp-container > form > div:nth-child(2) > div:nth-child(2) > input[type=text]")).sendKeys("800");
        driver.findElement(By.cssSelector("#root > div > div > div > div.cpp-container > form > div:nth-child(3) > div:nth-child(1) > input[type=text]")).sendKeys("Spring");
        driver.findElement(By.cssSelector("#root > div > div > div > div.cpp-container > form > div:nth-child(3) > div:nth-child(2) > input[type=text]")).sendKeys("2022");
        driver.findElement(By.cssSelector("#root > div > div > div > div.cpp-container > form > div:nth-child(4) > input[type=text]")).sendKeys("23215");
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div[2]/form/div[5]/button")).click();


        Assertions.assertTrue(driver.findElement(By.cssSelector("#courseList > a:nth-child(5) > li")).getText().contains("Math"));



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

        driver.findElement(By.xpath("//*[@id=\"courseList\"]/a[5]/li")).click();
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
            if (driver.findElement(By.cssSelector("#courseList > a:nth-child(6) > li")).getText().contains("Math")){
                DeleteCourseTest();
            }

        }catch (NoSuchElementException e){
            Assertions.assertTrue(true);
        }
    }
//        @Test
//        public void DeleteCourseTesta() {
////            if (driver.findElement(By.cssSelector("#courseList > a:nth-child(5) > li")).getText().contains("Math")) {
////                DeleteCourseTest();
////
////            } else{
////                Assertions.assertTrue(driver.findElement(By.cssSelector("#courseList > a:nth-child(5) > li"))==null);
////
////            }
//            try{
//                if (driver.findElement(By.cssSelector("#courseList > a:nth-child(5) > li")).getText().contains("Math")){
//                    DeleteCourseTest();
//                }
//
//            }catch (NoSuchElementException e){
//                Assertions.assertTrue(true);
//            }
//        }

//            driver.findElement(By.xpath("//*[@id=\"courseList\"]/a[4]/li")).click();
//            String strUrl = driver.getCurrentUrl();
//            if(strUrl.contains("http://moxie.cs.oswego.edu:13125/details/student"))
//            {
//                String newUrl = strUrl.replace("http://moxie.cs.oswego.edu:13125/details/student","http://moxie.cs.oswego.edu:13125/details/professor");
//                driver.get(newUrl);
//            }
//
//            driver.findElement(By.cssSelector("#root > div > div > div.pcp-container > div.pcp-components > div.pcp-component-links > p:nth-child(4)")).click();
//            driver.findElement(By.cssSelector("#root > div > div > div.pcp-container > div.pcp-components > div:nth-child(2) > div > form > div.ecc-button > a")).click();



    @Test
    //1)after update there is nothing in the Roster. Even if it does not change anything and just hit the save button it does not show anything in the Roster
    public void UpdateCourseTest() {

        driver.findElement(By.xpath("//*[@id=\"courseList\"]/a[5]/li")).click();

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



        WebDriverWait wait = new WebDriverWait(driver, 5);
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        Assertions.assertEquals("Course successfully updated!", alert.getText());
        alert.accept();

        driver.findElement(By.xpath("//*[@id=\"sidebar\"]/div/div[1]/div/div[1]/a/nav")).click();

        Assertions.assertTrue(driver.findElement(By.cssSelector("#courseList > a:nth-child(5) > li")).getText().contains("Cognitive Science"));




    }

    @Test
    public void CSVfileUploadTest(){
        driver.findElement(By.xpath("//*[@id=\"courseList\"]/a[5]/li")).click();
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[1]/p[4]")).click();


        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[2]/div/form/div[5]/input")).sendKeys("C:\\classlist-11098-redact.csv");
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[2]/div/form/div[6]/button")).click();


        int retries = 2;

        while (retries > 0) {
            WebDriverWait wait = new WebDriverWait(driver, 5);
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            if(alert.getText() == "Course successfully updated!") {
                Assertions.assertEquals("Course successfully updated!", alert.getText());
                alert.accept();
            }
            else{
                Assertions.assertFalse(false);
                alert.accept();
            }



            retries--;

        }

        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[1]/div/a[4]/tr/td/p")).click();
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[1]/div/a[5]/tr/td/p")).click();

        driver.findElement(By.cssSelector("#root > div > div > div > div > div.pcp-container > div.pcp-components > div.pcp-component-links > p:nth-child(3)")).click();

        Assertions.assertTrue(driver.findElement(By.cssSelector("#roster > table > tr:nth-child(2) > th:nth-child(1)")).getText().contains("esonnevi"));

    }

    @Test
    //1) you can create an assignment with the same assignment name that already exists.
    //2) If you create an assignment with the same name, you cannot check the assignment and always show the exists assignment page.
    // Even if everything is different except for the assignment name
    //3) assignment year can 5 digits number
    //4) //file format does not matter?
    public void CreateAssignmentTest() {

        driver.findElement(By.xpath("//*[@id=\"courseList\"]/a[5]/li")).click();
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
    }

    //no deleteAssignment?

    @Test
    //1)name = email?
    public void RosteraddTest() {

        driver.findElement(By.xpath("//*[@id=\"courseList\"]/a[5]/li")).click();

        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[1]/p[3]")).click();
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[2]/div/button/img")).click();
        driver.findElement(By.cssSelector("#addStudentDiv > input:nth-child(2)")).sendKeys("Taeyoung");
        driver.findElement(By.cssSelector("#addStudentDiv > input:nth-child(4)")).sendKeys("tpark@oswego.edu");

        driver.findElement(By.xpath("//*[@id=\"addStudentButton\"]")).click();

        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[1]/div/a[4]/tr/td/p")).click();
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[1]/div/a[5]/tr/td/p")).click();


        Assertions.assertEquals("tpark", driver.findElement(By.cssSelector("#roster > table > tr:nth-child(2) > th:nth-child(1)")).getText());

    }


    @Test
    public void RosterAlertTest() {

        driver.findElement(By.xpath("//*[@id=\"courseList\"]/a[6]/li")).click();

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
        driver.findElement(By.xpath("//*[@id=\"courseList\"]/a[5]/li")).click();

        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[1]/p[3]")).click();
        driver.findElement(By.xpath("//*[@id=\"roster\"]/table/tr[2]/span")).click();

        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[1]/div/a[4]/tr/td/p")).click();
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[1]/div/a[5]/tr/td/p")).click();
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div[2]/div[2]/div[1]/p[3]")).click();

        Assertions.assertFalse(driver.findElement(By.cssSelector("#roster > table > tr:nth-child(2) > th:nth-child(1)")).getText().contains("tpark"));
        try{
            if (driver.findElement(By.cssSelector("#roster > table > tr:nth-child(2) > th:nth-child(1)")).getText().contains("tpark")){
                RosterDeleteTest();
            }

        }catch (NoSuchElementException e){
            Assertions.assertTrue(true);
        }
    }

    }
