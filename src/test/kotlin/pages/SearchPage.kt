package pages

import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

class SearchPage(driver: WebDriver) : BasePage(driver) {
    private val searchBar: By = By.id("q")
    val url = mainUrl

    fun search(query: String) {
        waitUntilElementIsVisible(searchBar)
        val searchBarElement: WebElement = elementFinder(searchBar)

        searchBarElement.sendKeys(query)
        searchBarElement.sendKeys(Keys.ENTER)
    }
}