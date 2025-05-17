import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNotNull
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
        driver.get("https://top4fitness.hu/user/login")

        val usernameInput: WebElement = driver.findElement(By.name("login"))
        val passwordInput: WebElement = driver.findElement(By.name("pass"))

        usernameInput.sendKeys("yourusername")
        passwordInput.sendKeys("yourpassword")

        val loginButton = driver.findElement(By.cssSelector("button[type='submit']"))
        loginButton.click()

        val logoutExists = driver.get("https://top4fitness.hu/user/out")

        assertNotNull(logoutExists)
    }
}