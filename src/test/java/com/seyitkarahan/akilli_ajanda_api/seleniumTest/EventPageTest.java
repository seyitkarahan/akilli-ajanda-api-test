package com.seyitkarahan.akilli_ajanda_api.seleniumTest;

import com.seyitkarahan.akilli_ajanda_api.dto.request.AuthRequest;
import com.seyitkarahan.akilli_ajanda_api.service.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class EventPageTest {

    private WebDriver driver;

    @LocalServerPort
    private int port;

    @Autowired
    private AuthService authService;

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
    }

    private void login() {
        // Ensure the user exists before trying to login
        try {
            AuthRequest registerRequest = new AuthRequest();
            registerRequest.setName("Test User");
            registerRequest.setEmail("deneme@gmail.com");
            registerRequest.setPassword("1234");
            authService.register(registerRequest);
        } catch (Exception e) {
            // User might already exist, which is fine
        }

        driver.get("http://localhost:" + port + "/login");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.titleIs("Giriş Yap"));

        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));
        WebElement passwordInput = driver.findElement(By.name("password"));

        emailInput.sendKeys("deneme@gmail.com");
        passwordInput.sendKeys("1234");

        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
        loginButton.click();

        wait.until(ExpectedConditions.titleIs("Dashboard"));
    }

    @Test
    public void testAddEvent() {
        login();

        driver.get("http://localhost:" + port + "/events");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));

        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("input[name='title']")
        ));

        String eventTitle = "Test Event " + System.currentTimeMillis();
        driver.findElement(By.name("title")).sendKeys(eventTitle);

        JavascriptExecutor js = (JavascriptExecutor) driver;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        js.executeScript(
                "arguments[0].value = arguments[1];",
                driver.findElement(By.name("startTime")),
                startTime.format(formatter)
        );

        LocalDateTime endTime = startTime.plusHours(1);
        js.executeScript(
                "arguments[0].value = arguments[1];",
                driver.findElement(By.name("endTime")),
                endTime.format(formatter)
        );

        driver.findElement(By.cssSelector("form button[type='submit']")).click();

        driver.get("http://localhost:" + port + "/events");

        assertTrue(driver.getPageSource().contains(eventTitle));
    }


    @AfterEach
    public void tearDown() {
        if (driver != null) {
            try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
            driver.quit();
        }
    }
}
