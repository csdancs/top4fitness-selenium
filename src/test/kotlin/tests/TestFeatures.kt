package tests

import pages.LoginPage
import util.Page
import io.github.serpro69.kfaker.Faker
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.*
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import pages.PasswordChangePage
import pages.SearchPage
import util.Config
import util.DriverFactory
import java.time.Duration
import kotlin.test.assertNotNull

class TestFeatures {

    private val pageUrl: String = Config.pageUrl
    private val username: String = Config.username
    private val password: String = Config.password

    private lateinit var driver: WebDriver
    private lateinit var wait: WebDriverWait

    @BeforeEach
    fun setup() {
        driver = DriverFactory.create()
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

        Assertions.assertEquals("Rendeléseim - Top4Fitness.hu", driver.title)
        Assertions.assertEquals("Rendeléseim", loggedInElement.text)
    }

    @Test
    fun testLoginWithRandomData() {
        loginWithFakeUser()
        Assertions.assertEquals("Bejelentkezés - Top4Fitness.hu", driver.title)
    }

    @Test
    fun testLogout() {
        logout()
        Assertions.assertEquals("Sikeresen kijelentkeztél.", getToast())
    }

    @Test
    fun testPasswordChange() {
        login()
        closeCookiePopup()
        getDefPageAnd("/user/change-password")

        val passwordChangePage = PasswordChangePage(driver, password)
        passwordChangePage.changePassword()

        Thread.sleep(2000)
        Assertions.assertEquals("A belépési jelszó módosítása sikeresen megtörtént. Jelentkezz be megint.", passwordChangePage.getToast())
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
        Assertions.assertEquals("Selenium", selenium.text)
    }

    @Test
    fun testHoverElement() {
        getDefPageAnd()
        closeCookiePopup()

        val userIcon = elementFinder(By.xpath("//a[@href='/pg/kapcsolat']"))
        Actions(driver).moveToElement(userIcon).perform()
        Thread.sleep(2000)
        val dropDown = elementFinder(By.cssSelector(".nav-dropdown-list.header-userlist"))
        Assertions.assertTrue(dropDown.isDisplayed)
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

        Assertions.assertEquals("filling textarea for selenium testing", reviewText.getAttribute("value"))
    }

    @Test
    fun testStaticPage() {
        getDefPageAnd("pg/rolunk")
        val heading = driver.findElement(By.tagName("h1")).text
        Assertions.assertEquals("TOP4SPORT történet", heading)
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
            Assertions.assertEquals(page.title, driver.title)

            // check main heading
            Assertions.assertEquals(page.heading, elementFinder(By.tagName("h1")).text)
        }
    }

    @Test
    fun testSendForm() {
        getDefPageAnd()
        val searchPage = SearchPage(driver)
        searchPage.search("cipő")

        val currentUrl = driver.currentUrl
        Assertions.assertEquals("https://top4fitness.hu/?q=cip%C5%91", currentUrl)
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

    private fun getDefPageAnd(path: String = "") {
        driver.get(pageUrl + path)
    }

    private fun elementFinder(by: By): WebElement = driver.findElement(by)

    private fun waitUntilElementIsVisible(locator: By) {
        this.wait.until(ExpectedConditions.visibilityOfElementLocated(locator))
    }

    private fun getToast() =
        elementFinder(By.cssSelector("div.toast")).getAttribute("innerText").trim()
}