import io.github.serpro69.kfaker.Faker
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import org.openqa.selenium.Cookie
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.Keys
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


class TestFeatures {

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
        loginWithFakeUser()
        assertEquals("Bejelentkezés - Top4Fitness.hu", driver.title)
    }

    @Test
    fun testLogout() {
        logout()
        assertEquals("Sikeresen kijelentkeztél.", getToast())
    }

    @Test
    fun testPasswordChange() {
        login()
        closeCookiePopup()
        getDefPageAnd("/user/change-password")

        val currentPasswordLocator = By.name("old_pass")
        val newPasswordLocator = By.name("new_pass1")
        val newPasswordRepeatLocator = By.name("new_pass2")
        val submitLocator = By.cssSelector("input[type='submit'][name='send']")

        waitUntilElementIsVisible(currentPasswordLocator)
        elementFinder(currentPasswordLocator).sendKeys(password)
        waitUntilElementIsVisible(newPasswordLocator)
        elementFinder(newPasswordLocator).sendKeys(password)
        waitUntilElementIsVisible(newPasswordRepeatLocator)
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

        getDefPageAnd("user/login")

        val body = elementFinder(By.tagName("body"))
        (driver as JavascriptExecutor).executeScript(js)

        val selenium = body.findElement(By.className("selenium"))
        assertEquals("Selenium", selenium.text)
    }

    @Test
    fun testHoverElement() {
        getDefPageAnd()
        closeCookiePopup()

        val userIcon = elementFinder(By.xpath("//a[@href='/pg/kapcsolat']"))
        Actions(driver).moveToElement(userIcon).perform()
        Thread.sleep(2000)
        val dropDown = elementFinder(By.cssSelector(".nav-dropdown-list.header-userlist"))
        assertTrue(dropDown.isDisplayed)
    }

    @Test
    fun testCookieManipulation() {
        getDefPageAnd()
        driver.manage().addCookie(Cookie("cbat4fi", "eyJjcmVhdGVkQXQiOjE3NDgxMDA5NzB9"))
        driver.navigate().refresh()

        val consentCookie = driver.manage().cookies.firstOrNull { it.name == "cbat4fi" }
        Thread.sleep(2000)
        assertNotNull(consentCookie)
    }

    @Test
    fun testFillingTextBox() {
        login()
        getDefPageAnd("user/review")

        val reviewTextLocator = By.cssSelector("textarea[name='comment']")
        var reviewText = elementFinder(reviewTextLocator)
        waitUntilElementIsVisible(reviewTextLocator)
        reviewText.sendKeys("filling textarea for selenium testing")
        reviewText = elementFinder(reviewTextLocator)

        assertEquals("filling textarea for selenium testing", reviewText.getAttribute("value"))
    }

    @Test
    fun testStaticPage() {
        getDefPageAnd("pg/rolunk")
        val heading = driver.findElement(By.tagName("h1")).text
        assertEquals("TOP4SPORT történet", heading)
    }

    @Test
    fun testMultipleStaticPages() {
        val pagesToTest = listOf(
            Page(pageUrl + "pg/rolunk", "Rólunk - Top4Fitness.hu", "TOP4SPORT történet"),
            Page(
                pageUrl + "pg/obchodni-podminky",
                "Általános Szerződési Feltételek - Top4Fitness.hu",
                "ÁLTALÁNOS SZERZŐDÉSI FELTÉTELEK"
            ),
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

    @Test
    fun testSendForm() {
        getDefPageAnd()
        val searchBar = elementFinder(By.id("q"))
        searchBar.sendKeys("cipő")
        searchBar.sendKeys(Keys.ENTER)

        val currentUrl = driver.currentUrl
        assertEquals("https://top4fitness.hu/?q=cip%C5%91", currentUrl)
    }

    private fun login() {
        getDefPageAnd("/user/login")
        val loginPage = LoginPage(driver)
        loginPage.login(username, password)
    }

    private fun loginWithFakeUser() {
        getDefPageAnd("/user/login")
        val loginPage = LoginPage(driver)
        val fakeUsername = Faker().internet.email()

        loginPage.login(fakeUsername, password)
    }

    private fun closeCookiePopup() {
        elementFinder(By.tagName("wecoma-lite"))
            .shadowRoot
            .findElement(By.cssSelector("button.primaryAction"))
            .click()
    }

    private fun logout() {
        getDefPageAnd("/user/login")
        val loginPage = LoginPage(driver)

        loginPage.login(username, password)
        loginPage.logout()
    }

    private fun waitUntilElementIsVisible(locator: By) {
        this.wait.until(ExpectedConditions.visibilityOfElementLocated(locator))
    }

    private fun elementFinder(locator: By): WebElement = driver.findElement(locator)

    private fun getDefPageAnd(path: String = "") {
        driver.get(pageUrl + path)
    }

    private fun getToast() =
        elementFinder(By.cssSelector("div.toast")).getAttribute("innerText").trim()
}
