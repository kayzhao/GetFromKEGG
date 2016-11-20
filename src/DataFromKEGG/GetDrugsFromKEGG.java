package DataFromKEGG;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 从KEGG抓取Drug数据：<br>
 * ID<br>
 * Name<br>
 * Formula<br>
 * Exact mass<br>
 * Mol weight<br>
 */
public class GetDrugsFromKEGG {
	public static void main(String[] args) {
		GetDrugsFromKEGG kegg = new GetDrugsFromKEGG();
		try {

			/**
			 * 测试一条数据
			 */
			String url = "http://www.kegg.jp/dbget-bin/www_bget?dr:D00001";
			System.out.println(kegg.getContentPr(url));

			/**
			 * 抓取所有数据
			 */
			kegg.getDrugs("Kegg_Drugs_AllDatas.txt");
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
			doc = Jsoup.connect(url).timeout(100000).get();
			// doc is null
			if (doc == null) {
				return null;
			}

			// No such data.
			Elements strongElements = doc.getElementsByTag("strong");
			for (Element strong : strongElements) {
				if (strong.ownText().contains("No such data."))
					return null;
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
				// 获取name
				if (nobr_str.equals("Name")) {
					Element other_tr = nobr.parent().nextElementSibling();
					Elements div_tags = other_tr.getElementsByTag("div");
					String[] names = div_tags.get(1).ownText().split("<br>");
					for (String name : names) {
						result += name;
					}
				}

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

				// Other DBs
				if (nobr_str.equals("Other DBs")) {
					result += "\t";
					Element other_tr = nobr.parent().nextElementSibling();
					Elements div_tags = other_tr.getElementsByTag("div");
					for (int i = 0; i < div_tags.size(); i++) {
						result += (div_tags.get(i).ownText());
						if (i > 0
								&& div_tags.get(i).getElementsByTag("a") != null) {
							// result += ",";// 与前面的串分隔开，Cas number不含a标签
							for (Element a : div_tags.get(i).getElementsByTag(
									"a")) {
								result += (a.ownText() + ",");
							}
							// cas number强行分割开
							result += ",";
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
		// int id = 1, max = 10601;
		int id = 1, max = 10808;

		// 表头
		outputStream.write(("ID\tName\tFormula\tExact mass\tMol weight\n")
				.getBytes());
		// 注意flush
		outputStream.flush();

		DecimalFormat df = new DecimalFormat("00000");
		String url = "";
		while (id <= max) {
			url = "http://www.kegg.jp/dbget-bin/www_bget?dr:D" + df.format(id);
			System.out.println(url);
			String result = getContentPr(url);
			if (result != null && result != "") {
				outputStream.write(("D" + df.format(id) + "\t" + result + "\n")
						.getBytes());
				// 注意flush
				outputStream.flush();
			}
			id++;
		}
		outputStream.flush();
		outputStream.close();
	}
}
