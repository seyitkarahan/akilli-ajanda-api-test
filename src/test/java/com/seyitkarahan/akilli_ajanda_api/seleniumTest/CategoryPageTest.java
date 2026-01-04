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
public class CategoryPageTest {

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
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30)); // Increased timeout
        wait.until(ExpectedConditions.titleIs("Giriş Yap"));

        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email"))); // Explicit wait
        WebElement passwordInput = driver.findElement(By.name("password"));

        emailInput.sendKeys("deneme@gmail.com");
        passwordInput.sendKeys("1234");

        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
        loginButton.click();

        wait.until(ExpectedConditions.titleIs("Dashboard"));
    }

    @Test
    public void testAddCategory() {
        login();

        driver.get("http://localhost:" + port + "/categories");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30)); // Increased timeout
        wait.until(ExpectedConditions.titleIs("Categories"));

        WebElement nameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@placeholder='Yeni kategori adı']"))); // Explicit wait
        String categoryName = "Test Category " + System.currentTimeMillis();
        nameInput.sendKeys(categoryName);

        WebElement addButton = driver.findElement(By.className("btn-add"));
        addButton.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@value='" + categoryName + "']")));

        boolean isCategoryPresent = !driver.findElements(By.xpath("//input[@value='" + categoryName + "']")).isEmpty();
        assertTrue(isCategoryPresent, "The new category should be present in the table.");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
            driver.quit();
        }
    }
}
