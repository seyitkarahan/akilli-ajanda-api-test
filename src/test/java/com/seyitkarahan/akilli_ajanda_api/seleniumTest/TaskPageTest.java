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
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskPageTest {

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
    public void testAddTask() {
        login();

        driver.get("http://localhost:" + port + "/tasks");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.titleIs("Tasks"));

        WebElement titleInput = driver.findElement(By.xpath("//form[@action='/tasks']//input[@name='title']"));
        String taskTitle = "Test Task " + System.currentTimeMillis();
        titleInput.sendKeys(taskTitle);

        WebElement statusSelect = driver.findElement(By.xpath("//form[@action='/tasks']//select[@name='status']"));
        new Select(statusSelect).selectByVisibleText("PENDING");

        WebElement importanceSelect = driver.findElement(By.xpath("//form[@action='/tasks']//select[@name='importanceLevel']"));
        new Select(importanceSelect).selectByVisibleText("HIGH");

        WebElement addButton = driver.findElement(By.xpath("//form[@action='/tasks']//button[@type='submit']"));
        addButton.click();

        wait.until(ExpectedConditions.titleIs("Tasks"));

        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//td/form/input[@value='" + taskTitle + "']")));

        boolean isTaskPresent = !driver.findElements(By.xpath("//td/form/input[@value='" + taskTitle + "']")).isEmpty();
        assertTrue(isTaskPresent, "The new task should be present in the table.");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
            driver.quit();
        }
    }
}
