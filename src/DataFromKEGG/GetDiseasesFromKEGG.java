package DataFromKEGG;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
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
 * Gene<br>
 * Drug<br>
 * Env factor<br>
 * Comment<br>
 * Marker<br>
 * Reference<br>
 * Other DBs
 */
public class GetDiseasesFromKEGG {
	/**
	 * column name
	 */
	String[] colname = { "ID", "Name", "Description", "Category", "Gene", "Drug", "Envfactor", "Carcinogen", "Comment",
			"Marker", "Reference", "Other DBs" };

	public static void main(String[] args) {
		GetDiseasesFromKEGG kegg = new GetDiseasesFromKEGG();
		try {
			/**
			 * 测试一条数据
			 */
			String id = "H00001";
			String url = "http://www.kegg.jp/dbget-bin/www_bget?ds:" + id;
			System.out.println(id + "\t" + kegg.getContentPr(url));
			/**
			 * 所有数据
			 */
			kegg.getDisease("Kegg_Diseases_All_Data_" + System.currentTimeMillis() + ".txt");
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

				// 获取name
				if (nobr_str.equals("Name")) {
					Element other_tr = nobr.parent().nextElementSibling();
					Elements div_tags = other_tr.getElementsByTag("div");
					String[] names = div_tags.get(1).ownText().split("<br>");
					for (String name : names) {
						res_arr[columns.indexOf("Name")] += name;
					}
				}

				// 获取Description
				if (nobr_str.equals("Description")) {
					Element other_tr = nobr.parent().nextElementSibling();
					Elements div_tags = other_tr.getElementsByTag("div");
					String[] names = div_tags.get(0).ownText().split("<br>");
					res_arr[columns.indexOf("Description")] += names[0];
				}

				// Category
				if (nobr_str.equals("Category")) {
					Element other_tr = nobr.parent().nextElementSibling();
					Elements div_tags = other_tr.getElementsByTag("div");
					String[] names = div_tags.get(0).ownText().split("<br>");
					res_arr[columns.indexOf("Category")] += names[0];
				}

				// Gene
				if (nobr_str.equals("Gene")) {
					String gene = "";
					Element other_tr = nobr.parent().parent();
					Elements div_tags = other_tr.getElementsByTag("div");
					for (Element div : div_tags) {
						// 如果div包含":"，则他的下一个兄弟中的a标签里的元素都是对应的Other DBs ID
						String d_text = div.ownText().trim();
						if (d_text.length() > 0 && (d_text.contains("HSA") || d_text.contains("KO"))) {
							String[] texts = div.html().split("\\[|\\]");
							for (String t : texts) {
								t = t.replace("\n", " ").replace("<br />", "");
								// System.out.println(t);
								// gene
								if (t.length() > 0 && !(" ").equals(t) && !t.contains("href")) {
									gene += ("|" + t.trim() + "%");
									continue;
								}
								// a links
								if (t.contains("href")) {
									// t = t.replace(" ", "");
									// System.out.println(t);
									String type = t.substring(0, t.indexOf(':')) + ":";
									// System.out.println(type);
									for (int i = 0; i < t.length();) {
										t = t.substring(i);
										if (t.contains("<a"))
											gene += (type + t.substring(t.indexOf(">") + 1, t.indexOf("</a>")) + "%");
										else
											break;
										i = t.indexOf("</a>") + 4;
									}
								}
							}
						}
					}

					res_arr[columns.indexOf("Gene")] += gene;
				}

				// Env factor
				if (nobr_str.equals("Env factor")) {
					String envfs = "";
					Element other_tr = nobr.parent().nextElementSibling();
					Elements div_tags = other_tr.getElementsByTag("div");

					for (Element div : div_tags) {
						// 如果div包含":"，则他的下一个兄弟中的a标签里的元素都是对应的Other DBs ID
						String d_text = div.ownText().trim();
						if (d_text.length() > 0 && (d_text.contains("<a") || d_text.contains("</a>"))) {
							String[] texts = div.html().split("\\[|\\]");
							for (String t : texts) {
								t = t.replace("\n", " ").replace("<br />", "");
								// System.out.println(t);
								if (t.length() > 0 && !(" ").equals(t) && !t.contains("href")) {
									envfs += ("|" + t.trim() + "%");
									continue;
								}

								// a links
								if (t.contains("href")) {
									// t = t.replace(" ", "");
									// System.out.println(t);
									String type = t.substring(0, t.indexOf(':')) + ":";
									for (int i = 0; i < t.length();) {
										t = t.substring(i);
										if (t.contains("<a"))
											envfs += (type + t.substring(t.indexOf(">") + 1, t.indexOf("</a>")) + "%");
										else
											break;
										i = t.indexOf("</a>") + 4;
									}
								}
							}
						}
					}
					res_arr[columns.indexOf("Envfactor")] += envfs;
				}

				// Carcinogen
				if (nobr_str.equals("Carcinogen")) {
					Element other_tr = nobr.parent().nextElementSibling();
					Elements div_tags = other_tr.getElementsByTag("div");
					String[] names = div_tags.get(0).ownText().split("<br>");
					for (String name : names) {
						res_arr[columns.indexOf("Carcinogen")] += name;
					}
				}

				// Comment
				if (nobr_str.equals("Comment")) {
					Element other_tr = nobr.parent().nextElementSibling();
					Elements div_tags = other_tr.getElementsByTag("div");
					String[] names = div_tags.get(0).ownText().split("<br>");
					res_arr[columns.indexOf("Comment")] += names[0];
				}

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
									String type = t.substring(0, t.indexOf(':')) + ":";
									for (int i = 0; i < t.length();) {
										t = t.substring(i);
										if (t.contains("<a"))
											drugs += (type + t.substring(t.indexOf(">") + 1, t.indexOf("</a>")) + "%");
										else
											break;
										i = t.indexOf("</a>") + 4;
									}
								}
							}
						}
					}
					res_arr[columns.indexOf("Drug")] += drugs;
				}

				// Marker
				if (nobr_str.equals("Marker")) {
					String marker = "";
					// result += "\t";
					Element other_tr = nobr.parent().parent();
					Elements div_tags = other_tr.getElementsByTag("div");
					for (Element div : div_tags) {
						// 如果div包含":"，则他的下一个兄弟中的a标签里的元素都是对应的Other DBs ID
						String d_text = div.ownText().trim();
						if (d_text.length() > 0 && (d_text.contains("HSA") || d_text.contains("KO"))) {
							String[] texts = div.html().split("\\[|\\]");
							for (String t : texts) {
								t = t.replace("\n", " ").replace("<br />", "");
								// System.out.println(t);
								// gene
								if (t.length() > 0 && !(" ").equals(t) && !t.contains("href")) {
									marker += ("|" + t.trim() + "%");
									continue;
								}
								// a links
								if (t.contains("href")) {
									// t = t.replace(" ", "");
									// System.out.println(t);
									String type = t.substring(0, t.indexOf(':')) + ":";
									for (int i = 0; i < t.length();) {
										t = t.substring(i);
										if (t.contains("<a"))
											marker += (type + t.substring(t.indexOf(">") + 1, t.indexOf("</a>")) + "%");
										else
											break;
										i = t.indexOf("</a>") + 4;
									}
								}
							}
						}
					}
					res_arr[columns.indexOf("Marker")] += marker;
				}

				// Other DBs
				if (nobr_str.equals("Other DBs")) {
					String xrefs = "";
					Element other_tr = nobr.parent().parent();
					Elements div_tags = other_tr.getElementsByTag("div");
					for (Element div : div_tags) {
						// 如果div包含":"，则他的下一个兄弟中的a标签里的元素都是对应的Other DBs ID
						if (div.ownText().trim().contains(":")) {
							if (div.ownText().trim().startsWith("ICD")) {
								xrefs += div.ownText().trim();
								for (Element a : div.nextElementSibling().getElementsByTag("a")) {
									for (String a_text : a.ownText().split(" ")) {
										xrefs += (a_text + ",");
									}
								}
							} else {
								xrefs += div.ownText().trim();
								for (Element a : div.nextElementSibling().getElementsByTag("a")) {
									xrefs += (a.ownText() + ",");
								}
							}
						}
					}
					res_arr[columns.indexOf("Other DBs")] += xrefs;
				}

				// Reference
				if (nobr_str.equals("Reference")) {
					String ref = "";
					// result += "\t";
					Element other_tr = nobr.parent().nextElementSibling();
					Elements a_tags = other_tr.getElementsByTag("a");
					if (a_tags != null) {
						// result += ",";// 与前面的串分隔开，Cas number不含a标签
						for (Element a : a_tags) {
							ref += ("PMID:" + a.ownText() + ",");
						}
					}
					res_arr[columns.indexOf("Reference")] += ref;
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
		// int id = 1, max = 1710;
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
