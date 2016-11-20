package DataFromBioCSU;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 从生物组服务器抓取数据 <br>
 * <a href="http://bioinformatics.csu.edu.cn">bio.csu</a> column name ：<br>
 * Paper Citation<br>
 * Paper URL<br>
 * Published Year
 */
public class GetFromBioCSUServer {
	public static void main(String[] args) throws IOException {
		getContentPr("csu_bio_papers.txt");
	}

	/**
	 * 
	 * @param url
	 * @param output
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public static void getContentPr(String out) throws IOException {

		FileOutputStream outputStream = new FileOutputStream(new File(out));
		String url = "http://bioinformatics.csu.edu.cn/publication.jsp";

		Document doc;
		doc = Jsoup.connect(url).timeout(10000).get();
		if (doc == null) {
			return;
		}

		// years
		Elements h3s = doc.getElementsByTag("h3");

		for (Element h3 : h3s) {
			String year = h3.getElementsByTag("strong").get(0).ownText().trim();// 年份
			System.out.println(year);
			outputStream.write((year + "\n").getBytes());

			String res = "";
			// paper list
			Element p = h3.nextElementSibling();
			while (p != null && p.tagName() != "h3" && p.tagName() != "div") {
				Elements as = p.getElementsByTag("a");
				if (as.size() > 0) {
					res = p.ownText() + "\t" + as.get(0).attr("href") + "\t"
							+ year;
				} else {
					res = p.ownText() + "\t" + "-" + "\t" + year;
				}
				System.out.println(res);
				outputStream.write((res + "\n").getBytes());
				outputStream.flush();
				p = p.nextElementSibling();
			}
		}
		outputStream.flush();
		outputStream.close();
	}
}
