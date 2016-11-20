package Utils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Random;

public class RandomString {
	public static void main(String[] args) {
		try {
			generateTable();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 产生一个随机的字符串,长度由length参数而定
	 */
	public static String RandomString(int length) {
		String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random random = new Random();
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int num = random.nextInt(62);
			buf.append(str.charAt(num));
		}
		return buf.toString();
	}

	/**
	 * 数据处理，生成ID和密码(6位随机字符串)
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void generateTable() throws FileNotFoundException,
			IOException {
		InputStream dbk_in = new FileInputStream(new File("pr.txt"));
		OutputStream ou = new FileOutputStream(new File("RandomString.txt"));

		// br , bw
		BufferedReader br = new BufferedReader(new InputStreamReader(dbk_in));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ou));

		String readline = "";
		// ID，RandomString(6)密码
		int id = 999001;
		while ((readline = br.readLine()) != null) {
			readline = readline.trim();
			bw.write(readline + "\t" + Integer.toString(id) + "\t"
					+ RandomString(6));
			bw.newLine();
			bw.flush();
			id++;
		}
		System.out.println("success");
		br.close();
		bw.close();
	}
}
