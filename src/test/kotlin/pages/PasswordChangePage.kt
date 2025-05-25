package pages

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.interactions.Actions

class PasswordChangePage(
    driver: WebDriver,
    private val password: String
) : BasePage(driver) {
    private val currentPasswordLocator: By = By.name("old_pass")
    private val newPasswordLocator: By = By.name("new_pass1")
    private val newPasswordRepeatLocator: By = By.name("new_pass2")
    private val submitLocator: By = By.cssSelector("input[type='submit'][name='send']")
    val url = "$mainUrl/user/change-password"


    fun changePassword() {
        waitUntilElementIsVisible(currentPasswordLocator)
        elementFinder(currentPasswordLocator).sendKeys(password)
        waitUntilElementIsVisible(newPasswordLocator)
        elementFinder(newPasswordLocator).sendKeys(password)
        waitUntilElementIsVisible(newPasswordRepeatLocator)
        elementFinder(newPasswordRepeatLocator).sendKeys(password)

        val submitElement = elementFinder(submitLocator)
        Actions(driver).moveToElement(submitElement).click().perform()
    }
}