package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

public class UrlUtil {

	public static void main(String [] args) {
    	
    }
	
    public static String getHTML(String key) throws IOException  
    {  
        StringBuilder sb=new StringBuilder();  
        String path="http://www.baidu.com/s?tn=ichuner&lm=-1&word="+URLEncoder.encode(key,"gb2312")+"&rn=100";
        URL url=new URL(path);  
        BufferedReader breader=new BufferedReader(new InputStreamReader(url.openStream()));  
        String line=null;  
        while((line=breader.readLine())!=null)  
        {  
            sb.append(line);  
        } 
//        System.out.println(sb.toString());
//        sb=new StringBuilder();  
//        path="https://www.baidu.com/s?wd=" + URLEncoder.encode(key,"gb2312") + "&pn=10&tn=ichuner&ie=utf-8";  
//        url=new URL(path);  
//        breader=new BufferedReader(new InputStreamReader(url.openStream()));  
//        line=null;  
//        while((line=breader.readLine())!=null)  
//        {  
//            sb.append(line);  
//        }
        return sb.toString();  
    }

}
