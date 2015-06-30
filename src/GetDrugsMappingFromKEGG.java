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

public class GetDrugsMappingFromKEGG {
	public static void main(String[] args) {
		GetDrugsMappingFromKEGG kegg = new GetDrugsMappingFromKEGG();
		try {
			kegg.getDrugs("Kegg_Drugs_Mapping.txt");
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

				// Other DBs
				if (nobr_str.equals("Other DBs")) {
					Element other_tr = nobr.parent().nextElementSibling();
					Elements div_tags = other_tr.getElementsByTag("div");
					for (int i = 0; i < div_tags.size(); i++) {
						result += (div_tags.get(i).ownText());
						if (i > 0
								&& div_tags.get(i).getElementsByTag("a") != null) {
						//	result += ",";// 与前面的串分隔开，Cas number不含a标签
							for (Element a : div_tags.get(i).getElementsByTag(
									"a")) {
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

	public void getDrugs(String writepath) throws IOException {
		FileOutputStream outputStream = new FileOutputStream(
				new File(writepath));
		int id = 1, max = 10601;

		DecimalFormat df = new DecimalFormat("00000");
		String url = "";
		while (id < max) {
			url = "http://www.kegg.jp/dbget-bin/www_bget?dr:D" + df.format(id);
			System.out.println(url);
			outputStream
					.write(("D" + df.format(id) + "\t" + getContentPr(url) + "\n")
							.getBytes());
			id++;
		}
		outputStream.flush();
		outputStream.close();
	}
}
