package ru.chitai.gorod;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.testng.asserts.SoftAssert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;
import java.util.ArrayList;

public class ChitaiGorodTest extends ChitaiGorodMain {
    public WebDriver driver;

    @BeforeEach
    public void setUp() {
        String site = "https://www.chitai-gorod.ru/";
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();

        driver.get(site);
        driver.manage().window().maximize();
        Assertions.assertEquals(site, driver.getCurrentUrl());
    }

    @Test
    public void cartTest() throws InterruptedException {
        int booksCount = 3;
        SoftAssert softAssert = new SoftAssert();

        WebElement searchInput = driver.findElement(By.cssSelector(".header-search__input"));
        searchInput.sendKeys("тестирование");
        Thread.sleep(2000);

        searchInput.submit();
        Thread.sleep(1000);

        List<Book> books = new ArrayList<>();
        List<WebElement> buyButtons = driver.findElements((By.cssSelector(".action-button__text")));
        List<WebElement> bookTitles = driver.findElements((By.cssSelector(".product-title__head")));
        List<WebElement> bookPrices = driver.findElements((By.cssSelector(".product-price__value")));
        Thread.sleep(1000);

        for (int i = 0; i < booksCount; i++) {
            String bookTitle = bookTitles.get(i).getText();
            String bookPrice = bookPrices.get(i).getText();

            Book book = new Book(bookTitle, bookPrice);
            books.add(book);
            buyButtons.get(i).click();
            Thread.sleep(500);
        }

        driver.findElement(By.cssSelector(".header-cart")).click();
        Thread.sleep(2000);

        List<WebElement> cartBookTitles = driver.findElements((By.cssSelector(".product-title__head")));

        //Проверка количества книг в корзине
        softAssert.assertEquals(cartBookTitles.size(), booksCount);

        boolean foundMatch = false;

        for (WebElement cartBook : cartBookTitles) {
            String cartBookTitle = cartBook.getText();

            for (Book book : books) {
                String bookTitle = book.title;

                if (cartBookTitle.equals(bookTitle)) {
                    foundMatch = true;
                    break;
                }
            }
        }

        //Проверка названий книг
        softAssert.assertTrue(foundMatch);

        double totalPrice = 0;

        for (Book book : books) {
            totalPrice += convertStringPriceToDouble(book.price);
        }

        WebElement cartTotalPriceElement = driver.findElement(By.cssSelector(".info-item.cart-sidebar__item-summary .info-item__value"));
        double cartTotalPrice = convertStringPriceToDouble(cartTotalPriceElement.getText());

        //Проверка стоимости книг
        softAssert.assertEquals(totalPrice, cartTotalPrice);

        softAssert.assertAll();
        Thread.sleep(1000);
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}
