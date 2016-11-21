package DataProcess;

import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Disease-Drug Interations( KEGG to DrugBank)
 */
public class OMIM_to_DrugBank {
	public static void main(String[] args) {
		OMIM_to_DrugBank kegg = new OMIM_to_DrugBank();
		try {
			// kegg.readDisease2DrugIDs("disease/disease_drug_dbs",
			// "disease/disease_drug_dbs_");

			// kegg.readDrug2DBs("drug/drug_dbs", "drug/drug_dbs_");

			// kegg.getOMIM2DrugBanks();

			kegg.getOMIM2DB("disease/DrugBanks_OMIMs.txt", "disease/OMIM_DrugBank.txt");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void readDisease2DrugIDs(String readPath, String writePath)
			throws IOException {
		FileInputStream inputStream = new FileInputStream(new File(readPath));
		BufferedReader br = new BufferedReader(new InputStreamReader(
				inputStream));
		FileOutputStream outputStream = new FileOutputStream(
				new File(writePath));
		String line = "";
		while ((line = br.readLine()) != null) {
			System.out.println(line);
			if (!line.contains("OMIM"))
				continue;

			// OMIM
			String[] lines = line.trim().split("\t");
			int index = lines[2].indexOf("OMIM:");
			int index_next = lines[2].indexOf((int) ';',
					index + "OMIM:".length());
			String omim = "";
			if (index_next > 0) {
				omim = lines[2].substring(index + "OMIM:".length(), index_next);
			} else {
				omim = lines[2].substring(index + "OMIM:".length(),
						lines[2].length() - 1);
			}
			outputStream
					.write((lines[0] + "\t" + lines[1] + "\t" + omim.trim() + "\n")
							.getBytes());
		}
		br.close();
		outputStream.flush();
		outputStream.close();
	}

	public void readDrug2DBs(String readPath, String writePath)
			throws IOException {
		FileInputStream inputStream = new FileInputStream(new File(readPath));
		BufferedReader br = new BufferedReader(new InputStreamReader(
				inputStream));
		FileOutputStream outputStream = new FileOutputStream(
				new File(writePath));
		String line = "";
		while ((line = br.readLine()) != null) {
			System.out.println(line);
			if (!line.contains("DrugBank"))
				continue;

			// DrugBank
			String[] lines = line.trim().split("\t");
			int index = lines[1].indexOf("DrugBank");
			int s = index + "DrugBank: ,".length();
			int e = s + "DB02379".length();
			String drugbank = lines[1].substring(s, e);
			outputStream.write((lines[0] + "\t" + drugbank.trim() + "\n")
					.getBytes());
		}
		br.close();
		outputStream.flush();
		outputStream.close();
	}

	@SuppressWarnings("resource")
	public void getOMIM2DrugBanks() throws IOException {
		FileOutputStream outputStream = new FileOutputStream(new File(
				"disease/OMIM_DrugBank.txt"));

		BufferedReader br = new BufferedReader(new FileReader("drug/drug_dbs_"));
		// kegg drug to drugbank drug
		HashMap<String, String> D_DB_map = new HashMap<String, String>();
		String line = "";
		while ((line = br.readLine()) != null) {
			String[] lines = line.trim().split("\t");
			D_DB_map.put(lines[0], lines[1]);
		}

		br = new BufferedReader(new FileReader("drug/drug_group"));
		// kegg druggroup to drug set
		HashMap<String, Set<String>> DG_D_map = new HashMap<String, Set<String>>();
		while ((line = br.readLine()) != null) {
			String[] lines = line.trim().split("\t");
			Set<String> dSet = new HashSet<String>();
			for (String d : lines[1].split(",")) {
				dSet.add(d);
			}
			DG_D_map.put(lines[0], dSet);
		}

		br = new BufferedReader(new FileReader("disease/disease_drug_dbs_"));

		while ((line = br.readLine()) != null) {
			String[] lines = line.trim().split("\t");
			Set<String> dSet = new HashSet<String>();
			for (String d : lines[1].split(",")) {
				if (d.startsWith("DG") && DG_D_map.get(d) != null) {
					dSet.addAll(DG_D_map.get(d));
				} else {
					dSet.add(d);
				}
			}
			String dbs = "";
			for (String d : dSet) {
				if (D_DB_map.get(d) != null)
					dbs += (D_DB_map.get(d) + ",");
			}
			if (dbs != "") {
				outputStream.write((dbs + "\t" + lines[2] + "\n").getBytes());
				outputStream.flush();
			}
		}

		br.close();
		outputStream.flush();
		outputStream.close();
	}

	public void getOMIM2DB(String readPath, String writePath)
			throws IOException {
		FileInputStream inputStream = new FileInputStream(new File(readPath));
		BufferedReader br = new BufferedReader(new InputStreamReader(
				inputStream));
		FileOutputStream outputStream = new FileOutputStream(
				new File(writePath));
		String line = "";
		while ((line = br.readLine()) != null) {
			System.out.println(line);
			String[] lines = line.split("\t");

			String[] dbs = lines[0].split(",");
			String[] omims = lines[1].substring(1).split(",");
			for (int i = 0; i < omims.length; i++) {
				for (int j = 0; j < dbs.length; j++) {
					outputStream
							.write((omims[i].trim() + "\t" + dbs[j].trim() + "\n")
									.getBytes());
					outputStream.flush();
				}
			}
		}
		br.close();
		outputStream.flush();
		outputStream.close();
	}
}