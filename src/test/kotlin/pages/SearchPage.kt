package pages

import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

class SearchPage(driver: WebDriver) : BasePage(driver) {
    private val searchBar: WebElement
        get() = elementFinder(By.id("q"))

    fun search(query: String) {
        searchBar.sendKeys(query)
        searchBar.sendKeys(Keys.ENTER)
    }
}