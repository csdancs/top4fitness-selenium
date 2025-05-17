import org.gradle.internal.impldep.org.yaml.snakeyaml.Yaml
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import java.io.File

class LoginTest {

    private val PAGE_URL: String = "https://top4fitness.hu/"
    private val USERNAME: String = readCredentials().username
    private val PASSWORD: String = readCredentials().password

    private lateinit var driver: WebDriver

    @BeforeEach
    fun setup() {
        driver = ChromeDriver()
    }

    @AfterEach
    fun teardown() {
        driver.quit()
    }

    @Test
    fun testLogin() {
        driver.get(PAGE_URL + "user/login")

        val usernameInput: WebElement = driver.findElement(By.name("login"))
        val passwordInput: WebElement = driver.findElement(By.name("pass"))

        usernameInput.sendKeys(USERNAME)
        passwordInput.sendKeys(PASSWORD)

        val loginButton = driver.findElement(By.cssSelector("button[type='submit']"))
        loginButton.click()

        val logoutExists = driver.get("https://top4fitness.hu/user/out")

        assertNotNull(logoutExists)
    }
}

private fun readCredentials(): Credentials {
    val input = File("config.yml").inputStream()
    val yaml = Yaml()
    val data = yaml.loadAs(input, Credentials::class.java)
    return data
}

data class Credentials(val username: String, val password: String)
