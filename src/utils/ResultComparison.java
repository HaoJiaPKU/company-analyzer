package utils;

import java.io.IOException;

import config.FilePath;
import objects.CompanyObject;

public class ResultComparison {

	public static void compare() {
		FileInput fiOrigin = new FileInput(FilePath.OriginFile);
		FileInput fiBranch = new FileInput(FilePath.BranchFile);
		FileOutput fo = new FileOutput(FilePath.CompareFile);
		
		int counter = 0;
		String line = new String ();
		try {
			while ((line = fiOrigin.reader.readLine()) != null) {
				
				System.out.println(++ counter);
				
				boolean originMark = false;
				CompanyObject originCompanyObject = new CompanyObject();
				String originLine = line;
				String temp [] = line.split("	");
				for (int i = 0; i < temp.length; i ++) {
					if (temp[i] == null || temp[i].length() == 0
							|| temp[i].equals("")) {
						originMark = true;
					}
				}
				originCompanyObject = new CompanyObject(
							temp[0].trim(),
							temp[1].trim(),
							temp[2].trim(),
							temp[3].trim());
				
				line = fiBranch.reader.readLine();
				boolean branchMark = false;
				CompanyObject branchCompanyObject = new CompanyObject();
				String branchLine = line;
				temp = line.split("	");
				for (int i = 0; i < temp.length; i ++) {
					if (temp[i] == null || temp[i].length() == 0
							|| temp[i].equals("")) {
						branchMark = true;
					}
				}
				branchCompanyObject = new CompanyObject(
							temp[0].trim(),
							temp[1].trim(),
							temp[2].trim(),
							temp[3].trim());
				
				boolean compareMark = false;
				if (originCompanyObject.city.equals(branchCompanyObject.city)
					&& originCompanyObject.zihao.equals(branchCompanyObject.zihao)
					&& originCompanyObject.industry.equals(branchCompanyObject.industry)
					&& originCompanyObject.type.equals(branchCompanyObject.type)) {
					compareMark = true;
				}
				
				if (originMark) {
					fo.t3.write(originLine + "	");
				} else {
					fo.t3.write(originCompanyObject.city + "	"
							+ originCompanyObject.zihao + "	"
							+ originCompanyObject.industry + "	"
							+ originCompanyObject.type + "	");
				}
				fo.t3.write(originMark + "	");
				if (branchMark) {
					fo.t3.write(branchLine + "	");
				} else {
					fo.t3.write(branchCompanyObject.city + "	"
						+ branchCompanyObject.zihao + "	"
						+ branchCompanyObject.industry + "	"
						+ branchCompanyObject.type + "	");
				}
				fo.t3.write(branchMark + "	"
						+ compareMark);
				fo.t3.newLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		fiOrigin.closeInput();
		fiBranch.closeInput();
		fo.closeOutput();
	}
	
	public static void main(String args[]) {
		compare();
	}
}
