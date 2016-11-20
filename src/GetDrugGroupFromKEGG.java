import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 从KEGG抓取Drug Group数据：<br>
 * ID<br>
 * Name<br>
 * Member<br>
 * Remark<br>
 * Comment<br>
 */
public class GetDrugGroupFromKEGG {
	public static void main(String[] args) {
		GetDrugGroupFromKEGG kegg = new GetDrugGroupFromKEGG();
		try {
			kegg.getData("Kegg_DrugGroup_Data.txt");
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

			// Elements tbodys = doc.getElementsByTag("tbody");
			// Element tbody = tbodys.get(2);
			Elements nobrs = tbody.getElementsByTag("nobr");
			for (Element nobr : nobrs) {
				String nobr_str = nobr.ownText();

				// 获取name
				if (nobr_str.equals("Name")) {
					Element other_tr = nobr.parent().nextElementSibling();
					Elements div_tags = other_tr.getElementsByTag("div");
					String[] names = div_tags.get(0).ownText().split("<br>");
					for (String name : names) {
						result += name;
					}
				}

				// Remark
				if (nobr_str.equals("Remark")) {
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

				// Drug IDs
				if (nobr_str.equals("Member")) {
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

				// 获取Comment
				if (nobr_str.equals("Comment")) {
					result += "\t";
					Element other_tr = nobr.parent().nextElementSibling();
					Elements div_tags = other_tr.getElementsByTag("div");
					String[] names = div_tags.get(0).ownText().split("<br>");
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

	public void getData(String writepath) throws IOException {
		FileOutputStream outputStream = new FileOutputStream(
				new File(writepath));
		// int id = 1, max = 1822;
		int id = 1, max = 1979;

		// 表头
		outputStream.write(("ID\tName\tMember\tRemark\tComment\n").getBytes());
		// 注意flush
		outputStream.flush();

		DecimalFormat df = new DecimalFormat("00000");
		String url = "";
		while (id < max) {
			url = "http://www.kegg.jp/dbget-bin/www_bget?dg:DG" + df.format(id);
			System.out.println(url);
			outputStream
					.write(("DG" + df.format(id) + "\t" + getContentPr(url) + "\n")
							.getBytes());
			id++;
		}
		outputStream.flush();
		outputStream.close();
	}
}
