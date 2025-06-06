package pages

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

abstract class BasePage(protected val driver: WebDriver) {
    protected val wait = WebDriverWait(driver, Duration.ofSeconds(5))
    protected val mainUrl = "https://top4fitness.hu"

    fun elementFinder(by: By): WebElement = driver.findElement(by)

    fun waitUntilElementIsVisible(locator: By) {
        this.wait.until(ExpectedConditions.visibilityOfElementLocated(locator))
    }

    fun getToast() =
        elementFinder(By.cssSelector("div.toast")).getAttribute("innerText").trim()
}