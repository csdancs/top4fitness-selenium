package pages

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

class LoginPage(driver: WebDriver) : BasePage(driver) {

    private val usernameField: WebElement
        get() = driver.findElement(By.id("frm-logInForm-login"))

    private val passwordField: WebElement
        get() = driver.findElement(By.id("passField5"))

    private val loginButton: WebElement
        get() = driver.findElement(By.cssSelector("button[type='submit'][name='log_in']"))

    fun login(username: String, password: String) {
        usernameField.sendKeys(username)
        passwordField.sendKeys(password)
        Thread.sleep(2000)
        loginButton.click()
    }

    fun logout() {
        driver.get("https://top4fitness.hu/user/out")
    }
}