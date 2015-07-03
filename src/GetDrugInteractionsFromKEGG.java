import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GetDrugInteractionsFromKEGG {
	public static void main(String[] args) {
		GetDrugInteractionsFromKEGG kegg = new GetDrugInteractionsFromKEGG();
		try {
			kegg.getData("Kegg_DrugInteractions.txt");
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
				return null;
			}
			Elements a_tags = doc.select("a[href]");
			for (Element a : a_tags) {
				if (a.text().startsWith("D")) {
					result += (a.text() + ",");
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
		int id = 1, max = 10628;

		DecimalFormat df = new DecimalFormat("00000");
		String url = "";
		while (id < max) {
			url = "http://www.kegg.jp/kegg-bin/ddi_list?drug=D" + df.format(id);
			System.out.println(url);
			outputStream.write((getContentPr(url) + "\n").getBytes());
			id++;
		}
		outputStream.flush();
		outputStream.close();
	}
}
