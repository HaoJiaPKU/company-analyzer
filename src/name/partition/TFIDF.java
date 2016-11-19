package name.partition;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import config.FilePath;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import objects.CompanyObject;
import utils.FileInput;
import utils.FileOutput;
import utils.SegmenterUtil;
import utils.UrlUtil;

public class TFIDF {
	
	public static HashMap<String, Double> IDFDict = new HashMap<String, Double> ();
	public static final int TestNumber = 0x7fffffff;
	public static final boolean Debug = false;
	
	public static CompanyObject makeCompanyObject(String companyName) throws IOException {
		CompanyObject co = new CompanyObject ();
		String str [] = new String [1];
		
		try {
			companyName = companyName.replaceAll(SegmenterUtil.StopSigns, "").trim();		
	//		System.out.println(companyName);
			
			str = SegmenterUtil.segmenter.classifyToString(companyName).split(" +");
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
					if (SegmenterUtil.isCompanyType(str[i])) {
						co.type += str[i];
						used[i] = true;
						lastIndex = i;
						if (str[i].contains("公司")) {
							break;
						}
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
					if (SegmenterUtil.isCityName(str[i])) {
						companyName = companyName.substring(0, companyName.indexOf(str[i]))
								+ companyName.substring(companyName.indexOf(str[i]) + str[i].length());
						co.city += str[i];
						used[i] = true;
						lastIndex = i;
					}
				}
			}
			
			for (int i = 0; i < str.length; i ++) {
				if (!used[i] && str[i] != null && str[i].length() > 0 && !str[i].equals("")) {
					co.zihao += str[i];
				}
			}
			
			co.tokens = SegmenterUtil.segmenter.classifyToString(co.zihao).split(" +");
			HashSet<String> single = new HashSet<String> ();
			co.tokenTF = new double [co.tokens.length];
			for (int i = 0; i < co.tokens.length; i ++) {
				co.tokens[i] = co.tokens[i].trim();
				if (!single.contains(co.tokens[i])) {
					single.add(co.tokens[i]);
				}
				for (int j = 0; j < co.tokens.length; j ++) {
					if (co.tokens[i].equals(co.tokens[j])) {
						co.tokenTF[i] ++;
					}
				}
			}
			for (String item : single) {
				if (!IDFDict.containsKey(item)) {
					IDFDict.put(item, 1.0);
				} else {
					double temp = IDFDict.get(item);
					IDFDict.remove(item);
					IDFDict.put(item, temp + 1.0);
				}
			}
		
		} catch (IndexOutOfBoundsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(companyName + " " + str[0] + " " + str[0].length());
		}
		return co;
	}
	
	public static void partition () {
		FileInput fi = new FileInput(FilePath.RawCompanyNameFile);
		FileOutput fo = new FileOutput(FilePath.CompanyNameTFIDFFile);
		FileOutput foTFIDF = new FileOutput(FilePath.CompanyNameTFIDFTokensFile);
		
		int counter = 0;
		
		ArrayList<CompanyObject> coList = new ArrayList<CompanyObject> ();
		
		String line = new String ();
		try {
			while ((line = fi.reader.readLine()) != null) {
				counter ++;
				if (counter > TestNumber) {
					 break;
				}
				line = line.trim();
				//System.out.println("***" + line);
				CompanyObject co = makeCompanyObject(line);
				coList.add(co);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		double adjustN = IDFDict.size();
		for (int i = 0; i < coList.size(); i ++) {
			System.out.println(i);
			if (coList.get(i).tokenTF != null) {
				for (int j = 0; j < coList.get(i).tokenTF.length; j ++) {
					coList.get(i).tokenTF[j] = (double)coList.get(i).tokenTF[j]
							/ (adjustN + IDFDict.get(coList.get(i).tokens[j]) + 1.0);
				}
			}
		}
		
		for (int i = 0; i < coList.size(); i ++) {
			if (coList.get(i).tokenTF != null) {
				double pre = 0.0, suc = 0.0, minValue = 0x7fffffff;
				int index = 0;
				for (int j = 0; j < coList.get(i).tokenTF.length; j ++) {
					suc += coList.get(i).tokenTF[j];
				}
				for (int j = 0; j < coList.get(i).tokenTF.length - 1; j ++) {
					pre += coList.get(i).tokenTF[j];
					suc -= coList.get(i).tokenTF[j];
					double temp = pre * pre + suc * suc;
					if (temp <= minValue) {
						minValue = temp;
						index = j;
					}
				}
				coList.get(i).zihao = "";
				for (int j = 0; j <= index; j ++) {
					coList.get(i).zihao += coList.get(i).tokens[j];
				}
				for (int j = index + 1; j < coList.get(i).tokens.length; j ++) {
					coList.get(i).industry += coList.get(i).tokens[j];
				}
			}
		}
		
		for (int i = 0; i < coList.size(); i ++) {
			try {
				fo.t3.write(coList.get(i).city + "	" + coList.get(i).zihao
					+ "	" + coList.get(i).industry + "	" + coList.get(i).type);
				fo.t3.newLine();
				if (coList.get(i).tokens != null) {
					for (int j = 0; j < coList.get(i).tokens.length; j ++) {
						foTFIDF.t3.write(coList.get(i).tokens[j] + "	");
					}
				}
				foTFIDF.t3.newLine();
				if (coList.get(i).tokenTF != null) {
					for (int j = 0; j < coList.get(i).tokenTF.length; j ++) {
						foTFIDF.t3.write(coList.get(i).tokenTF[j] + "	");
					}
				}
				foTFIDF.t3.newLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (Debug) {
			for (int i = 0; i < coList.size(); i ++) {
				System.out.println(coList.get(i).city + "	" + coList.get(i).zihao
						+ "	" + coList.get(i).industry + "	" + coList.get(i).type);
				for (int j = 0; j < coList.get(i).tokens.length; j ++) {
					System.out.print(coList.get(i).tokens[j] + "	");
				}
				System.out.println();
				for (int j = 0; j < coList.get(i).tokenTF.length; j ++) {
					System.out.print(coList.get(i).tokenTF[j] + "	");
				}
				System.out.println();
			}
			
			for (String item : IDFDict.keySet()) {
				System.out.print(item + "	" + IDFDict.get(item) + "	" + item.length());
			}
		}
		
		fi.closeInput();
		fo.closeOutput();
		foTFIDF.closeOutput();
	}
	
	public static void main(String [] args) throws IOException {
		SegmenterUtil.loadSegmenter();
		SegmenterUtil.loadCityName();
		partition();		
	}	
}