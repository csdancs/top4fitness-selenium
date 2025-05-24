import io.github.serpro69.kfaker.Faker
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import org.openqa.selenium.Cookie
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
import kotlin.test.assertNotNull


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
    fun testLoginWithRandomData() {
        gatDefPageAnd("user/login")
        val usernameLocator = By.id("frm-logInForm-login")
        val passwordLocator = By.id("passField5")
        val submitLocator = By.cssSelector("button[type='submit'][name='log_in']")
        val fakeUsername = Faker().internet.email()

        waitUntilElementIsVisible(usernameLocator)
        elementFinder(usernameLocator).sendKeys(fakeUsername)
        elementFinder(passwordLocator).sendKeys(password)
        elementFinder(submitLocator).click()

        assertEquals("Bejelentkezés - Top4Fitness.hu", driver.title)
    }

    @Test
    fun testLogout() {
        login()
        gatDefPageAnd("/user/out")
        assertEquals("Sikeresen kijelentkeztél.", getToast())
    }

    @Test
    fun testPasswordChange() {
        login()
        closeCookiePopup()
        gatDefPageAnd("/user/change-password")
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

        gatDefPageAnd("user/login")

        val body = elementFinder(By.tagName("body"))
        (driver as JavascriptExecutor).executeScript(js)

        val selenium = body.findElement(By.className("selenium"))
        assertEquals("Selenium", selenium.text)
    }

    @Test
    fun testHoverElement() {
        gatDefPageAnd()
        closeCookiePopup()

        val userIcon = elementFinder(By.cssSelector("a[href='/pg/kapcsolat']"))
        Actions(driver).moveToElement(userIcon).perform()
        Thread.sleep(2000)
        val dropDown = elementFinder(By.cssSelector(".nav-dropdown-list.header-userlist"))
        assertTrue(dropDown.isDisplayed)
    }

    @Test
    fun testCookieManipulation() {
        gatDefPageAnd()
        driver.manage().addCookie(Cookie("cbat4fi", "eyJjcmVhdGVkQXQiOjE3NDgxMDA5NzB9"))
        driver.navigate().refresh()

        val consentCookie = driver.manage().cookies.firstOrNull { it.name == "cbat4fi" }
        Thread.sleep(2000)
        assertNotNull(consentCookie)
    }

    @Test
    fun testFillingTextBox() {
        login()
        gatDefPageAnd("user/review")

        val reviewTextLocator = By.cssSelector("textarea[name='comment']")
        var reviewText = elementFinder(reviewTextLocator)
        waitUntilElementIsVisible(reviewTextLocator)
        reviewText.sendKeys("filling textarea for selenium testing")
        reviewText = elementFinder(reviewTextLocator)

        assertEquals("filling textarea for selenium testing", reviewText.getAttribute("value"))
    }

    @Test
    fun testStaticPage() {
        gatDefPageAnd("pg/rolunk")
        val heading = driver.findElement(By.tagName("h1")).text
        assertEquals("TOP4SPORT történet", heading)
    }

    @Test
    fun testMultipleStaticPages() {
        val pagesToTest= listOf(
            Page(pageUrl + "pg/rolunk", "Rólunk - Top4Fitness.hu", "TOP4SPORT történet"),
            Page(pageUrl + "pg/obchodni-podminky", "Általános Szerződési Feltételek - Top4Fitness.hu", "ÁLTALÁNOS SZERZŐDÉSI FELTÉTELEK"),
            Page(pageUrl + "pg/visszakuldes-menete", "Termék visszaküldése - Top4Fitness.hu", "Termék visszaküldése")
        )

        pagesToTest.forEach { page ->
            driver.get(page.url)

            // check title
            assertEquals(page.title, driver.title)

            // check main heading
            assertEquals(page.heading, elementFinder(By.tagName("h1")).text)
        }
    }

    private fun closeCookiePopup() {
        elementFinder(By.tagName("wecoma-lite"))
            .shadowRoot
            .findElement(By.cssSelector("button.primaryAction"))
            .click()
    }

    private fun login() {
        gatDefPageAnd("user/login")
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

    private fun elementFinder(locator: By): WebElement {
        return driver.findElement(locator)
    }

    private fun gatDefPageAnd(param: String = "") {
        driver.get(pageUrl + param)
    }

    private fun getToast() =
        elementFinder(By.cssSelector("div.toast")).getAttribute("innerText").trim()
}

data class Page(
    val url: String,
    val title: String,
    val heading: String
)
