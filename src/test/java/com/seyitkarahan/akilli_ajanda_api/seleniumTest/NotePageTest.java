package com.seyitkarahan.akilli_ajanda_api.seleniumTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class NotePageTest {

    private WebDriver driver;

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        driver = new ChromeDriver(options);
    }

    private void login() {
        driver.get("http://localhost:" + port + "/login");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.titleIs("Giriş Yap"));

        WebElement emailInput = driver.findElement(By.name("email"));
        WebElement passwordInput = driver.findElement(By.name("password"));

        emailInput.sendKeys("deneme@gmail.com");
        passwordInput.sendKeys("1234");

        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
        loginButton.click();

        wait.until(ExpectedConditions.titleIs("Dashboard"));
    }

    @Test
    public void testAddNote() {
        login();

        driver.get("http://localhost:" + port + "/notes");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.titleIs("Notes"));

        WebElement titleInput = driver.findElement(By.name("title"));
        String noteTitle = "Test Note " + System.currentTimeMillis();
        titleInput.sendKeys(noteTitle);

        WebElement contentInput = driver.findElement(By.name("content"));
        contentInput.sendKeys("This is a test note content.");

        WebElement colorInput = driver.findElement(By.name("color"));
        colorInput.sendKeys("#FF0000");

        WebElement addButton = driver.findElement(By.className("btn-add"));
        addButton.click();

        wait.until(ExpectedConditions.titleIs("Notes"));

        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//td[text()='" + noteTitle + "']")));

        boolean isNotePresent = !driver.findElements(By.xpath("//td[text()='" + noteTitle + "']")).isEmpty();
        assertTrue(isNotePresent, "The new note should be present in the table.");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
            driver.quit();
        }
    }
}
