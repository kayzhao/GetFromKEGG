package DataFromKEGG;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * MESH to OMIM By KEGG Mapping
 */
public class GetFromKEGG {
	public static void main(String[] args) {
		GetFromKEGG kegg = new GetFromKEGG();
		try {
			// kegg.getDisease("Diseases_Mesh_Check.txt");
			kegg.readMeshIDs("Diseases_Mesh_Check.txt",
					"Diseases_OMIM_Check.txt");
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
	public String getOMIMs(String meshId) {
		Document doc;
		String url = "http://ctdbase.org/detail.go?type=disease&acc=MESH%3A"
				+ meshId;
		String omim = "";
		try {
			doc = Jsoup.connect(url).timeout(10000).get();
			Elements ths = doc.getElementsByTag("th");
			for (Element th : ths) {
				String th_str = th.html().trim();
				if (th_str.startsWith("OMIM")) {
					Element td = th.nextElementSibling();
					Elements a_tags = td.getElementsByTag("a");
					for (Element a : a_tags) {
						omim += (a.ownText() + "\t");
					}
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return omim;
	}

	/**
	 * 
	 * @param url
	 * @param output
	 * @return
	 */
	public String getContentPr(String url) {
		Document doc;
		String mesh = "";
		try {
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
						if ("MeSH: ".equals(div.ownText())) {
							mesh = "MeSH:\t";
							for (Element a : div.nextElementSibling()
									.getElementsByTag("a")) {
								mesh += (a.ownText() + "\t");
							}
						}
						if ("OMIM: ".equals(div.ownText().trim())) {
							// System.out.println(div.ownText());
							if ("".equals(mesh)) {
								mesh += "OMIM:\t";
							} else {
								mesh += "\n\t\tOMIM:\t";
							}

							for (Element a : div.nextElementSibling()
									.getElementsByTag("a")) {
								mesh += (a.ownText() + "\t");
							}
						}
					}
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return mesh;
	}

	public void readMeshIDs(String readPath, String writePath)
			throws IOException {
		FileInputStream inputStream = new FileInputStream(new File(readPath));
		BufferedReader br = new BufferedReader(new InputStreamReader(
				inputStream));

		FileOutputStream outputStream = new FileOutputStream(
				new File(writePath));
		String str = "";
		while ((str = br.readLine()) != null) {
			System.out.println(str);
			String[] lines = str.trim().split("\t");
			if (lines.length > 2 && lines[1].startsWith("Me")
					&& lines[0].startsWith("H")) {
				System.out.println(str);
				String omims = "";
				for (int i = 2; i < lines.length; i++) {
					String omim_ = getOMIMs(lines[i].trim());
					if ("".equals(omim_)) {
						omims += lines[i].trim() + "\t";
					} else {
						omims += omim_;
					}
					System.out.println(lines[0] + "\t" + omim_);
				}
				outputStream
						.write((lines[0] + "\t" + "MeSH:\t" + omims + "\t\n")
								.getBytes());
			} else {
				outputStream.write((str + "\n").getBytes());
			}
		}
		outputStream.flush();
		outputStream.close();
	}

	public void getDisease(String writepath) throws IOException {
		FileOutputStream outputStream = new FileOutputStream(
				new File(writepath));
		int id = 1, max = 1412;

		DecimalFormat df = new DecimalFormat("00000");
		String url = "";
		while (id < max) {
			url = "http://www.kegg.jp/dbget-bin/www_bget?ds:H" + df.format(id);
			System.out.println(url);
			outputStream
					.write(("H" + df.format(id) + "\t" + getContentPr(url) + "\n")
							.getBytes());
			outputStream.flush();
			id++;
		}
		// url = "http://www.kegg.jp/dbget-bin/www_bget?ds:H" + df.format(202);
		// outputStream
		// .write(("H" + df.format(id) + "\t" + getContentPr(url) + "\r\n")
		// .getBytes());
		outputStream.flush();
		outputStream.close();
	}
}
