package utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileInput {
	
	public InputStreamReader isr;
	public BufferedReader reader;
	
	public FileInput (String inputPath) {
		initInput(inputPath);
	}
	
	public void initInput(String inputPath)
	{
		try {
			this.isr = new InputStreamReader(new FileInputStream(inputPath));
			this.reader = new BufferedReader(this.isr);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void closeInput()
	{
		try {
			this.reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String [] args) {}
}
