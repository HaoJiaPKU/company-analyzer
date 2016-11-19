package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;

import config.FilePath;

public class CityName {
	
	public static final String InputCityNameFile = FilePath.DataDir + "/citys.txt";
	public static final String OutputCityNameFile = FilePath.DataDir + "/city-name.txt";
	public static final String EncodingOutput = "UTF-8";
	public static ArrayList<String> cityName = new ArrayList<String> ();
	public static HashSet<String> cityNameSet = new HashSet<String> ();
	public static final String StopSigns = "[\\p{P}~$`^=|<>～｀＄＾＋＝｜＜＞￥× \\s|\t|\r|\n]+";
	public static final String StopWords [] = {"有限", "责任", "公司", "股份", "经济", "科技", "开发", "太平", "合伙"};
	
	public static boolean isStopWords(String str) {
		for (int j = 0; j < StopWords.length; j ++) {
			if (str.contains(StopWords[j])) {
				return true;
			}
		}
		return false;
	}
	
	public static void loadCityName()
	{
		InputStreamReader isr;
		try {
			isr = new InputStreamReader(new FileInputStream(InputCityNameFile));
			BufferedReader reader = new BufferedReader(isr);
			String line = null;
			try {
				while((line = reader.readLine()) != null) {
					line = line.replace(StopSigns, "").trim();
					String temp [] = line.split("	+");
					for (int i = 0; i < temp.length; i ++) {
						temp[i] = temp[i].trim();
						if (temp[i] != null
								&& temp[i].length() > 1 && temp[i] != "") {
							if (!isStopWords(temp[i])
									&& !cityNameSet.contains(temp[i])) {
								cityNameSet.add(temp[i]);
								cityName.add(temp[i]);
							}
						}
					}
				}
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
	
	public static void outputCityName() {
		FileOutputStream t1;
		try {
			t1 = new FileOutputStream(new File(OutputCityNameFile));
			OutputStreamWriter t2;
			try {
				t2 = new OutputStreamWriter(t1, EncodingOutput);
				BufferedWriter t3 = new BufferedWriter(t2);
				try {
					for (int i = 0; i < cityName.size(); i ++) {
						t3.write(cityName.get(i));
						t3.newLine();
					}
					t3.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String [] args) {
		loadCityName();
		outputCityName();
	}
}
