package pages

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver

class LoginPage(driver: WebDriver) : BasePage(driver) {

    private val usernameField: By = By.id("frm-logInForm-login")
    private val passwordField: By = By.id("passField5")
    private val loginButton: By = By.cssSelector("button[type='submit'][name='log_in']")
    private val reviewTextLocator = By.cssSelector("textarea[name='comment']")

    val url = "$mainUrl/user/login"
    val reviewUrl = "$mainUrl/user/review"

    fun login(username: String, password: String) {
        waitUntilElementIsVisible(usernameField)
        elementFinder(usernameField).sendKeys(username)
        waitUntilElementIsVisible(passwordField)
        elementFinder(passwordField).sendKeys(password)

        Thread.sleep(2000)

        waitUntilElementIsVisible(loginButton)
        elementFinder(loginButton).click()
    }

    fun getValueAfterFillingTextBox(keysToSend: String): String {
        val reviewTextElement = elementFinder(reviewTextLocator)

        waitUntilElementIsVisible(reviewTextLocator)
        reviewTextElement.sendKeys(keysToSend)
        return elementFinder(reviewTextLocator).getAttribute("value")
    }

    fun logout() {
        driver.get("https://top4fitness.hu/user/out")
    }
}