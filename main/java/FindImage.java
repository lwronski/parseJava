import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import sun.net.www.protocol.http.HttpURLConnection;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.util.ByteArrayBuffer;

public class FindImage {

    private List<Images> imagesList;
    private static final String USER_AGENT = "Mozilla/5.0 (Linux; U; Android 2.2.1; en-us; MB525 Build/3.4.2-107_JDN-9) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";

    public FindImage(String url) throws IOException {
        getImages(url);
    }

    private void getImages(String url) throws IOException {

        if (!url.startsWith("http")) {
            url = "https://" + url;
        }

        Document doc = Jsoup.connect(url).get();
        imagesList = new ArrayList<>();

        Set<String> image = new HashSet();
        for (Element element : doc.select("img")) {
            image.add(element.attr("src"));
        }

        for (String images : image) {
            System.out.println(images);
            images = getAdress(images, url);
            System.out.println(images);

            if (!images.contains("base64")) {
                URL urls = new URL(images);
                InputStream in;
                try {
                    in = new BufferedInputStream(urls.openStream());

                } catch (Exception e) {
                    URLConnection connection = urls.openConnection();
                    System.setProperty("http.agent", "");
                    connection.setRequestProperty("User-Agent", USER_AGENT);
                    in = connection.getInputStream();

                }
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                int n = 0;
                while (-1 != (n = in.read(buf))) {
                    out.write(buf, 0, n);
                }
                byte[] response = out.toByteArray();
                in.close();
                out.close();
                imagesList.add(new Images(images, response.length));

            } else {
                String base64Image = images.split(",")[1];
                byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);
                imagesList.add(new Images(images, imageBytes.length));
            }
        }

    }

    public double getSumSize() {
        int sum = 0;
        for (Images image : imagesList) {
            sum += image.getSize();
        }
        System.out.println(sum);
        return (double) sum / (double) (1024 * 1024);
    }

    public String getAmount() {
        return String.valueOf(imagesList.size());
    }

    private String getAdress(String images, String url) {
        Pattern r = Pattern.compile("http");
        images = images.replace("http:", "https:");
        url = url.replace("http:", "https:");
        Matcher m = r.matcher(images);
        if (m.find()) {
            return images;
        } else {
            r = Pattern.compile("(https?://[^/]*)");
            m = r.matcher(url);
            if (m.find()) {
                if (!images.startsWith("/") && !images.startsWith("http")) {
                    images = "/" + images;
                }

                String adress = m.group(1) + images;
                if (adress.length() - adress.replaceAll("//", ".").length() == 1 && !adress.contains("base64")) {
                    return adress;
                } else if (!images.contains("base64")) {
                    return "https:" + images;
                } else {
                    return images;
                }
            }
        }
        return null;
    }


}
