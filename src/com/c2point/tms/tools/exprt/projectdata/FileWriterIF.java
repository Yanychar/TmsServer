package com.c2point.tms.tools.exprt.projectdata;

public interface FileWriterIF {

	public boolean open();
	public boolean close();
	public boolean delete();
	public boolean write( Object obj );
	
	
	
}
