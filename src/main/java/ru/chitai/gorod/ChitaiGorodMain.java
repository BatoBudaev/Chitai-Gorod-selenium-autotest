package ru.chitai.gorod;

public class ChitaiGorodMain {
    static class Book {
        String title;
        String price;

        Book(String title, String price) {
            this.title = title;
            this.price = price;
        }
    }

    static double convertStringPriceToDouble(String price) {
        String priceWithoutSpaces = price.replaceAll("\\s+", "");
        String totalPriceWithoutSymbol = priceWithoutSpaces.replaceAll("[₽]", "");

        return Double.parseDouble(totalPriceWithoutSymbol);
    }
}