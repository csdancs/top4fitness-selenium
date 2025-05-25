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
import org.openqa.selenium.support.ui.WebDriverWait
import pages.PasswordChangePage
import pages.SearchPage
import util.Config
import util.DriverFactory
import java.time.Duration
import kotlin.test.assertNotNull

class TestFeatures {

    private val homePage: String = "https://top4fitness.hu"
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
        val loginPage = LoginPage(driver)
        goToPage(loginPage.url)
        loginPage.login(username, password)

        val loggedInElement = loginPage.elementFinder(By.cssSelector("h5"))

        Assertions.assertEquals("Rendeléseim - Top4Fitness.hu", driver.title)
        Assertions.assertEquals("Rendeléseim", loggedInElement.text)
    }

    @Test
    fun testLoginWithRandomData() {
        loginWithFakeUser(LoginPage(driver))
        Assertions.assertEquals("Bejelentkezés - Top4Fitness.hu", driver.title)
    }

    @Test
    fun testLogout() {
        val loginPage = LoginPage(driver)
        goToPage(loginPage.url)
        loginPage.login(username, password)
        loginPage.logout()

        Assertions.assertEquals("Sikeresen kijelentkeztél.", loginPage.getToast())
    }

    @Test
    fun testPasswordChange() {
        val loginPage = LoginPage(driver)
        goToPage(loginPage.url)
        loginPage.login(username, password)
        closeCookiePopup()
        val passwordChangePage = PasswordChangePage(driver, password)
        goToPage(passwordChangePage.url)

        passwordChangePage.changePassword()

        Thread.sleep(2000)
        Assertions.assertEquals(
            "A belépési jelszó módosítása sikeresen megtörtént. Jelentkezz be megint.",
            passwordChangePage.getToast()
        )
    }

    @Test
    fun testJavascriptExecutor() {
        goToPage(homePage)

        val script = """
            const d = document.createElement('div');
            d.innerText = 'Selenium';
            d.classList.add('selenium');
            document.body.appendChild(d);
        """.trimIndent()

        val body = driver.findElement(By.tagName("body"))
        (driver as JavascriptExecutor).executeScript(script)

        val selenium = body.findElement(By.className("selenium"))
        Assertions.assertEquals("Selenium", selenium.text)
    }

    @Test
    fun testHoverElement() {
        goToPage(homePage)
        closeCookiePopup()

        val userIcon = driver.findElement(By.xpath("//a[@href='/pg/kapcsolat']"))
        Actions(driver).moveToElement(userIcon).perform()
        Thread.sleep(2000)
        val dropDown = driver.findElement(By.cssSelector(".nav-dropdown-list.header-userlist"))
        Assertions.assertTrue(dropDown.isDisplayed)
    }

    @Test
    fun testCookieManipulation() {
        goToPage(homePage)
        driver.manage().addCookie(Cookie("cbat4fi", "eyJjcmVhdGVkQXQiOjE3NDgxMDA5NzB9"))
        driver.navigate().refresh()

        val consentCookie = driver.manage().cookies.firstOrNull { it.name == "cbat4fi" }
        Thread.sleep(2000)
        assertNotNull(consentCookie)
    }

    @Test
    fun testFillingTextBox() {
        val loginPage = LoginPage(driver)
        goToPage(loginPage.url)
        loginPage.login(username, password)
        goToPage(loginPage.reviewUrl)

        val keysToSend = "filling textbox for selenium testing"
        val textBoxValue = loginPage.getValueAfterFillingTextBox(keysToSend)

        Assertions.assertEquals(keysToSend, textBoxValue)
    }

    @Test
    fun testStaticPage() {
        goToPage("${homePage}/pg/rolunk")
        val heading = driver.findElement(By.tagName("h1")).text
        Assertions.assertEquals("TOP4SPORT történet", heading)
    }

    @Test
    fun testMultipleStaticPages() {
        val pagesToTest = listOf(
            Page("$homePage/pg/rolunk", "Rólunk - Top4Fitness.hu", "TOP4SPORT történet"),
            Page(
                "$homePage/pg/obchodni-podminky",
                "Általános Szerződési Feltételek - Top4Fitness.hu",
                "ÁLTALÁNOS SZERZŐDÉSI FELTÉTELEK"
            ),
            Page("$homePage/pg/visszakuldes-menete", "Termék visszaküldése - Top4Fitness.hu", "Termék visszaküldése")
        )

        pagesToTest.forEach { page ->
            goToPage(page.url)

            // check title
            Assertions.assertEquals(page.title, driver.title)

            // check the main heading
            Assertions.assertEquals(page.heading, driver.findElement(By.tagName("h1")).text)
        }
    }

    @Test
    fun testSendForm() {
        driver.get(homePage)
        val searchPage = SearchPage(driver)
        searchPage.search("cipő")

        val currentUrl = driver.currentUrl
        Assertions.assertEquals("https://top4fitness.hu/?q=cip%C5%91", currentUrl)
    }

    private fun loginWithFakeUser(loginPage: LoginPage) {
        goToPage(loginPage.url)
        val fakeUsername = Faker().internet.email()
        loginPage.login(fakeUsername, password)
    }

    private fun closeCookiePopup() {
        driver.findElement(By.tagName("wecoma-lite"))
            .shadowRoot
            .findElement(By.cssSelector("button.primaryAction"))
            .click()
    }

    private fun goToPage(url: String) {
        driver.get(url)
    }
}