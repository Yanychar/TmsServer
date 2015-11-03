package com.c2point.tms.tools.exprt;

import java.io.File;
import java.io.Writer;
import java.util.List;

public interface WritersSetIF {

	public Writer getWriter( String code );
	public File getFile( String code );
//	public Writer getWriter();
//	public void close( String code );
	public void close();
	
	// Used to get output file name
//	public String getWriterName( String code );
	public List< String > getWriterNames();
	
}
