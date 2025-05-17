import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver

class LoginTest {

    private val pageUrl: String = "https://top4fitness.hu/"
    private val username: String = "username"
    private val password: String = "password"

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
        driver.get(pageUrl + "user/login")

        val usernameInput: WebElement = driver.findElement(By.name("login"))
        val passwordInput: WebElement = driver.findElement(By.name("pass"))

        usernameInput.sendKeys(username)
        passwordInput.sendKeys(password)

        val loginButton = driver.findElement(By.cssSelector("button[type='submit']"))
        loginButton.click()

        val logoutExists = driver.get("https://top4fitness.hu/user/out")

        assertNotNull(logoutExists)
    }
}
