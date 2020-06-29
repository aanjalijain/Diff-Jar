package com.pkg.compare;
import java.io.File;
import java.io.IOException;

public class Compare {

	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		File file1 = new File("C:\\Users\\anjajain\\Documents\\jarPoc1.jar");
		File file2 = new File("C:\\Users\\anjajain\\Documents\\jarPoc2.jar");
		CompareFile cmpF = new CompareFile();
		// Pass two files to start with, or instruct to prompt
		cmpF.startCompare(file1, file2);

	}

}
