package com.c2point.tms.web.reporting.pdf;

import com.itextpdf.text.Document;

public interface DocCreateIf {

/*	
	public void addHeader_H1( String str );
	public void addHeader_H2( String str );
	public void addHeader_H3( String str );

	public void addParagraph( String str );
	
	public void addString( String str, Font font );
	
	public void nextLine();
*/	
	
	
	public Document getDocument(); 
	
	public void endDoc();
	   
}
