import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FindLink {


    public List<String> getListLink(String url) throws IOException {

        if (!url.startsWith("http")) url = "https://" + url;

        Document doc = Jsoup.connect(url).get();
        List<String> linkList = new ArrayList<>();

        for (Element element : doc.select("a[href]")) {
            String attr = element.attr("abs:href");
            if (!linkList.contains(attr) && !attr.equals("")) {
                linkList.add(attr);
            }
        }

        return linkList;
    }


}
