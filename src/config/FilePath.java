package config;

public class FilePath {
	public static final String DataDir = "data";
	
	public static final String RawCityNameFile = FilePath.DataDir + "/citys.txt";
	public static final String CityNameFile = FilePath.DataDir + "/city-name.txt";
	
	public static final String EncodingOutput = "UTF-8";
	
	public static final String RawCompanyNameFile =
			FilePath.DataDir + "/companys.txt";
	public static final String CompanyNameBaiduFile =
			FilePath.DataDir + "/company-name-baidu.txt";
	public static final String CompanyNameTFIDFFile =
			FilePath.DataDir + "/company-name-tfidf.txt";
	public static final String CompanyNameTFIDFTokensFile =
			FilePath.DataDir + "/company-name-tfidf-tokens.txt";
}
