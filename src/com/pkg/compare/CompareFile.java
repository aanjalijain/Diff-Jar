package com.pkg.compare;

import java.awt.BorderLayout;
import java.io.File;

public class CompareFile {
	
	/** Two files to compare */
	private File[] _files = new File[2];
    public void startCompare(File inFile1, File inFile2)
	{
        File file1 = inFile1;
		File file2 = inFile2;
		if (file1==null || !file1.exists() || !file1.canRead()) {
			System.out.println("JAR1 Missing");
		}
		if (file2 == null || !file2.exists() || !file2.canRead()) {
			System.out.println("JAR2 Missing");
		}
		_files[0] = file1;
		_files[1] = file2;
		new Thread(new Runnable() {
			public void run() {
				doCompare();
			}
		}).start();

		
	}
	private void doCompare()
	{
		CompareResults results = Comparer.compare(_files[0], _files[1]);
		results.getEntryList().forEach(print->System.out.println(print));
		final boolean archivesDifferent = (results.getStatus() == EntryDetails.EntryStatus.CHANGED_SIZE);
		if (archivesDifferent) {
			System.out.println("Archives have different size (" + results.getSize(0) + ", " + results.getSize(1) + ")");
		}
		else {
			System.out.println("Archives have the same size (" + results.getSize(0)+ ")");
		}
		if (results.getEntriesDifferent()) {
			System.out.println((archivesDifferent?"and":"but") + " the Jars have different counts");
		}
		else {
				System.out.println((archivesDifferent?"but":"and") + " the Jars  have the same counts");
			}
	}

}
