package config;

public class FilePath {
	public static final String DataDir = "data";
	
	public static final String RawCityNameFile = FilePath.DataDir + "/citys.txt";
	public static final String CityNameFile = FilePath.DataDir + "/city-name.txt";
	
	public static final String EncodingOutput = "UTF-8";
	
	public static final String RawCompanyNameFile =
			DataDir + "/companys.txt";
	public static final String CompanyNameBaiduFile =
			DataDir + "/company-name-baidu.txt";
	public static final String CompanyNameTFIDFFile =
			DataDir + "/company-name-tfidf.txt";
	public static final String CompanyNameTFIDFTokensFile =
			DataDir + "/company-name-tfidf-tokens.txt";
	
	public static final String OriginFile = CompanyNameBaiduFile;
	public static final String BranchFile = CompanyNameTFIDFFile;
	public static final String CompareFile =
			DataDir + "/result-compare.txt";
	
}
