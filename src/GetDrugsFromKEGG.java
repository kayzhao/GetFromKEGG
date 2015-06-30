import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GetDrugsFromKEGG {
	public static void main(String[] args) {
		GetDrugsFromKEGG kegg = new GetDrugsFromKEGG();
		try {
			kegg.getDrugs("Kegg_Drugs_Info.txt");
			// System.out.println(getContentPr("http://www.kegg.jp/dbget-bin/www_bget?dr:D10350"));
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
				//
				if (nobr_str.equals("Name")) {
					Element other_tr = nobr.parent().nextElementSibling();
					Elements div_tags = other_tr.getElementsByTag("div");
					String[] names = div_tags.get(1).ownText().split("<br>");
					for (String name : names) {
						result += name;
					}
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
			System.out.println(id);
			url = "http://www.kegg.jp/dbget-bin/www_bget?dr:D" + df.format(id);
			result = getContentPr(url);
			if (result != "") {
				outputStream.write(("D" + df.format(id) + "\t"
						+ getContentPr(url) + "\n").getBytes());
			}
			id++;
		}
		outputStream.flush();
		outputStream.close();
	}
}
