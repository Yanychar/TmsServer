package com.c2point.tms.web.reporting.pdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
//import com.vaadin.terminal.StreamResource.StreamSource;
import com.vaadin.server.StreamResource.StreamSource;

@SuppressWarnings("serial")
public class PdfTemplate implements StreamSource, DocCreateIf {

	private static Logger logger = LogManager.getLogger( PdfTemplate.class.getName());
	
	private final ByteArrayOutputStream os = new ByteArrayOutputStream();
	
	protected Document document = null;            
/*
	protected Font h1Font = new Font( Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD );
	protected Font h2Font = new Font( Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD );
	protected Font h3Font = new Font( Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD );

	protected Font titleFont = new Font( Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD );
	protected Font subtitleFont = new Font( Font.FontFamily.TIMES_ROMAN, 14, Font.BOLDITALIC );
*/	
	
    public PdfTemplate() {
    	try {                
    		document = new Document( PageSize.A4, 50, 50, 50, 50 );                
    		PdfWriter.getInstance( document, os );                
    		document.open();                
    	} catch ( Exception e ) {                
    		logger.error( "Cannot create iText.Document and/or PdfWriter!" );
    	} finally {
    	}
    }
    
    @Override        
	public InputStream getStream() {
		// Here we return the pdf contents as a byte-array            
		return new ByteArrayInputStream( os.toByteArray());        
	}

    public static String getTmpName() {
    	
    	return "TempReport" + Long.toString( new Date().getTime()) + ".pdf";
    	
    }
    
	@Override
	public Document getDocument() {
		return document;
	}

	@Override
	public void endDoc() {
		if ( document != null ) {
			document.close();
		}            
	}        

	public void nextLine( Paragraph prg ) {
		
		prg.add( new Paragraph( " " ));

	}
	
	public void nextLine() {
		
		try {
			document.add( new Paragraph( " " ));
		} catch (DocumentException e) {
			logger.error( "Cannot add LF to document\n" + e );
		}

	}
	
	public void newPage() {
		
		document.newPage();

	}

/*	
	public void addTitle( Paragraph prg, String str ) {
		addParagraph( prg, str, titleFont );
	}
	public void addSubtitle( Paragraph prg, String str ) {
		addParagraph( prg, str, subtitleFont );
	}
	
	public void addParagraph( Paragraph prg, String str, Font font ) {
		prg.add( new Paragraph( str, font ) );
	}
*/	
	
	
	


}
