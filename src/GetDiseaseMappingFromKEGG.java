import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GetDiseaseMappingFromKEGG {
	public static void main(String[] args) {
		GetDiseaseMappingFromKEGG kegg = new GetDiseaseMappingFromKEGG();
		try {
			kegg.getDisease("Kegg_Diseases.txt");
			/*
			 * String result = kegg
			 * .getContentPr("http://www.kegg.jp/dbget-bin/www_bget?ds:H00001");
			 * System.out.println(result);
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param url
	 * @param output
	 * @return
	 */
	public String getContentPr(String url) {
		Document doc;
		String result = "";
		try {
			doc = Jsoup.connect(url).timeout(10000).get();
			Elements tbodys = doc.getElementsByTag("tbody");
			Element tbody = tbodys.get(2);
			Elements nobrs = tbody.getElementsByTag("nobr");
			for (Element nobr : nobrs) {
				String nobr_str = nobr.ownText();
				// Drug
				if (nobr_str.equals("Drug")) {
					result += "\t";
					Element other_tr = nobr.parent().nextElementSibling();
					Elements a_tags = other_tr.getElementsByTag("a");
					if (a_tags != null) {
						// result += ",";// 与前面的串分隔开，Cas number不含a标签
						for (Element a : a_tags) {
							result += (a.ownText() + ",");
						}
					}
				}

				// Other DBs
				if (nobr_str.equals("Other DBs")) {
					result += "\t";

					Element other_tr = nobr.parent().parent();
					Elements div_tags = other_tr.getElementsByTag("div");
					for (Element div : div_tags) {
						if ("OMIM: ".equals(div.ownText().trim())) {
							for (Element a : div.nextElementSibling()
									.getElementsByTag("a")) {
								result += (a.ownText() + ",");
							}
						}
					}
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return result;
	}

	public void getDisease(String writepath) throws IOException {
		FileOutputStream outputStream = new FileOutputStream(
				new File(writepath));
		int id = 1, max = 1437;

		DecimalFormat df = new DecimalFormat("00000");
		String url = "";
		while (id < max) {
			url = "http://www.kegg.jp/dbget-bin/www_bget?ds:H" + df.format(id);
			System.out.println(url);
			outputStream.write(("H" + df.format(id) + getContentPr(url) + "\n")
					.getBytes());
			id++;
		}
		outputStream.flush();
		outputStream.close();
	}
}
