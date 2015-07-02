import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GetDrugsFromKEGG {
	public static void main(String[] args) {
		GetDrugsFromKEGG kegg = new GetDrugsFromKEGG();
		try {
			kegg.getDrugs("Kegg_Drugs_Formula_Mass_Weight.txt");
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
	public static String getContentPr(String url) {
		Document doc;
		String result = "";
		try {
			doc = Jsoup.connect(url).timeout(10000).get();
			if (doc == null) {
				return result;
			}
			Elements tbodys = doc.getElementsByTag("tbody");
			Element tbody = null;
			if (tbodys.size() > 2) {
				tbody = tbodys.get(2);
			} else {
				return result;
			}
			Elements nobrs = tbody.getElementsByTag("nobr");
			for (Element nobr : nobrs) {
				String nobr_str = nobr.ownText();
				// 获取drug name
				// if (nobr_str.equals("Name")) {
				// Element other_tr = nobr.parent().nextElementSibling();
				// Elements div_tags = other_tr.getElementsByTag("div");
				// String[] names = div_tags.get(1).ownText().split("<br>");
				// for (String name : names) {
				// result += name;
				// }
				// }

				// 获取Formula
				if (nobr_str.equals("Formula")) {
					result += "\t";
					Element other_tr = nobr.parent().nextElementSibling();
					Elements div_tags = other_tr.getElementsByTag("div");
					String[] names = div_tags.get(0).ownText().split("<br>");
					result += names[0];
				}

				// 获取Exact mass
				if (nobr_str.equals("Exact mass")) {
					result += "\t";
					Element other_tr = nobr.parent().nextElementSibling();
					Elements div_tags = other_tr.getElementsByTag("div");
					String[] names = div_tags.get(0).ownText().split("<br>");
					result += names[0];
				}

				// 获取Mol weight
				if (nobr_str.equals("Mol weight")) {
					result += "\t";
					Element other_tr = nobr.parent().nextElementSibling();
					Elements div_tags = other_tr.getElementsByTag("div");
					String[] names = div_tags.get(0).ownText().split("<br>");
					result += names[0];
				}

			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return result;
	}

	public void getDrugs(String writepath) throws IOException {
		FileOutputStream outputStream = new FileOutputStream(
				new File(writepath));
		int id = 1, max = 10601;

		DecimalFormat df = new DecimalFormat("00000");
		String url = "";
		String result = null;
		while (id <= max) {
			url = "http://www.kegg.jp/dbget-bin/www_bget?dr:D" + df.format(id);
			System.out.println(url);
			result = getContentPr(url);
			if (result != "") {
				outputStream
						.write(("D" + df.format(id) + getContentPr(url) + "\n")
								.getBytes());
				//注意flush
				outputStream.flush();
			}
			id++;
		}
		outputStream.flush();
		outputStream.close();
	}
}
