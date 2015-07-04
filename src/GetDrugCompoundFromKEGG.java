import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GetDrugCompoundFromKEGG {
	public static void main(String[] args) {
		GetDrugCompoundFromKEGG kegg = new GetDrugCompoundFromKEGG();
		try {
			kegg.getData("Kegg_Drug_Compound.txt");
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
			boolean name_flag = nobrs.text().contains("Name");
			boolean formula_flag = nobrs.text().contains("Formula");
			boolean mass_flag = nobrs.text().contains("Exact mass");
			boolean weight_flag = nobrs.text().contains("Mol weight");
			boolean remark_flag = nobrs.text().contains("Remark");

			for (Element nobr : nobrs) {
				String nobr_str = nobr.ownText();
				// 获取drug name
				if (nobr_str.equals("Name")) {
					Element other_tr = nobr.parent().nextElementSibling();
					Elements div_tags = other_tr.getElementsByTag("div");
					String[] arr = div_tags.get(1).ownText().split("<br>");
					if (arr.length > 0) {
						for (String name : arr) {
							result += (name + ";");
						}
					} else {
						result += "-";
					}
				}

				// 获取Formula
				if (nobr_str.equals("Formula")) {
					result += "\t";
					Element other_tr = nobr.parent().nextElementSibling();
					Elements div_tags = other_tr.getElementsByTag("div");
					String[] arr = div_tags.get(0).ownText().split("<br>");
					if (arr.length > 0) {
						result += arr[0];
					} else {
						result += "-";
					}
				}

				// 获取Exact mass
				if (nobr_str.equals("Exact mass")) {
					result += "\t";
					Element other_tr = nobr.parent().nextElementSibling();
					Elements div_tags = other_tr.getElementsByTag("div");
					String[] arr = div_tags.get(0).ownText().split("<br>");
					if (arr.length > 0) {
						result += arr[0];
					} else {
						result += "-";
					}
				}

				// 获取Mol weight
				if (nobr_str.equals("Mol weight")) {
					result += "\t";
					Element other_tr = nobr.parent().nextElementSibling();
					Elements div_tags = other_tr.getElementsByTag("div");
					String[] arr = div_tags.get(0).ownText().split("<br>");
					if (arr.length > 0) {
						result += arr[0];
					} else {
						result += "-";
					}
				}

				// Remark
				if (nobr_str.equals("Remark")) {
					result += "\t";
					Element other_tr = nobr.parent().nextElementSibling();
					Elements a_tags = other_tr.getElementsByTag("a");
					// String[] arr =
					// div_tags.get(0).ownText().split("<br>");
					if (a_tags.size() > 0) {
						for (Element element : a_tags) {
							result += (element.text() + ";");
						}
					} else {
						result += "-";
					}
				}

				// Other DBs
				if (nobr_str.equals("Other DBs")) {
					// 如果Name这一行不存在就用-替代
					if (!name_flag) {
						result += "\t-";
					}
					// 如果formula这一行不存在就用-替代
					if (!formula_flag) {
						result += "\t-";
					}
					// 如果extra mass这一行不存在就用-替代
					if (!mass_flag) {
						result += "\t-";
					}
					// 如果mol weight这一行不存在就用-替代
					if (!weight_flag) {
						result += "\t-";
					}
					// 如果Remark这一行不存在就用-替代
					if (!remark_flag) {
						result += "\t-";
					}
					result += "\t";
					Element other_tr = nobr.parent().parent();
					Elements div_tags = other_tr.getElementsByTag("div");
					for (Element div : div_tags) {
						// 如果div包含":"，则他的下一个兄弟中的a标签里的元素都是对应的Other DBs ID
						if (div.ownText().trim().contains(":")) {
							result += div.ownText().trim();
							Elements ids = div.nextElementSibling()
									.getElementsByTag("a");
							if (ids.size() == 0l) {
								result += div.nextElementSibling().text()
										.trim();
							} else {
								for (Element a : ids) {
									result += (a.ownText() + ",");
								}
							}
							result += ";";
						}
					}
				}

			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		System.out.println(result);
		return result;
	}

	public void getData(String writepath) throws IOException {
		FileOutputStream outputStream = new FileOutputStream(
				new File(writepath));
		int id = 1, max = 20952;

		DecimalFormat df = new DecimalFormat("00000");
		String url = "";
		while (id < max) {
			url = "http://www.kegg.jp/dbget-bin/www_bget?C" + df.format(id);
			System.out.println(url);
			outputStream
					.write(("C" + df.format(id) + "\t" + getContentPr(url) + "\n")
							.getBytes());
			id++;
		}
		outputStream.flush();
		outputStream.close();
	}
}
