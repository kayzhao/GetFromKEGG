package DataProcess;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Drug Mapping( KEGG to DrugBank)
 */
public class KEGG_to_DrugBank {
	public static void main(String[] args) {
		KEGG_to_DrugBank kegg = new KEGG_to_DrugBank();
		try {
			kegg.readIDs("KEGG_Drug.txt", "DrugBank_Drug.txt");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void readIDs(String readPath, String writePath) throws IOException {
		FileInputStream inputStream = new FileInputStream(new File(readPath));
		BufferedReader br = new BufferedReader(new InputStreamReader(
				inputStream));
		FileOutputStream outputStream = new FileOutputStream(
				new File(writePath));
		String str = "";
		while ((str = br.readLine()) != null) {
			System.out.println(str);
			String[] lines = str.trim().split("\t");
			String dbid = "";
			for (int i = 0; i < lines.length; i++) {
				dbid = getDBIDs(lines[i].trim());
				System.out.println(lines[i] + "\t" + dbid);
				outputStream.write((lines[i] + "\t" + dbid + "\n").getBytes());
			}
		}
		br.close();
		outputStream.flush();
		outputStream.close();
	}

	public String getDBIDs(String id) throws IOException {
		Document doc;
		String url = "http://www.kegg.jp/dbget-bin/www_bget?dr:" + id;
		String dbid = "";

		doc = Jsoup.connect(url).timeout(10000).get();

		Elements tbodys = doc.getElementsByTag("tbody");
		Element tbody = tbodys.get(2);
		Elements nobrs = tbody.getElementsByTag("nobr");
		for (Element nobr : nobrs) {
			String nobr_str = nobr.ownText();
			if (nobr_str.equals("Other DBs")) {
				Element other_tr = nobr.parent().parent();
				Elements div_tags = other_tr.getElementsByTag("div");
				for (Element div : div_tags) {
					// System.out.print(div.ownText().trim());
					if ("DrugBank: ".equals(div.ownText())) {
						for (Element a : div.nextElementSibling()
								.getElementsByTag("a")) {
							dbid += (a.ownText() + "\t");
						}
					}
				}
			}
		}
		return dbid;
	}
}