package objects;

public class CompanyObject {
	public String city = new String ();
	public String zihao = new String ();
	public String industry = new String ();
	public String type = new String ();
	
	public String [] tokens;
	public double [] tokenTF;
	
	public CompanyObject () {}
	
	public CompanyObject (String city, String zihao, String industry,
			String type) {
		this.city = city;
		this.zihao = zihao;
		this.industry = industry;
		this.type = type;
	}
}
