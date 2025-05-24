import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.interactions.Actions
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
        driver = ChromeDriver(ChromeOptions().addArguments("start-maximized", "incognito"))
        wait = WebDriverWait(driver, Duration.ofSeconds(5))
    }

    @AfterEach
    fun teardown() {
        driver.quit()
    }

    @Test
    fun testLogin() {
        login()
        val loggedInElement = elementFinder(By.cssSelector("h5"))
        assertEquals("Rendeléseim - Top4Fitness.hu", driver.title)
        assertEquals("Rendeléseim", loggedInElement.text)
    }

    @Test
    fun testLogout() {
        login()
        driver.get("$pageUrl/user/out")
        assertEquals("Sikeresen kijelentkeztél.", getToast())
    }

    @Test
    fun testPasswordChange() {
        login()
        closeCookiePopup()
        driver.get("$pageUrl/user/change-password")
        val currentPasswordLocator = By.name("old_pass")
        val newPasswordLocator = By.name("new_pass1")
        val newPasswordRepeatLocator = By.name("new_pass2")
        val submitLocator = By.cssSelector("input[type='submit'][name='send']")
        waitUntilElementIsVisible(currentPasswordLocator)
        elementFinder(currentPasswordLocator).sendKeys(password)
        elementFinder(newPasswordLocator).sendKeys(password)
        elementFinder(newPasswordRepeatLocator).sendKeys(password)

        val submitElement = elementFinder(submitLocator)
        Actions(driver).moveToElement(submitElement).click().perform()
        Thread.sleep(2000)

        assertEquals("A belépési jelszó módosítása sikeresen megtörtént. Jelentkezz be megint.", getToast())
    }

    @Test
    fun testJavascriptExecutor() {
        val js = """
            const d = document.createElement('div');
            d.innerText = 'Selenium';
            d.classList.add('selenium');
            document.body.appendChild(d);
        """.trimIndent()

        driver.get(pageUrl + "user/login")

        val body = elementFinder(By.tagName("body"))
        (driver as JavascriptExecutor).executeScript(js)

        val selenium = body.findElement(By.className("selenium"))
        assertEquals("Selenium", selenium.text)
    }

    private fun closeCookiePopup() {
        elementFinder(By.tagName("wecoma-lite"))
            .shadowRoot
            .findElement(By.cssSelector("button.primaryAction"))
            .click()
    }

    private fun login() {
        driver.get(pageUrl + "user/login")
        val usernameLocator = By.id("frm-logInForm-login")
        val passwordLocator = By.id("passField5")
        val submitLocator = By.cssSelector("button[type='submit'][name='log_in']")
        waitUntilElementIsVisible(usernameLocator)
        elementFinder(usernameLocator).sendKeys(username)
        elementFinder(passwordLocator).sendKeys(password)
        elementFinder(submitLocator).click()
    }

    private fun waitUntilElementIsVisible(locator: By) {
        this.wait.until(ExpectedConditions.visibilityOfElementLocated(locator))
    }

    private fun invisibilityOfElementLocated(locator: By) {
        this.wait.until(ExpectedConditions.invisibilityOfElementLocated(locator))
    }

    private fun elementFinder(locator: By): WebElement {
        return driver.findElement(locator)
    }

    private fun getToast() =
        elementFinder(By.cssSelector("div.toast")).getAttribute("innerText").trim()

}
