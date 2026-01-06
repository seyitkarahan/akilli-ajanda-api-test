package com.seyitkarahan.akilli_ajanda_api.seleniumTest;

import com.seyitkarahan.akilli_ajanda_api.dto.request.AuthRequest;
import com.seyitkarahan.akilli_ajanda_api.service.AuthService;
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
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private AuthService authService;

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
    }

    private WebDriverWait getWait() {
        return new WebDriverWait(driver, Duration.ofSeconds(40));
    }

    private void login() {
        try {
            AuthRequest registerRequest = new AuthRequest();
            registerRequest.setName("Test User");
            registerRequest.setEmail("deneme@gmail.com");
            registerRequest.setPassword("1234");
            authService.register(registerRequest);
        } catch (Exception ignored) {}

        driver.get("http://localhost:" + port + "/login");

        WebElement emailInput = getWait().until(
                ExpectedConditions.visibilityOfElementLocated(By.name("email"))
        );
        WebElement passwordInput = getWait().until(
                ExpectedConditions.visibilityOfElementLocated(By.name("password"))
        );

        emailInput.sendKeys("deneme@gmail.com");
        passwordInput.sendKeys("1234");

        WebElement loginButton = getWait().until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']"))
        );
        loginButton.click();

        // Dashboard’a özgü bir element bekle
        getWait().until(ExpectedConditions.urlContains("/dashboard"));
    }

    @Test
    public void testAddNote() {
        login();

        driver.get("http://localhost:" + port + "/notes");

        WebElement titleInput = getWait().until(
                ExpectedConditions.visibilityOfElementLocated(By.name("title"))
        );
        WebElement contentInput = getWait().until(
                ExpectedConditions.visibilityOfElementLocated(By.name("content"))
        );
        WebElement colorInput = getWait().until(
                ExpectedConditions.visibilityOfElementLocated(By.name("color"))
        );

        String noteTitle = "Test Note " + System.currentTimeMillis();

        titleInput.sendKeys(noteTitle);
        contentInput.sendKeys("This is a test note content.");
        colorInput.sendKeys("#FF0000");

        WebElement addButton = getWait().until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']"))
        );
        addButton.click();

        WebElement createdNote = getWait().until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//td[contains(text(),'" + noteTitle + "')]")
                )
        );

        assertTrue(createdNote.isDisplayed());
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

}
