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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ImageFilePageTest {

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
    public void testUploadImage() throws IOException {
        login();

        driver.get("http://localhost:" + port + "/image-files");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        wait.until(ExpectedConditions.titleIs("Image Files"));

        WebElement fileInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("input[type='file']")
                )
        );

        File testFile = File.createTempFile("test-image", ".txt");
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("This is a test file content.");
        }
        testFile.deleteOnExit();

        fileInput.sendKeys(testFile.getAbsolutePath());

        WebElement addButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("button[type='submit']")
                )
        );
        addButton.click();

        WebElement uploadedFileLink = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//a[contains(text(),'.txt')]")
                )
        );

        assertTrue(uploadedFileLink.isDisplayed(),
                "The uploaded image link should be visible.");
        assertTrue(uploadedFileLink.getText().endsWith(".txt"),
                "The uploaded file should have the correct extension.");
    }


    @AfterEach
    public void tearDown() {
        if (driver != null) {
            try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
            driver.quit();
        }
    }
}
