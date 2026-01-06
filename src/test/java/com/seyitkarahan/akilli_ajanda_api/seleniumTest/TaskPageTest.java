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
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class TaskPageTest {

    private WebDriver driver;

    @LocalServerPort
    private int port;

    @Autowired
    private AuthService authService;

    @BeforeEach
    void setUp() {
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
            AuthRequest req = new AuthRequest();
            req.setName("Test User");
            req.setEmail("deneme@gmail.com");
            req.setPassword("1234");
            authService.register(req);
        } catch (Exception ignored) {}

        driver.get("http://localhost:" + port + "/login");

        WebElement email = getWait().until(
                ExpectedConditions.visibilityOfElementLocated(By.name("email"))
        );
        WebElement password = getWait().until(
                ExpectedConditions.visibilityOfElementLocated(By.name("password"))
        );

        email.sendKeys("deneme@gmail.com");
        password.sendKeys("1234");

        getWait().until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']"))
        ).click();

        getWait().until(ExpectedConditions.urlContains("/dashboard"));
    }

    @Test
    void testAddTask() {
        login();

        driver.get("http://localhost:" + port + "/tasks");

        WebElement titleInput = getWait().until(
                ExpectedConditions.visibilityOfElementLocated(By.name("title"))
        );

        String taskTitle = "Test Task " + System.currentTimeMillis();
        titleInput.sendKeys(taskTitle);

        WebElement statusSelect = getWait().until(
                ExpectedConditions.elementToBeClickable(By.name("status"))
        );
        new Select(statusSelect).selectByVisibleText("PENDING");

        WebElement importanceSelect = getWait().until(
                ExpectedConditions.elementToBeClickable(By.name("importanceLevel"))
        );
        new Select(importanceSelect).selectByVisibleText("HIGH");

        getWait().until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']"))
        ).click();

        WebElement createdTask = getWait().until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//td//input[@value='" + taskTitle + "']")
                )
        );

        assertTrue(createdTask.isDisplayed());
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
