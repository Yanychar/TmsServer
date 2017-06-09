package com.c2point.tms.web.reporting.pdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.util.DateUtil;
import com.c2point.tms.web.application.TmsApplication;
import com.c2point.tms.web.util.Lang;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.AreaBreakType;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.vaadin.server.StreamResource;

public abstract class PdfDocTemplate {

	private static Logger logger = LogManager.getLogger( PdfDocTemplate.class.getName());
	
	private	File 		reportFile = null;
	private Document	managedDocument = null;
	
	protected PdfFont textFont;
	protected PdfFont headerFont;
	protected PdfFont headerFontBig;
	protected PdfFont subheaderFont;
	protected PdfFont subheaderFontBig;
	
	protected PdfFont titleFont;
	protected PdfFont subtitleFont;
	
	private	TmsApplication 	app;
	
	private  Map<String, String> 	params = new HashMap<String, String>();

	public PdfDocTemplate( TmsApplication app ) {

		this.app = app;
		
		createDoc();
		
		createFonts();
		
	}
	
	private Document createDoc() {
		
		try {
			reportFile = File.createTempFile( "tmsreport", ".pdf" );
			logger.debug( "Report file was created as temporal: " + reportFile.getAbsolutePath());
		} catch (IOException e1) {

			logger.error( "Cannot create file to write PDF document!" );
			return null;
		}
		
		try {
			
			PdfWriter writer = new PdfWriter( new FileOutputStream( reportFile ));
			logger.debug( "PdfWriter created" );
			// Initialize PDF document
			PdfDocument pdf = new PdfDocument( writer );
			logger.debug( "PdfDocument created" );
			// Init abstract(hides PDF nature document
			managedDocument = new Document( pdf, PageSize.A4 );
			managedDocument.setMargins( 20, 20, 20, 20 );
			
			logger.debug( "Document created" );
			
		} catch (FileNotFoundException e) {
			logger.error( "Cannot create PDF document!" );
			return null;
		}
		
		return managedDocument;
		
		
	}
	
	protected abstract void printTitlePage() throws Exception;
	protected abstract void printLastPage() throws Exception;
	
	protected abstract void printContent() throws Exception;
	
	public void printReport() {

		try {
		
			printTitlePage();
			
			printContent();
			
			printLastPage();
		
    	} catch ( Exception e ) {                
    		logger.error( "Failed to create Report!" );
    	} finally {
			try {
				closeReport();
			} catch ( Exception e ) {
	    		logger.error( "Failed to close Report!" );
			}
    	}
		
	}
	
	public void closeReport() {
		
		// Close document
		managedDocument.close();
	}
	
	public void deleteReport() {
		
		// Close document
		managedDocument.close();
		// Delete Temp file
		if ( reportFile != null && reportFile.exists()) {
			reportFile.delete();
		}
	}
	
	
	public String getParameter( String name ) { return params.get( name );
	}
	public void setParameter( String name, String value ) {
		params.put( name,  value );
	}

    public void testFill() {

		PdfFont font = null;
		try {
			font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		managedDocument.add(new Paragraph( "iText is:" ).setFont(font));

        // Create a List
        List list = new List()
            .setSymbolIndent(12)
            .setListSymbol("\u2022")
            .setFont(font);
        // Add ListItem objects
        list.add(new ListItem("BBBBB"))
        	.add(new ListItem("Never gonna give you up"))
            .add(new ListItem("Never gonna let you down"))
            .add(new ListItem("Never gonna run around and desert you"))
            .add(new ListItem("Never gonna make you cry"))
            .add(new ListItem("Never gonna say goodbye"))
            .add(new ListItem("Never gonna tell a lie and hurt you"));
//        	.add(new ListItem( "*** " + Integer.toString(i++)+ " ***" ));
        // Add the list
        managedDocument.add(list);    	
   
        closeReport();
    }
    
	public StreamResource getResource() {

		StreamResource resource = null;
		
		if ( reportFile != null && reportFile.exists()) {
			StreamResource.StreamSource stream = new StreamResource.StreamSource() {
				private static final long serialVersionUID = 1L;
	
				@Override
				public InputStream getStream() {
					try {
						
						return new FileInputStream( reportFile );
						
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					return null;
				}
				
			};
			resource = new StreamResource( stream, reportFile.getAbsolutePath());
		} else {
			logger.error( "PDF document does not exist!" );
		}
		
		return resource;
	}

	protected Document getDoc() { return managedDocument; }
	protected TmsApplication getApp() { return app; }

	private void createFonts() {

		if ( app.getSessionData().getLocale() == Lang.LOCALE_RU ) {
		
			try {
/*
				BaseFont baseFont = BaseFont.createFont( "c:/windows/fonts/ARIAL.TTF", "cp1251", true );
*/			
				textFont = PdfFontFactory.createFont( FontConstants.TIMES_ROMAN, "cp1251", true ); // size 8
				
				headerFont = PdfFontFactory.createFont( FontConstants.TIMES_BOLD, "cp1251", true ); // size 10
				headerFontBig = PdfFontFactory.createFont( FontConstants.TIMES_BOLD, "cp1251", true );  // size 14
				
				subheaderFont = PdfFontFactory.createFont( FontConstants.TIMES_BOLDITALIC + FontConstants.UNDERLINE, "cp1251", true ); // Size 10
				subheaderFontBig = PdfFontFactory.createFont( FontConstants.TIMES_BOLDITALIC + FontConstants.UNDERLINE, "cp1251", true ); // Size 12
	
				titleFont = PdfFontFactory.createFont( FontConstants.TIMES_BOLD, "cp1251", true ); // Size 18
				subtitleFont = PdfFontFactory.createFont( FontConstants.TIMES_BOLDITALIC, "cp1251", true ); // Size 14
				
			
			} catch ( Exception e ) {
				
				logger.error( "Cannot create embedded fonts\n" + e );

				try {
					textFont = PdfFontFactory.createFont( FontConstants.TIMES_ROMAN, "cp1251", true ); // size 8

					headerFont = PdfFontFactory.createFont( FontConstants.TIMES_BOLD, "cp1251" ); // size 10
					headerFontBig = PdfFontFactory.createFont( FontConstants.TIMES_BOLD, "cp1251" );  // size 14
					
					subheaderFont = PdfFontFactory.createFont( FontConstants.TIMES_BOLDITALIC + FontConstants.UNDERLINE, "cp1251" ); // Size 10
					subheaderFontBig = PdfFontFactory.createFont( FontConstants.TIMES_BOLDITALIC + FontConstants.UNDERLINE, "cp1251" ); // Size 112
		
					titleFont = PdfFontFactory.createFont( FontConstants.TIMES_BOLD, "cp1251" ); // Size 18
					subtitleFont = PdfFontFactory.createFont( FontConstants.TIMES_BOLDITALIC, "cp1251" ); // Size 14
				} catch (IOException e1) {
					logger.error( "Cannot create fonts\n" + e );
				}  
				
			}
		} else {

			try {
				textFont = PdfFontFactory.createFont( FontConstants.TIMES_ROMAN ); // size 8
				
				headerFont = PdfFontFactory.createFont( FontConstants.TIMES_BOLD ); // size 10
				headerFontBig = PdfFontFactory.createFont( FontConstants.TIMES_BOLD );  // size 14
				
				subheaderFont = PdfFontFactory.createFont( FontConstants.TIMES_BOLDITALIC); // Size 10
				subheaderFontBig = PdfFontFactory.createFont( FontConstants.TIMES_BOLDITALIC); // Size 112

				titleFont = PdfFontFactory.createFont( FontConstants.TIMES_BOLD ); // Size 18
				subtitleFont = PdfFontFactory.createFont( FontConstants.TIMES_BOLDITALIC ); // Size 14
				
			} catch (IOException e) {
				logger.error( "Cannot create fonts\n" + e );
			} 
			
		}
	}

	// Several methods to draw line of text
	public void addTitle( String str ) { addParagraph( str, titleFont, 18 );}
	public void addSubtitle( String str ) { addParagraph( str, subtitleFont, 14 ); }
	public void addParagraph( String str, PdfFont font, int size ) {
		this.getDoc().add( new Paragraph( str ).setFont( font ).setFontSize( size ));
	}

	// Several methods to draw in table cells
	protected Cell addHeaderCell( Table table, String str ) {
		return addCell( table, str, 0, headerFontBig, 14, true );
	}
	protected Cell addFooterCell( Table table, String str ) {
		return addCell( table, str, 0, headerFontBig, 14, false );
	}
	protected Cell addFooterCell( Table table, float num ) {
		
		return addCell( table, String.format( "%.1f", num ), 0, headerFontBig, 14, false );
	}


	protected Cell addHeaderCell( Table table, String str, PdfFont font, int size ) {
		return addCell( table, str, 0, font, size, true );
	}
	protected Cell addFooterCell( Table table, String str, PdfFont font, int size ) {
		return addCell( table, str, 0, font, size, false );
	}


	protected Cell addHeaderCell( Table table, String str, int spanCount ) {
		return addCell( table, str, spanCount, headerFontBig, 14, true );
	}
	protected Cell addFooterCell( Table table, String str, int spanCount ) {
		return addCell( table, str, spanCount, headerFontBig, 14, false );
	}
	
	
	protected Cell addCell( Table table, String str, int spanCount, PdfFont font, int size, boolean isPageHeader ) {
		Cell cell = null;
		if ( spanCount > 1 ) {
			cell = new Cell( 1, spanCount );
		} else {
			cell = new Cell();
		}
		
		cell.add(  new Paragraph( str ).setFont( font ).setFontSize(size));
		cell.setHorizontalAlignment( HorizontalAlignment.CENTER );
		if ( isPageHeader )
			table.addHeaderCell( cell );
		else 
			table.addCell( cell );
		
	    return cell;
	}
	
	protected Cell add( Table table, int num, String suffix ) {
		
		return add( table, Integer.toString( num ) + suffix, HorizontalAlignment.LEFT, textFont, 10 );
	}
	protected Cell add( Table table, float num ) {
		
		return add( table, String.format( "%.1f", num ), HorizontalAlignment.LEFT, textFont, 10 );
	}
	protected Cell add( Table table, int num ) {
		
		return add( table, Integer.toString( num ), HorizontalAlignment.LEFT, textFont, 10 );
	}

	protected Cell add( Table table, String str ) {
		
		return add( table, str, HorizontalAlignment.RIGHT, textFont, 10 );
	}
	protected Cell add( Table table, Date date ) {
		
		return add( table, DateUtil.dateToString( date ), HorizontalAlignment.CENTER, textFont, 10 );
	}
	protected Cell add( Table table, String str, HorizontalAlignment alignment, PdfFont font, int size ) {
		
		Cell cell = new Cell();
		
		cell.add(  new Paragraph( str ).setFont( font ).setFontSize(size));
		cell.setHorizontalAlignment( alignment );
	    table.addCell( cell );
		
		return cell;
	}

	protected Cell add( Table table, String str, int colspan ) {
		Cell cell = new Cell( 1, colspan );
		cell.add(  new Paragraph( str ));
	    table.addCell( cell );
		
		return cell;
	}
	protected Cell addEmptyCells( Table table, int colspan ) {
		
		return add( table, "", colspan );
	}



	
	// Additional helper methods
	
	public void addTable( Table table ) {

		this.getDoc().add( table );
		
	}
	
	public void nextLine() {
		this.getDoc().add( new Paragraph( " " ));
	}
	
	public void newPage() {
		this.getDoc().add(new AreaBreak(AreaBreakType.NEXT_PAGE));
	}
	
	
}
