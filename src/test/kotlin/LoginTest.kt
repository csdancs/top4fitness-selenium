import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration
import kotlin.test.assertEquals

class LoginTest {
    private lateinit var driver: WebDriver

    @BeforeEach
    fun setup() {
        driver = ChromeDriver()
    }

    @AfterEach
    fun teardown() {
        driver.quit()
    }

    @Test
    fun testLogin() {
        driver.get("https://testepites.pro/wp-login.php")

        val usernameInput: WebElement = driver.findElement(By.name("log"))
        val passwordInput: WebElement = driver.findElement(By.name("pwd"))

        usernameInput.sendKeys("YOURUSERNAME")
        passwordInput.sendKeys("YOURPASSWORD")

        val loginButton = driver.findElement(By.cssSelector("input[type='submit']"))
        loginButton.click()

        WebDriverWait(driver, Duration.ofSeconds(5))
            .until(ExpectedConditions.urlToBe("https://testepites.pro/"))

        assertEquals("https://testepites.pro/", driver.currentUrl)
    }
}