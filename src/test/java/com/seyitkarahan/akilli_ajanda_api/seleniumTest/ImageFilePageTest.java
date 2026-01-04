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
    public void testUploadImage() {
        login();

        driver.get("http://localhost:" + port + "/image-files");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.titleIs("Image Files"));

        // 1. Count rows before upload
        List<WebElement> rowsBefore = driver.findElements(By.xpath("//table[@class='category-table']/tbody/tr"));
        int rowsBeforeCount = rowsBefore.size();

        // 2. Upload the file
        File testFile = new File("src/test/resources/test-image.txt");
        WebElement fileInput = driver.findElement(By.xpath("//input[@type='file']"));
        fileInput.sendKeys(testFile.getAbsolutePath());

        WebElement addButton = driver.findElement(By.className("btn-add"));
        addButton.click();

        // 3. Wait for the number of rows to increase by 1
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                By.xpath("//table[@class='category-table']/tbody/tr"), rowsBeforeCount));

        // 4. Now that the row exists, wait for the link inside it to be visible
        // Note: The file name on the server will be a UUID, so we can't search for "test-image" in the link text directly
        // unless the UI displays the original filename or we check for the presence of ANY new link.
        // However, looking at ImageService, it saves the file with a UUID name, but the ImageResponse returns that UUID name.
        // The UI displays `imageFile.fileName`.
        // Since the original filename is lost and replaced by a UUID, we should check if a new row appeared.
        
        // Let's just verify that we have more rows than before, which we already did.
        // To be more specific, we can check if the last row contains a link.
        
        List<WebElement> rowsAfter = driver.findElements(By.xpath("//table[@class='category-table']/tbody/tr"));
        WebElement lastRow = rowsAfter.get(rowsAfter.size() - 1);
        WebElement link = lastRow.findElement(By.tagName("a"));
        
        assertTrue(link.isDisplayed(), "The uploaded image link should be present in the table.");
        assertTrue(link.getText().endsWith(".txt"), "The uploaded file should have the correct extension.");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
            driver.quit();
        }
    }
}
