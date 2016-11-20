package DataFromKEGG;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class GetKeggSimcomp {
	public static void main(String[] args) {
		getSimList("/simcomp/C00022", "sim.txt");
	}

	public static List<String> getSimList(String querydrugs, String writepath) {
		FileOutputStream outputStream;
		List<String> simlist = new ArrayList<String>();
		try {
			outputStream = new FileOutputStream(new File(writepath));
			String url = "http://rest.genome.jp" + querydrugs;
			System.out.println(url);
			Document doc = Jsoup.connect(url).timeout(100000).get();
			Elements body = doc.getElementsByTag("body");
			if (body.size() != 0) {
				String bodyString = body.get(0).ownText();
				String[] simarray = bodyString.split(" ");
				String sim = "";
				for (int i = 0; i < simarray.length; i++) {
					if (i % 2 == 0)
						sim = simarray[i] + "\t";
					else {
						sim += simarray[i] + "\n";
						outputStream.write(sim.getBytes());
						outputStream.flush();
					}
				}
			}
			// close
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return simlist;
	}
}
