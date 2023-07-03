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
        Thread.sleep(3000);

        List<Book> books = new ArrayList<>();
        List<WebElement> buyButtons = driver.findElements((By.cssSelector(".action-button__text")));
        List<WebElement> bookTitles = driver.findElements((By.cssSelector(".product-title__head")));
        List<WebElement> bookPrices = driver.findElements((By.cssSelector(".product-price__value")));
        Thread.sleep(2000);

        for (int i = 0; i < booksCount; i++) {
            String bookTitle = bookTitles.get(i).getText();
            String bookPrice = bookPrices.get(i).getText();

            Book book = new Book(bookTitle, bookPrice);
            books.add(book);
            buyButtons.get(i).click();
            Thread.sleep(1000);
        }

        driver.findElement(By.cssSelector(".header-cart")).click();
        Thread.sleep(2000);

        List<WebElement> cartBookTitles = driver.findElements((By.cssSelector(".product-title__head")));

        //Проверка количества книг в корзине
        softAssert.assertEquals(cartBookTitles.size(), booksCount);

        boolean foundMatch = false;
        System.out.println("--------------------------------------------------------------");

        for (WebElement cartBook : cartBookTitles) {
            String cartBookTitle = cartBook.getText();

            for (Book book : books) {
                String bookTitle = book.title;

                if (cartBookTitle.equals(bookTitle)) {
                    System.out.println(cartBookTitle);
                    foundMatch = true;
                    break;
                }
            }
        }

        //Проверка названий книг
        softAssert.assertTrue(foundMatch);

        List<WebElement> cartBookPricesBeforeDelete = driver.findElements((By.cssSelector(".product-price__value")));
        double totalPrice = 0;

        for (WebElement cartBookPrice : cartBookPricesBeforeDelete) {
            totalPrice += convertStringPriceToDouble(cartBookPrice.getText());
        }

        WebElement cartTotalPriceElement = driver.findElement(By.cssSelector(".info-item.cart-sidebar__item-summary .info-item__value"));
        double cartTotalPrice = convertStringPriceToDouble(cartTotalPriceElement.getText());

        //Проверка стоимости книг до удаления
        softAssert.assertEquals(totalPrice, cartTotalPrice);
        Thread.sleep(2000);

        System.out.println("--------------------------------------------------------------");
        System.out.println("Стоимость книг: " + cartTotalPrice);
        System.out.println("Количество книг: " + cartBookTitles.size());

        driver.findElement(By.cssSelector(".cart-item__actions-button--delete")).click();
        Thread.sleep(2000);

        List<WebElement> cartBookPrices = driver.findElements((By.cssSelector(".product-price__value")));
        double totalPriceAfterDelete = 0;

        for (WebElement cartBookPrice : cartBookPrices) {
            totalPriceAfterDelete += convertStringPriceToDouble(cartBookPrice.getText());
        }

        WebElement cartTotalPriceAfterDeleteElement = driver.findElement(By.cssSelector(".info-item.cart-sidebar__item-summary .info-item__value"));
        double cartTotalPriceAfterDelete = convertStringPriceToDouble(cartTotalPriceAfterDeleteElement.getText());

        //Проверка стоимости книг после удаления
        softAssert.assertEquals(cartTotalPriceAfterDelete, totalPriceAfterDelete);


        List<WebElement> booksCountAfterDelete = driver.findElements((By.cssSelector(".info-item__title")));
        String booksCountAfterDeleteString = booksCountAfterDelete.get(0).getText().substring(0, 1);

        //Проверка количества книг после удаления
        softAssert.assertEquals(booksCountAfterDeleteString, "2");
        Thread.sleep(2000);

        System.out.println("--------------------------------------------------------------");
        System.out.println("Стоимость книг после удаления: " + cartTotalPriceAfterDelete);
        System.out.println("Количество книг после удаления: " + booksCountAfterDeleteString);

        softAssert.assertAll();
        Thread.sleep(1000);
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}
