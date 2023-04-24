import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.junit.*;

import java.time.Duration;

public class AdminInterfaceTest {

  private WebDriver driver;
  private String baseUrl;
  private JavascriptExecutor js;

  @BeforeAll
  public static void setUpClass() {
    System.setProperty("webdriver.chrome.driver", "src/test/WebDrivers/chromedriver.exe");
  }

  @BeforeEach
  public void setUp() {
    driver = new ChromeDriver();
    baseUrl = "http://localhost:3000/admin";
    driver.get(baseUrl);
  }

  @Test
  public void testSearchFunctionality() {
    // Find the search input field
    WebElement searchInput = driver.findElement(By.cssSelector(".search-bar input[type='text']"));

    // Enter search term
    searchInput.sendKeys("ernie");

    // Verify that the correct number of search results are displayed
    WebElement userRoles = driver.findElement(By.cssSelector(".user-roles"));
    Assertions.assertEquals(8, userRoles.findElements(By.cssSelector(".user-row")).size());

    // Clear search input
    searchInput.clear();

    // Enter another search term
    searchInput.sendKeys("perry");

    // Verify that the correct number of search results are displayed
    Assertions.assertEquals(1, userRoles.findElements(By.cssSelector(".user-row")).size());
  }

  @Test
  public void testDropdownFilter() throws Exception {
    driver.get(baseUrl);
    // wait for page to load
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(10L));
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("admin-container")));

    // select dropdown filter and choose "Teacher" option
    Select dropdown = new Select(driver.findElement(By.name("role")));
    dropdown.selectByVisibleText("Teacher");

    // check if only users with role "Teacher" are displayed
    wait.until(ExpectedConditions
        .visibilityOfElementLocated(By.xpath("//div[@class='user-container']/div[contains(@class, 'Teacher')]")));

//    Assertions.assertFalse(
//        driver.findElements(By.xpath("//div[@class='user-container']/div[not(contains(@class, 'Teacher'))]")).size());

    // change dropdown filter to "Student"
    dropdown.selectByVisibleText("Student");

    // check if only users with role "Student" are displayed
    wait.until(ExpectedConditions
        .visibilityOfElementLocated(By.xpath("//div[@class='user-container']/div[contains(@class, 'Student')]")));
//    assertFalse(
//        driver.findElements(By.xpath("//div[@class='user-container']/div[not(contains(@class, 'Student'))]")).size());
  }

}
