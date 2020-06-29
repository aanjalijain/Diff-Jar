package com.pkg.compare;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;

import com.pkg.compare.EntryDetails.EntryStatus;

/**
 * Class to do the actual comparison of jar files,
 * populating a list of EntryDetails objects
 */
public  class Comparer
{
	private static final int NOT_FOUND = -1;
	

	/**
	 * Compare the two given files and return the results
	 * @param inFile1 first file
	 * @param inFile2 second file
	 * @param inMd5 true to also check md5 sums
	 * @return results of comparison
	 */
	public static CompareResults compare(File inFile1, File inFile2)
	{
		// Make results object and compare file sizes
		CompareResults results = new CompareResults();
		
		results.setSize(0, inFile1.length());
		results.setSize(1, inFile2.length());
		// Make empty list
		ArrayList<EntryDetails> entryList = new ArrayList<EntryDetails>();
		// load first file, make entrydetails object for each one
		final int numFiles1 = makeEntries(entryList, inFile1, 0);
		results.setNumFiles(0, numFiles1);
		// load second file, try to find entrydetails for each file or make new one
		final int numFiles2 = makeEntries(entryList, inFile2, 1);
		results.setNumFiles(1, numFiles2);
		results.setEntryList(entryList);
		
		return results;
	}


	/**
	 * Make entrydetails objects for each entry in the given file and put in list
	 * @param inList list of entries so far
	 * @param inFile zip/jar file to search through
	 * @param inIndex 0 for first file, 1 for second
	 * @return number of files found
	 */
	private static int makeEntries(ArrayList<EntryDetails> inList, File inFile, int inIndex)
	{
		 ArrayList<String> diffFileColl=new ArrayList<String>();
		boolean checkList = (inList.size() > 0);
		int numFiles = 0;
		JarFile jarF = null;
		try
		{
			jarF = new JarFile(inFile);
			Enumeration<?> jarEntries = jarF.entries();
			while (jarEntries.hasMoreElements())
			{
			    JarEntry ze = (JarEntry) jarEntries.nextElement();
				numFiles++;
				String name = ze.getName();
				EntryDetails details = null;
				if (checkList) {details = getEntryFromList(inList, name);}
				// Construct new details object if necessary
				if (details == null)
				{
					details = new EntryDetails();
					details.setName(name);
					inList.add(details);
				}
				// set size
				details.setSize(inIndex, ze.getSize());
				details.set_modifiedDate(inIndex, new Date(ze.getLastModifiedTime().toMillis()));
				if(inIndex==1){
					
					if(EntryDetails.EntryStatus.ADDED.equals(details.getStatus())){
						diffFileColl.add(ze.getName());
						System.out.println("Added");
					}
					if(EntryDetails.EntryStatus.CHANGED_SIZE.equals(details.getStatus()) && EntryDetails.EntryStatus.DATEMODIFIED.equals(details.isDateModified())){
						diffFileColl.add(ze.getName());
						System.out.println("Either size or date modified");
					}
				}

			}
			if(inIndex==1){
			//Create DiffJar
			createDiffJar(diffFileColl,inFile);
			}
			
		
		}
		catch (IOException ioe) {
			System.err.println("Ouch: " + ioe.getMessage());
		}
		finally {
			try {jarF.close();} catch (Exception e) {}
		}
		return numFiles;
	}
   public static void  createDiffJar(ArrayList<String> diffFileColl,File inFile) throws IOException{
	   if(!diffFileColl.isEmpty()){
			String jarName = inFile.toString();   // Pass your Jar2 path here which having new changes(Jar2)
			String newJarName = "C:\\Users\\anjajain\\Documents\\jarPoc3.jar";   // Pass your New Jar path here which you want to create new(Jar3)
			  List<String> listFileChanged= new ArrayList<String>();   // Pass your List here which having changed file names(Complete list off diff)
			  for(int i=0;i<diffFileColl.size();i++){
				  listFileChanged.add(diffFileColl.get(i));
			  }
			  //listFileChanged.add(fileName1);  listFileChanged.add(fileName2); // Remove this line if assigning list directly
			  // Create file descriptors for the jar and a temp jar.
			  File jarFile = new File(jarName);
			  File newJarFile = new File(newJarName);
			  File tempJarFile = new File(jarName + ".tmp");
			  if(newJarFile.exists()){
				  newJarFile.delete();
			  }
			  
			  // Open the jar file.
			  JarFile jar;
			  // Initialize a flag that will indicate that the jar was updated.
			  jar = new JarFile(jarFile);
			  System.out.println(jarName + " opened.");
			  boolean jarUpdated = false;
			   try {
			     JarOutputStream tempJar =
			        new JarOutputStream(new FileOutputStream(tempJarFile));
			   // Allocate a buffer for reading entry data.
			
			     byte[] buffer = new byte[1024];
			     int bytesRead;
			
			     try {
                   // This will add all changed files to new temp jar with same structure
			        for (Enumeration entries = jar.entries(); entries.hasMoreElements(); ) {
			           // Get the next entry.
			           JarEntry entry = (JarEntry) entries.nextElement();
			           // If the entry has not been added already, add it.
			           if(null!=listFileChanged){
			        	   if (listFileChanged.contains(entry.getName())) {
			 	              // Get an input stream for the entry.
			 	              InputStream entryStream = jar.getInputStream(entry);
			 	              // Read the entry and write it to the temp jar.
			 	              tempJar.putNextEntry(entry);
			 	              while ((bytesRead = entryStream.read(buffer)) != -1) {
			 	                 tempJar.write(buffer, 0, bytesRead);
			 	              }
			 	           }
			        	   jarUpdated = true;
			           }
                    }	
                 }
			     catch (Exception ex) {
			        System.out.println(ex);
			        // Add a stub entry here, so that the jar will close without an
			        // exception.
			        tempJar.putNextEntry(new JarEntry("stub"));
			     }
			     finally {
			        tempJar.close();
			     }
			  }
			  catch(Exception ex){
				  System.out.println(ex);
			  }
			  finally {
			     jar.close();
			     System.out.println(jarName + " closed.");
			    // If no changes detected delete the temp jar file.
			    if (! jarUpdated) {
			        tempJarFile.delete();
			     }
			  }
			    // If the jar was updated, rename temp jar with new name
			  if (jarUpdated) {
			     tempJarFile.renameTo(newJarFile);
			     System.out.println(newJarName + " updated.");
			  }
		}
   }
	/**
	 * Look up the given name in the list
	 * @param inList list of EntryDetails objects
	 * @param inName name to look up
	 */
	private static EntryDetails getEntryFromList(ArrayList<EntryDetails> inList, String inName)
	{
		EntryDetails details = null;
		System.out.println(inList.size());
		for (int i=0; i<inList.size(); i++)
		{
			
			details = inList.get(i);
			System.out.println("index here"+i + " details Name"+details.getName());
			if (details.getName() != null && details.getName().equals(inName)) {
				
				return details;
			}
		}
		return null;
	}
	
}

