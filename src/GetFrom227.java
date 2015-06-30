import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GetFrom227 {
	public static void main(String[] args) throws IOException {
		getContentPr();
	}

	/**
	 * 
	 * @param url
	 * @param output
	 * @return
	 * @throws IOException
	 */
	public static void getContentPr() throws IOException {

		FileOutputStream outputStream = new FileOutputStream(new File(
				"paper.txt"));

		String url = "http://202.197.66.227/publication.html";

		Document doc;
		String res = "";

		doc = Jsoup.connect(url).timeout(10000).get();
		Elements h2s = doc.getElementsByTag("h2");
		for (Element h2 : h2s) {
			String h2_ = h2.ownText().trim();// 年份
			Element ol = h2.nextElementSibling();
			Elements ol_lis = ol.getElementsByTag("li");
			for (Element li : ol_lis) {
				Elements as = li.getElementsByTag("a");
				if (as.size() > 0) {
					res = li.ownText() + "\t" + as.get(0).attr("href") + "\t"
							+ h2_;
				} else {
					res = li.ownText() + "\t" + "-" + "\t" + h2_;
				}
				outputStream.write((res + "\n").getBytes());
			}
		}

		outputStream.flush();
		outputStream.close();
	}
}
