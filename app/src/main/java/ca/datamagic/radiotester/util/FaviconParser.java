package ca.datamagic.radiotester.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class FaviconParser {
    public static String getFaviconUrl(String websiteUrl) throws URISyntaxException, IOException {
        // Ensure schema is provided
        String validUrl = websiteUrl;
        if (!validUrl.startsWith("http://") && !validUrl.startsWith("https://")) {
            validUrl = "https://" + validUrl;
        }

        URI baseUri = new URI(validUrl);
        String domainBase = baseUri.getScheme() + "://" + baseUri.getHost();

        // Connect and parse HTML
        Document doc = Jsoup.connect(validUrl)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .timeout(5000)
                .get();

        // Search for icon rel tags in <head>
        Elements iconElements = doc.select("link[rel~=(?i)^(shortcut icon|icon|apple-touch-icon)]");

        for (Element element : iconElements) {
            String href = element.attr("href").trim();
            if (!href.isEmpty()) {
                return resolveUrl(domainBase, validUrl, href);
            }
        }

        // Fallback to default domain favicon location
        return domainBase + "/favicon.ico";
    }

    private static String resolveUrl(String domainBase, String pageUrl, String href) {
        if (href.startsWith("http://") || href.startsWith("https://")) {
            return href;
        } else if (href.startsWith("//")) {
            return "https:" + href;
        } else if (href.startsWith("/")) {
            return domainBase + href;
        } else {
            int lastSlash = pageUrl.lastIndexOf('/');
            if (lastSlash > 8) {
                return pageUrl.substring(0, lastSlash) + "/" + href;
            } else {
                return domainBase + "/" + href;
            }
        }
    }
}
