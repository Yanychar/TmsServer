package com.c2point.tms.tools.exprt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.c2point.tms.util.StringUtils;

public class TestTmp {

	private String 	EOL = "\r\n";
	private BufferedReader br;
	private CSVReader csvReader = null;
	
	
	public static void main(String[] args) {
/*

		File file = new File( "D:\\Development\\test.txt" );

		String nextLine;

		try {
		
			br = new BufferedReader( new FileReader( file ));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
	    try {
			while (( nextLine = br.readLine()) != null) {
			   System.out.println( nextLine );
			}
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
*/
		
		TestTmp tt = new TestTmp();
		
		
		String[][] strSS = {
				{ "Project \"CSKA center\"", "xxx" },	
				{ "cccsgh lkjsl sj'lk", "vvv" },	
				{ "bbb", "nnn" }	
		};

		
		
//		tt.readCsvFile( "D:\\Development\\test.txt" );
		
		tt.writeCsvFile( strSS, "D:\\Development\\test_out.txt" );
		
		tt.readCsvFile( "D:\\Development\\test_out.txt" );
		
	}

	List<String[]> lstOfStr;
	protected boolean readCsvFile( String fileName ) {
		boolean res = false;

		lstOfStr = new ArrayList<String[]>();
		
		try {
			csvReader = new CSVReader( new FileReader( fileName ), ';', '"', CSVWriter.NO_ESCAPE_CHARACTER );
		} catch (FileNotFoundException e1) {
			System.out.println( "ERROR: Cannot create CSVReader. " + e1.getMessage());
			return res;
		}

		String [] nextLine;
		boolean exitFlag = false;
		while ( !exitFlag ) {
			
			try {
				nextLine = csvReader.readNext();
				
				if ( nextLine != null ) {
					for( String str : nextLine ) {
						System.out.print( str + "   "  );
					}
					System.out.println();
					
					lstOfStr.add( nextLine );
					
				} else {
					exitFlag = true;
				}
				
			} catch (IOException e1) {
				System.out.println( "ERROR: Cannot read line from file. " + e1.getMessage());
			}
		}
		res = true;

		try {
			csvReader.close();
		} catch (IOException e) {
			System.out.println( "ERROR: Cannot close CSVReader properly" );
		}
		
		return res;
	}
	
	private boolean writeCsvFile( String[][] strSS, String fileName ) {
		boolean res = false;
		
		CSVWriter writer;
		try {
			writer = new CSVWriter(new FileWriter( fileName ), ';', '"', CSVWriter.NO_ESCAPE_CHARACTER, EOL );
		} catch (IOException e) {
			System.out.println( "ERROR: Cannot create CSVWriter. " + e.getMessage());
			return res;
		}
	     // feed in your array (or convert your data to an array)
		for ( String[] strS : strSS ) {
			
		     writer.writeNext( strS );
			
		     for ( String str : strS ) {
		    	 System.out.print( str + ';' ); 
		     }
		     System.out.println();
		     
		}
		
		if ( lstOfStr != null && lstOfStr.size() > 0 ) {
			for ( String[] strS : lstOfStr ) {
				
			     writer.writeNext( strS );
				
			}
		}
		res = true;
		
		try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return res;
	}
	
}
