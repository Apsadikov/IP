package ru.itis;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

public class ip1 {
    public static final String URL = "https://www.business-gazeta.ru";
    public static final int MIN_PAGES = 100;
    public static final int MIN_WORDS = 1000;

    public static void main(String[] args) throws IOException {
        List<String> links = getLinks();
        for (int i = 0; i < links.size(); i++) {
            String page = getPage(links.get(i)).html();
            if (getNumberOfWords(extractText(page)) > MIN_WORDS) {
                writePage(i, page);
                writeIndex(i, links.get(i));
            }
        }
    }

    public static Document getPage(String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            return new Document(URL);
        }
    }

    public static List<String> getLinks() {
        Document page = getPage(URL);
        HashSet<String> links = new HashSet<>();
        links.add(URL);
        int index = 1;

        while (links.size() < MIN_PAGES) {
            Elements aElements = page.select("a");
            for (Element element : aElements) {
                String link = element.attr("href");
                if (link.contains("?")) {
                    link = link.substring(0, link.indexOf("?"));
                }
                if (link.contains("#")) {
                    link = link.substring(0, link.indexOf("#"));
                }
                if (link.startsWith("/") && !link.equals("/")) {
                    links.add(URL + link);
                }
            }
            index += 1;
            page = getPage(links.toArray()[index].toString());
        }

        return links.stream().toList();
    }

    public static void writePage(int index, String text) throws IOException {
        FileWriter writer = new FileWriter(index + ".txt", false);
        writer.write(extractText(text));
        writer.flush();
        writer.close();
    }

    public static void writeIndex(int index, String link) throws IOException {
        FileWriter writer = new FileWriter("index.txt", true);
        writer.write(index + " " + link);
        writer.write("\n");
        writer.flush();
        writer.close();
    }

    public static long getNumberOfWords(String text) {
        String trim = text.trim();
        if (trim.isEmpty())
            return 0;
        return trim.split("\\s+").length;
    }

    public static String extractText(String text) {
        text = Jsoup.parse(text).wholeText();
        text = text.replaceAll("[^А-Яа-я\s]", " ");
        text = text.replaceAll("\s+", " ");
        return text;
    }
}
