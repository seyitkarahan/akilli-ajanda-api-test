package com.seyitkarahan.akilli_ajanda_api.seleniumTest;

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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EventPageTest {

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
    public void testAddEvent() {
        login();

        driver.get("http://localhost:" + port + "/events");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.titleIs("Events"));

        // Use more specific selectors for the "Add Event" form
        WebElement titleInput = driver.findElement(By.xpath("//form[@action='/events']//input[@name='title']"));
        String eventTitle = "Test Event " + System.currentTimeMillis();
        titleInput.sendKeys(eventTitle);

        JavascriptExecutor js = (JavascriptExecutor) driver;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        WebElement startTimeInput = driver.findElement(By.xpath("//form[@action='/events']//input[@name='startTime']"));
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        js.executeScript("arguments[0].value = arguments[1];", startTimeInput, startTime.format(formatter));

        WebElement endTimeInput = driver.findElement(By.xpath("//form[@action='/events']//input[@name='endTime']"));
        LocalDateTime endTime = startTime.plusHours(1);
        js.executeScript("arguments[0].value = arguments[1];", endTimeInput, endTime.format(formatter));

        WebElement addButton = driver.findElement(By.xpath("//form[@action='/events']//button[@type='submit']"));
        addButton.click();

        // Wait for the page to reload by waiting for the title to be "Events" again
        wait.until(ExpectedConditions.titleIs("Events"));

        // Now, wait for the new event to appear in the list
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//td/form/input[@value='" + eventTitle + "']")));

        boolean isEventPresent = !driver.findElements(By.xpath("//td/form/input[@value='" + eventTitle + "']")).isEmpty();
        assertTrue(isEventPresent, "The new event should be present in the table.");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
            driver.quit();
        }
    }
}
