package DataFromKEGG;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.soap.Text;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 从KEGG抓取疾病数据：<br>
 * ID<br>
 * Drug<br>
 */
public class GetDiseaseDrugAssocationsFromKEGG {
	/**
	 * column name
	 */
	String[] colname = { "ID", "Drug" };

	public static void main(String[] args) {
		GetDiseaseDrugAssocationsFromKEGG kegg = new GetDiseaseDrugAssocationsFromKEGG();
		try {
			/**
			 * 测试一条数据
			 */
			String url = "http://www.kegg.jp/dbget-bin/www_bget?ds:H00001";
			System.out.println(kegg.getContentPr(url));

			/**
			 * 所有数据
			 */
			kegg.getDisease("Kegg_Diseases_Drugs_Data.txt");
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

			// 初始化column_name list
			List<String> columns = new ArrayList<String>();
			for (String string : colname) {
				columns.add(string);
			}
			// 初始化 result array
			String[] res_arr = new String[columns.size()];
			for (int i = 0; i < res_arr.length; i++) {
				res_arr[i] = "\t";
			}

			Element tbody = tbodys.get(2);
			Elements nobrs = tbody.getElementsByTag("nobr");
			for (Element nobr : nobrs) {
				String nobr_str = nobr.ownText();

				// Drug
				if (nobr_str.equals("Drug")) {
					String drugs = "";
					Element other_tr = nobr.parent().nextElementSibling();
					Elements div_tags = other_tr.getElementsByTag("div");

					for (Element div : div_tags) {
						// 如果div包含":"，则他的下一个兄弟中的a标签里的元素都是对应的Other DBs ID
						String d_text = div.ownText().trim();
						if (d_text.length() > 0 && (d_text.contains("DG") || d_text.contains("DR"))) {
							String[] texts = div.html().split("\\[|\\]");
							for (String t : texts) {
								t = t.replace("\n", " ").replace("<br />", "");
								// System.out.println(t);
								if (t.length() > 0 && !(" ").equals(t) && !t.contains("href")) {
									drugs += ("|" + t.trim() + "%");
									continue;
								}
								// a links
								if (t.contains("href")) {
									// t = t.replace(" ", "");
									// System.out.println(t);
									if (t.contains("DG:")) {
										for (int i = 0; i < t.length();) {
											t = t.substring(i);
											drugs += ("DG:" + t.substring(t.indexOf(">") + 1, t.indexOf("</a>")) + "%");
											i = t.indexOf("</a>") + 4;
										}
									} else {
										for (int i = 0; i < t.length();) {
											t = t.substring(i);
											drugs += ("DR:" + t.substring(t.indexOf(">") + 1, t.indexOf("</a>")) + "%");
											i = t.indexOf("</a>") + 4;
										}
									}
								}
							}
							drugs += ";";
						}
					}
					res_arr[columns.indexOf("Drug")] += drugs;
				}
			}

			for (int j = 1; j < res_arr.length; j++) {
				result += res_arr[j];
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return result;
	}

	public void getDisease(String writepath) throws IOException {
		FileOutputStream outputStream = new FileOutputStream(new File(writepath));
		// int id = 1, max = 1437;
		int id = 1, max = 1716;

		String head = "";
		for (int i = 0; i < colname.length; i++) {
			head += (colname[i] + "\t");
		}
		head = "#" + head.substring(0, head.length() - 1) + "\n";
		// 表头
		outputStream.write(head.getBytes());
		// 注意flush
		outputStream.flush();

		DecimalFormat df = new DecimalFormat("00000");
		String url = "";
		while (id < max) {
			url = "http://www.kegg.jp/dbget-bin/www_bget?ds:H" + df.format(id);
			System.out.println(url);
			String result = getContentPr(url);
			if (result != null && result != "") {
				outputStream.write(("H" + df.format(id) + result + "\n").getBytes());
				// 注意flush
				outputStream.flush();
			}
			id++;
		}
		outputStream.flush();
		outputStream.close();
	}
}
