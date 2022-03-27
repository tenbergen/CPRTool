import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
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
    public static void setupClass(){
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
        Set<String> handles =driver.getWindowHandles();
        Iterator<String> it = handles.iterator();
//iterate through your windows
        while (it.hasNext()){
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





    @Test
    public void loginTest(){
        driver.get("http://moxie.cs.oswego.edu:13125/");

        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#box > button")));
        driver.findElement(By.cssSelector("#box > button")).click();

        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

//Get all the window handles in a set
        Set<String> handles =driver.getWindowHandles();
        Iterator<String> it = handles.iterator();
//iterate through your windows
        while (it.hasNext()){
            String parent = it.next();
            String newwin = it.next();
            driver.switchTo().window(newwin);
//perform actions on new window
            driver.findElement(By.xpath("//*[@id=\"identifierId\"]")).sendKeys("tpark");
            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
            driver.findElement(By.cssSelector("#identifierNext > div > button > span")).click();

            driver.findElement(By.xpath("//*[@id=\"password\"]/div[1]/div/div[1]/input")).sendKeys("zZ!9805892");
            driver.findElement(By.cssSelector("#passwordNext > div > button > span")).click();
            // driver.close();
            driver.switchTo().window(parent);
        }
    }
    @Test
    public void CreateCourseTest(){



        //create course
        driver.findElement(By.xpath("//*[@id=\"addButton\"]")).click();
        driver.findElement(By.cssSelector("#root > div > div.cpp-container > form > div.ccp-input-field > input[type=text]")).sendKeys("Math");
        driver.findElement(By.cssSelector("#root > div > div.cpp-container > form > div:nth-child(2) > div:nth-child(1) > input[type=text]")).sendKeys("MAT");
        driver.findElement(By.cssSelector("#root > div > div.cpp-container > form > div:nth-child(2) > div:nth-child(2) > input[type=text]")).sendKeys("800");
        driver.findElement(By.cssSelector("#root > div > div.cpp-container > form > div:nth-child(2) > div:nth-child(3) > input[type=text]")).sendKeys("24351");
        driver.findElement(By.cssSelector("#root > div > div.cpp-container > form > div:nth-child(3) > div:nth-child(1) > input[type=text]")).sendKeys("Spring");
        driver.findElement(By.cssSelector("#root > div > div.cpp-container > form > div:nth-child(3) > div:nth-child(2) > input[type=text]")).sendKeys("2022");
        driver.findElement(By.cssSelector("#root > div > div.cpp-container > form > div.ccp-button > button")).click();



    }

    @Test
    public void AlterTest(){

        //checking Alter when element are empty.
        driver.findElement(By.xpath("//*[@id=\"addButton\"]")).click();
        driver.findElement(By.cssSelector("#root > div > div.cpp-container > form > div.ccp-input-field > input[type=text]")).sendKeys("Math");
        driver.findElement(By.cssSelector("#root > div > div.cpp-container > form > div:nth-child(2) > div:nth-child(1) > input[type=text]")).sendKeys("");
        driver.findElement(By.cssSelector("#root > div > div.cpp-container > form > div:nth-child(2) > div:nth-child(2) > input[type=text]")).sendKeys("");
        driver.findElement(By.cssSelector("#root > div > div.cpp-container > form > div:nth-child(2) > div:nth-child(3) > input[type=text]")).sendKeys("");
        driver.findElement(By.cssSelector("#root > div > div.cpp-container > form > div:nth-child(3) > div:nth-child(1) > input[type=text]")).sendKeys("");
        driver.findElement(By.cssSelector("#root > div > div.cpp-container > form > div:nth-child(3) > div:nth-child(2) > input[type=text]")).sendKeys("");
        Alert simpleAlert = driver.switchTo().alert();

        // simpleAlert.accept();
        Assertions.assertEquals("Fields can't be empty!",simpleAlert.getText());
        //Thread.sleep(2000);
        // driver.quit();

    }

    @Test
    public void ThirdTest(){

        driver.get("http://129.3.168.61:13129/");
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        driver.findElement(By.cssSelector("#box > div > button")).click();

        //create and then delete
        driver.findElement(By.xpath("//*[@id=\"addButton\"]")).click();
        driver.findElement(By.cssSelector("#root > div > div.container > form > div:nth-child(1) > input[type=text]")).sendKeys("Math");
        driver.findElement(By.cssSelector("#root > div > div.container > form > div:nth-child(2) > input[type=number]")).sendKeys("101");
        driver.findElement(By.cssSelector("#root > div > div.container > form > div:nth-child(3) > input[type=text]")).sendKeys("Fall");
        driver.findElement(By.cssSelector("#root > div > div.container > form > div:nth-child(4) > input[type=text]")).sendKeys("MAT");
        driver.findElement(By.cssSelector("#root > div > div.container > form > div.button > button")).click();
        driver.findElement(By.xpath("//*[@id=\"courseList\"]/ul/a[6]/li")).click();
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/form/div[4]/a")).click();




    }

    @Test
    public void FourthTest(){

        driver.get("http://129.3.168.61:13129/");
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        driver.findElement(By.cssSelector("#box > div > button")).click();

        //editing course & upload file
        //need more function for checking non-CSV file.
        driver.findElement(By.cssSelector("#courseList > ul > a:nth-child(5) > li")).click();
        driver.findElement(By.cssSelector("#root > div > div.container > form > div.course-name > input[type=text]")).sendKeys("Music History");
        driver.findElement(By.xpath("//input[@type='file']")).sendKeys("C:\\Users\\태영\\Desktop\\Hello.txt");
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/form/div[4]/button")).click();

        //need any Alert for checking assert

    }

    @Test
    public void FifthTest(){

        driver.get("http://129.3.168.61:13129/");
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        driver.findElement(By.cssSelector("#box > div > button")).click();


//if everything works and peer size is "int" then pass.
        driver.findElement(By.cssSelector("#courseList > ul > a:nth-child(5) > li")).click();
        driver.findElement(By.cssSelector("#root > div > div.container > form > div.course-name > input[type=text]")).sendKeys("English");
        driver.findElement(By.xpath("//input[@type='file']")).sendKeys("C:\\Users\\태영\\Desktop\\Hello.txt");
        driver.findElement(By.cssSelector("#root > div > div.container > form > div.team-size > input[type=number]")).sendKeys("3");
        // driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/form/div[4]/button")).click();

        //if everything works and peer size is int then pass.
        //Assert.assertEquals(int i,3);


    }


    /*
    @Test
    public void fourthTest() throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "resources/windows/chromedriver.exe");


        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        driver.get("http://localhost:13129/");
        driver.findElement(By.cssSelector("#box > div > button")).click();

        //create and then delete
        driver.findElement(By.xpath("//*[@id=\"addButton\"]")).click();
        driver.findElement(By.cssSelector("#root > div > div.container > form > div:nth-child(1) > input[type=text]")).sendKeys("Math");
        driver.findElement(By.cssSelector("#root > div > div.container > form > div:nth-child(2) > input[type=number]")).sendKeys("101");
        driver.findElement(By.cssSelector("#root > div > div.container > form > div:nth-child(3) > input[type=text]")).sendKeys("Fall");
        driver.findElement(By.cssSelector("#root > div > div.container > form > div:nth-child(4) > input[type=text]")).sendKeys("MAT");
        driver.findElement(By.cssSelector("#root > div > div.container > form > div.button > button")).click();

        driver.findElement(By.xpath("//*[@id=\"addButton\"]")).click();
        driver.findElement(By.cssSelector("#root > div > div.container > form > div:nth-child(1) > input[type=text]")).sendKeys("Math");
        driver.findElement(By.cssSelector("#root > div > div.container > form > div:nth-child(2) > input[type=number]")).sendKeys("101");
        driver.findElement(By.cssSelector("#root > div > div.container > form > div:nth-child(3) > input[type=text]")).sendKeys("Fall");
        driver.findElement(By.cssSelector("#root > div > div.container > form > div:nth-child(4) > input[type=text]")).sendKeys("MAT");
        driver.findElement(By.cssSelector("#root > div > div.container > form > div.button > button")).click();



}*/

    @AfterAll
    public static void teardown(){
        // driver.quit();
    }
}
