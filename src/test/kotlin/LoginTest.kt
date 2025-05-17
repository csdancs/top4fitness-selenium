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
import java.util.*


class LoginTest {

    val props = Properties()

    init {
        val inputStream = object {}.javaClass.getResourceAsStream("/application.properties")
            ?: throw Exception("Could not find application.properties")
        props.load(inputStream)
    }

    private val pageUrl: String = "https://top4fitness.hu/"
    private val username: String = props.getProperty("username")
    private val password: String = props.getProperty("password")

    private lateinit var driver: WebDriver
    private lateinit var wait: WebDriverWait

    @BeforeEach
    fun setup() {
        driver = ChromeDriver()
        wait = WebDriverWait(driver, Duration.ofSeconds(5))
    }

    @AfterEach
    fun teardown() {
        driver.quit()
    }

    @Test
    fun testLogin() {
        driver.get(pageUrl + "user/login")

        val usernameLocator = By.id("frm-logInForm-login")
        val passwordLocator = By.id("passField5")
        val submitLocator = By.cssSelector("button[type='submit']")
        val logoutLocator = By.xpath("//a[@href='/user/out']")

        waitUntilElementIsVisible(usernameLocator)
        elementFInder(usernameLocator)?.sendKeys(username)
        waitUntilElementIsVisible(passwordLocator)
        elementFInder(passwordLocator)?.sendKeys(password)
        waitUntilElementIsVisible(submitLocator)
        elementFInder(submitLocator)?.click()

        waitUntilElementIsVisible(logoutLocator)
        elementFInder(logoutLocator)?.click()
    }

    private fun waitUntilElementIsVisible(locator: By) {
        this.wait.until(ExpectedConditions.visibilityOfElementLocated(locator))
    }

    private fun elementFInder(locator: By): WebElement? {
        return try {
            driver.findElement(locator)
        } catch (e: NoSuchElementException) {
            null
        }
    }
}
