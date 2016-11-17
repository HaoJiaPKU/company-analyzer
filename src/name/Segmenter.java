package name;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import config.FilePath;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import utils.FileUtil;
import utils.UrlUtil;

public class Segmenter {
	
	public static CRFClassifier<CoreLabel> segmenter;
	public static final String StopSigns = "[\\p{P}~$`^=|<>～｀＄＾＋＝｜＜＞￥× \\s|\t|\r|\n]+";
	public static final String CityNameFile = FilePath.DataDir + "/city-name.txt";
	public static final String CompanyType [] = {"有限责任公司", "股份有限公司", "有限合伙"};
	public static ArrayList<String> cityName = new ArrayList<String> ();
	
	public static void loadSegmenter()
	{	
		String basedir = "stanford-segmenter-2015-12-09/data";
		Properties props = new Properties();
		props.setProperty("sighanCorporaDict", basedir);
		//props.setProperty("NormalizationTable", "data/norm.simp.utf8");
		//props.setProperty("normTableEncoding", "UTF-8");
		//below is needed because CTBSegDocumentIteratorFactory accesses it
		props.setProperty("serDictionary", basedir + "/dict-chris6.ser.gz");
		//props.setProperty("testFile", args[0]);
		props.setProperty("inputEncoding", "UTF-8");
		props.setProperty("sighanPostProcessing", "true");

		segmenter = new CRFClassifier<CoreLabel>(props);
		segmenter.loadClassifierNoExceptions(basedir + "/ctb.gz", props);
		//segmenter.classifyAndWriteAnswers(args[0]);
		//System.out.println(segmenter.classifyToString("今天天气不错啊"));
	}
	
	public static void loadCityName()
	{
		InputStreamReader isr;
		try {
			isr = new InputStreamReader(new FileInputStream(CityNameFile));
			BufferedReader reader = new BufferedReader(isr);
			String line = null;
			try {
				while((line = reader.readLine()) != null)
					cityName.add(line.trim());
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean isCompanyType(String str) {
		for (int i = 0; i < CompanyType.length; i ++) {
			if (CompanyType[i].contains(str)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isCityName(String str) {
		for (int i = 0; i < cityName.size(); i ++) {
			if (cityName.get(i).contains(str)) {
				return true;
			}
		}
		return false;
	}
	
	public static int calculateSearchCompanyNum(String str, String targetStr) {
		int ret = 0;
		try {
			String content = UrlUtil.getHTML(str);
			Document doc = Jsoup.parse(content);
			Elements divs = doc.select(".result");
			for (Element div : divs) {
//				System.out.println(div.select("h3").first().text().toString() + " " + target);
//				System.out.println(div.select("h3").first().text().toString().contains(target));
				String name = div.select("h3").first().text().toString()
						.replaceAll(StopSigns, "").trim();
				String tokens [] = segmenter.classifyToString(name).split(" +");
				for (int i = 0; i < tokens.length; i ++) {
					if (isCityName(tokens[i]) && name.indexOf(tokens[i]) != -1) {
						name = name.substring(0, name.indexOf(tokens[i]))
								+ name.substring(name.indexOf(tokens[i]) + tokens[i].length());
					}
				}
				if (name.contains(targetStr)) {
					ret ++;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	public static CompanyObject partition(String companyName) throws IOException {
		CompanyObject co = new CompanyObject ();
		
		try {
		companyName = companyName.replaceAll(StopSigns, "").trim();		
//		System.out.println(companyName);
		
		String str [] = segmenter.classifyToString(companyName).split(" +");
		boolean used [] = new boolean [str.length];
		for (int i = 0; i < used.length; i ++) {
			if (str[i] != null && str[i].length() > 0 && !str[i].equals("")) {
				used[i] = false;
			} else {
				used[i] = true;
			}
		}
		
//		for (int i = 0; i < str.length; i ++) {
//			if (!used[i])
//				System.out.print("*" + str[i] + " ");
//		}
//		System.out.println(str.length);
		
		int lastIndex = -1;		
		for (int i = 0; i < str.length; i ++) {
			str[i] = str[i].trim();
			if (str[i].length() > 1 && !used[i]) {
				if (isCompanyType(str[i])) {
					co.type += str[i];
					used[i] = true;
					lastIndex = i;
				}
			}
		}
		
		if (lastIndex != -1) {
			for (int i = lastIndex + 1; i < str.length; i ++) {
				used[i] = true;
			}
			companyName = companyName.substring(0,
					companyName.indexOf(str[lastIndex]) + str[lastIndex].length());
		}
		else {
			co.type = str[str.length - 1];
			used[str.length - 1] = true;
		}
		
		for (int i = 0; i < str.length; i ++) {
			str[i] = str[i].trim();
			if (str[i].length() > 1 && !used[i]) {
				if (isCityName(str[i])) {
					companyName = companyName.substring(0, companyName.indexOf(str[i]))
							+ companyName.substring(companyName.indexOf(str[i]) + str[i].length());
					co.city += str[i];
					used[i] = true;
					lastIndex = i;
				}
			}
		}
		
		int pre = 0, cur = 1;
		while (pre < str.length - 1 && cur < str.length) {
			while (pre < str.length - 1 && cur < str.length
					&& !used[pre] && !used[cur]
					&& str[cur].length() == 1) {
				str[pre] += str[cur];
				used[cur] = true;
				cur ++;
			}
			pre = cur;
			cur ++;
		}
		
//		for (int i = 0; i < str.length; i ++) {
//			System.out.print(str[i] + " " + used[i] + " ");
//		}
//		System.out.println(str.length);
		
		pre = str.length - 1;
		cur = pre - 1;
		while (pre >= 1 && cur >= 0) {
			while (pre >= 1 && cur >= 0
					&& !used[pre] && !used[cur]
					&& str[cur].length() == 1) {
				str[pre] = str[cur] + str[pre];
				used[cur] = true;
				cur --;
			}
			pre = cur;
			cur --;
		}
		
		if (!Debug) {
			try {
				for (int i = 0; i < str.length; i ++) {
					FileUtil.t6.write(str[i] + "	");
				}
				FileUtil.t6.newLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (Debug) {
			for (int i = 0; i < str.length; i ++) {
				System.out.print(str[i] + " " + used[i] + " ");
			}
			System.out.println(str.length);
		}
		
		boolean fisrtName = false, companyFind = false;
		int companyCounter = 0;
		for (int i = 0; i < str.length; i ++) {
			if (!used[i] && str[i].length() > 0) {
				//首个词直接作为公司名
				if (!fisrtName) {
					fisrtName = true;
					co.zihao = str[i];
					companyCounter = calculateSearchCompanyNum(co.zihao, co.type);
					used[i] = true;
					lastIndex = i;
					if (calculateSearchCompanyNum(co.zihao, companyName.substring(companyName.indexOf(co.zihao))) > 0) {
						if (Debug) {
							System.out.println("第一个单词找到了公司名称");
						}
						companyFind = true;
					}
				}
				else {
					int temp = calculateSearchCompanyNum(str[i], co.type);
					//后面的词比前面的词得到更好的搜索结果
					if (Debug) {
						System.out.println(co.zihao + str[i] + " " + co.type);
						System.out.println(calculateSearchCompanyNum(co.zihao + str[i], co.type));
					}
					if (!companyFind && temp >= companyCounter && temp > 0) {
						co.zihao += str[i];
						companyCounter = calculateSearchCompanyNum(co.zihao, co.type);
						used[i] = true;
						lastIndex = i;
						if (calculateSearchCompanyNum(co.zihao, companyName.substring(companyName.indexOf(co.zihao))) > 0) {
							companyFind = true;
						}
					//词的组合得到更好的搜索结果
					} else if (!companyFind && companyCounter > 0
							&& calculateSearchCompanyNum(co.zihao + str[i], co.type) > companyCounter) {
						co.zihao += str[i];
						companyCounter = calculateSearchCompanyNum(co.zihao, co.type);
						used[i] = true;
						lastIndex = i;
						if (calculateSearchCompanyNum(co.zihao, companyName.substring(companyName.indexOf(co.zihao))) > 0) {
							companyFind = true;
						}
					//增加一个词可以得到搜索结果
					} else if (!companyFind
							&& calculateSearchCompanyNum(co.zihao + str[i], companyName.substring(companyName.indexOf(co.zihao))) > 0) {
						co.zihao += str[i];
						used[i] = true;
						lastIndex = i;
						companyFind = true;
					//加入单词，直到找到结果
//					} else if (!companyFind
//							&& calculateSearchCompanyNum(co.zihao + str[i], co.zihao + str[i]) > 0) {
//						co.zihao += str[i];
//						used[i] = true;
//						lastIndex = i;
//						companyFind = true;
					} else {
						break;
					}
				}
			}
		}
		
		for (int i = 0; i < str.length; i ++) {
			if (!used[i] && str[i].length() > 0) {
				co.industry += str[i];
			}
		}
		if (Debug) {
			System.out.println("字号: " + co.zihao + "\n行业: " + co.industry);
		}
		if (co.industry.length() == 0 && lastIndex != -1) {
			co.industry = str[lastIndex];
			if (Debug) {
				System.out.println("字号: " + co.zihao + "\n行业: " + co.industry);
			}
			co.zihao = co.zihao.substring(0, co.zihao.indexOf(co.industry));
		}
		
		} catch (IndexOutOfBoundsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return co;
	}
	
	public static final boolean Debug = false;
	
	public static void main(String [] args) throws IOException {
		loadSegmenter();
		loadCityName();
		
//		String companyName = new String ();
//		while(!companyName.equals("0")) {
//			@SuppressWarnings("resource")
//			Scanner scanner = new Scanner(System.in);
//			companyName = scanner.next();
//			CompanyObject co = partition(companyName);
//			System.out.println(co.city);
//			System.out.println(co.zihao);
//			System.out.println(co.industry);
//			System.out.println(co.type);
//		}
		
		FileUtil.initLoadCompanyName();
		FileUtil.initOutputCompanyName();
		FileUtil.initOutputCompanyTokens();
		
		int counter = 0;
		
		if (!Debug) {
			String line = new String ();
			try {
				while ((line = FileUtil.reader.readLine()) != null) {
					counter ++;
					if (counter < 3291) {
						 continue;
					}
					line = line.trim();
					//System.out.println("***" + line);
					CompanyObject co = partition(line);
					System.out.println(co.city + "	" + co.zihao
							+ "	" + co.industry + "	" + co.type);
					FileUtil.t3.write(co.city + "	");
					FileUtil.t3.write(co.zihao + "	");
					FileUtil.t3.write(co.industry + "	");
					FileUtil.t3.write(co.type + "	");
					FileUtil.t3.newLine();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		FileUtil.closeLoadCompanyName();
		FileUtil.closeOutputCompanyName();
		FileUtil.closeOutputCompanyTokens();
		
		if (Debug) {
			String cns [] = {"北京北方天宇建筑装饰设计院有限公司"};
			
			for (int i = 0; i < cns.length; i ++) {
				CompanyObject co = partition(cns[i]);
				System.out.println(cns[i] + "***********");
				System.out.println(co.city);
				System.out.println(co.zihao);
				System.out.println(co.industry);
				System.out.println(co.type);
				System.out.println();
			}
		}
	}	
}

class CompanyObject {
	String city = new String ();
	String zihao = new String ();
	String industry = new String ();
	String type = new String ();
	public CompanyObject () {}
}
