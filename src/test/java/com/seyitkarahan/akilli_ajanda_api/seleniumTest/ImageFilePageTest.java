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

import java.io.File;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ImageFilePageTest {

    private WebDriver driver;

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        // options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--ignore-certificate-errors");

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
        wait.until(ExpectedConditions.numberOfElementsToBe(
                By.xpath("//table[@class='category-table']/tbody/tr"), rowsBeforeCount + 1));

        // 4. Verify that the new row count is correct
        List<WebElement> rowsAfter = driver.findElements(By.xpath("//table[@class='category-table']/tbody/tr"));
        assertEquals(rowsBeforeCount + 1, rowsAfter.size(), "The number of rows should increase by 1.");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
            driver.quit();
        }
    }
}
