package ru.parser;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WeatherForecastApp {

    private static final Pattern pattern = Pattern.compile("\\d{2}\\.\\d{2}");

    private static String getDate(String stringDate) throws Exception {
        Matcher matcher = pattern.matcher(stringDate);
        if (matcher.find()) {
            return matcher.group();
        }
        throw new Exception("Не смог найти дату в строке");
    }

    private static Document getPage() throws IOException {
        String url = "https://pogoda.spb.ru/";
        return Jsoup.parse(new URL(url), 3000);
    }

    private static String getHeadString(Element element) throws Exception {
        String dateString = element.select("th[id=dt]").text();
        String date = getDate(dateString);
        String conditions = element.select("th[id=yt]").text();
        String temp = element.select("th[id=tt]").text();
        String pressure = element.select("th[id=pt]").text();
        String humidity = element.select("th[id=ht]").text();
        String wind = element.select("th[id=wt]").text();
        return String.format("%-10s%-25s%-7s%-8s%-15s%-15s", date, conditions, temp, pressure, humidity, wind);
    }

    private static Integer getValuesString(Elements values, int index) {
        int count = 0;
        if (index == 0) {
            if (values.get(2).text().contains("Утро")) {
                count = 2;
            }
            if (values.get(3).text().contains("Утро")) {
                count = 3;
            }
            if (values.get(4).text().contains("Утро")) {
                count = 4;
            }
            if (values.get(5).text().contains("Утро")) {
                count = 5;
            }
            for (int i = 0; i < count; i++) {
                Element val = values.get(index + i);
                System.out.println(val.text());
            }
        } else {
            for (int i = 0; i < 4; i++) {
                Element val = values.get(index + i);
                System.out.println(val.text());
                count++;
            }
        }
        return count;
    }

    public static void main(String[] args) throws Exception {
        Document page = getPage();
        Element table = page.select("table[class=wt]").first();
        if (table == null) throw new Exception("Нет данных для обработки");

        Elements head = table.select("tr[class=wth]");
        Elements values = table.select("tr[valign=top]");
        int index = 0;
        for (Element element : head) {
            System.out.println(getHeadString(element));
            int count = getValuesString(values, index);
            System.out.println();
            index += count;
        }
    }
}