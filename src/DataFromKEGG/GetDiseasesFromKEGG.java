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
 * 从KEGG抓取疾病数据：<br>
 * ID<br>
 * Name<br>
 * Description<br>
 * Category<br>
 * Drug<br>
 * Other DBs
 */
public class GetDiseasesFromKEGG {
	public static void main(String[] args) {
		GetDiseasesFromKEGG kegg = new GetDiseasesFromKEGG();
		try {
			/**
			 * 测试一条数据
			 */
			String url = "http://www.kegg.jp/dbget-bin/www_bget?ds:H00001";
			System.out.println(kegg.getContentPr(url));

			/**
			 * 所有数据
			 */
			kegg.getDisease("Kegg_Diseases_AllData.txt");
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

			// tbodys size is <=2
			Elements tbodys = doc.getElementsByTag("tbody");
			if (tbodys.size() <= 2)
				return null;

			Element tbody = tbodys.get(2);
			Elements nobrs = tbody.getElementsByTag("nobr");
			for (Element nobr : nobrs) {
				String nobr_str = nobr.ownText();

				// 获取drug name
				if (nobr_str.equals("Name")) {
					Element other_tr = nobr.parent().nextElementSibling();
					Elements div_tags = other_tr.getElementsByTag("div");
					String[] names = div_tags.get(1).ownText().split("<br>");
					for (String name : names) {
						result += name;
					}
				}

				// 获取Description
				if (nobr_str.equals("Description")) {
					result += "\t";
					Element other_tr = nobr.parent().nextElementSibling();
					Elements div_tags = other_tr.getElementsByTag("div");
					String[] names = div_tags.get(0).ownText().split("<br>");
					result += names[0];
				}

				// 获取Category
				if (nobr_str.equals("Category")) {
					result += "\t";
					Element other_tr = nobr.parent().nextElementSibling();
					Elements div_tags = other_tr.getElementsByTag("div");
					String[] names = div_tags.get(0).ownText().split("<br>");
					result += names[0];
				}

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

				// Gene

				// Other DBs
				if (nobr_str.equals("Other DBs")) {
					result += "\t";
					Element other_tr = nobr.parent().parent();
					Elements div_tags = other_tr.getElementsByTag("div");
					for (Element div : div_tags) {
						// 如果div包含":"，则他的下一个兄弟中的a标签里的元素都是对应的Other DBs ID
						if (div.ownText().trim().contains(":")) {
							result += div.ownText().trim();
							for (Element a : div.nextElementSibling()
									.getElementsByTag("a")) {
								result += (a.ownText() + ",");
							}
							result += ";";
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
		// int id = 1, max = 1437;
		int id = 1, max = 1698;

		// 表头
		outputStream
				.write(("ID\tName\tDescription\tCategory\tDrug\tOther DBs\n")
						.getBytes());
		// 注意flush
		outputStream.flush();

		DecimalFormat df = new DecimalFormat("00000");
		String url = "";
		while (id < max) {
			url = "http://www.kegg.jp/dbget-bin/www_bget?ds:H" + df.format(id);
			System.out.println(url);
			String result = getContentPr(url);
			if (result != null && result != "") {
				outputStream.write(("H" + df.format(id) + "\t" + result + "\n")
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
