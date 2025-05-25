package util

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions

object DriverFactory {
    fun create(): WebDriver {
        val options = ChromeOptions()
        options.addArguments("start-maximized", "incognito")
        return ChromeDriver(options)
    }
}