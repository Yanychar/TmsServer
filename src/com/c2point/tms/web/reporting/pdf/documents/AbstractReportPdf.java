package com.c2point.tms.web.reporting.pdf.documents;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.web.application.TmsApplication;
import com.c2point.tms.web.util.Lang;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

public abstract class AbstractReportPdf {
	private static Logger logger = LogManager.getLogger( AbstractReportPdf.class.getName());

	protected Font headerFont;
	protected Font headerFontBig;
	protected Font subheaderFont;
	protected Font subheaderFontBig;
	
	
	protected Font h1Font;
	protected Font h2Font;
	protected Font h3Font;

	protected Font titleFont;
	protected Font subtitleFont;

	
	
	protected TmsApplication app;
	
	public  AbstractReportPdf( TmsApplication app ) {
		this.app = app;
		
		if ( app.getSessionData().getLocale() == Lang.LOCALE_RU ) {
			try {
				BaseFont baseFont = BaseFont.createFont( "c:/windows/fonts/ARIAL.TTF", "cp1251", true );
	
				headerFont = new Font( baseFont, 8, Font.BOLD );  
				headerFontBig = new Font( baseFont, 12, Font.BOLD );
				
				subheaderFont = new Font( baseFont, 8, Font.BOLDITALIC + Font.UNDERLINE );
				subheaderFontBig = new Font( baseFont, 10, Font.BOLDITALIC + Font.UNDERLINE );
	
				titleFont = new Font( baseFont, 16, Font.BOLD );
				subtitleFont = new Font( baseFont, 12, Font.BOLDITALIC );
			
			
			
			} catch ( Exception e ) {
				
				logger.error( "Cannot create embedded fonts\n" + e );
				
				headerFont = new Font( Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD );  
				headerFontBig = new Font( Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD );
				
				subheaderFont = new Font( Font.FontFamily.TIMES_ROMAN, 10, Font.BOLDITALIC + Font.UNDERLINE );
				subheaderFontBig = new Font( Font.FontFamily.TIMES_ROMAN, 12, Font.BOLDITALIC + Font.UNDERLINE );
	
				titleFont = new Font( Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD );
				subtitleFont = new Font( Font.FontFamily.TIMES_ROMAN, 14, Font.BOLDITALIC );
			}
		} else {
			headerFont = new Font( Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD );  
			headerFontBig = new Font( Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD );
			
			subheaderFont = new Font( Font.FontFamily.TIMES_ROMAN, 10, Font.BOLDITALIC + Font.UNDERLINE );
			subheaderFontBig = new Font( Font.FontFamily.TIMES_ROMAN, 12, Font.BOLDITALIC + Font.UNDERLINE );

			titleFont = new Font( Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD );
			subtitleFont = new Font( Font.FontFamily.TIMES_ROMAN, 14, Font.BOLDITALIC );
		}

		
	
	}
	
	protected PdfPCell addHeaderCell( PdfPTable table, String str ) {
		return addHeaderCell( table, str, 0, headerFontBig );
	}

	protected PdfPCell addHeaderCell( PdfPTable table, String str, Font font ) {
		return addHeaderCell( table, str, 0, font );
	}

	protected PdfPCell addHeaderCell( PdfPTable table, String str, int spanCount ) {
		return addHeaderCell( table, str, spanCount, headerFontBig );
	}
	
	protected PdfPCell addHeaderCell( PdfPTable table, String str, int spanCount, Font font ) {
		PdfPCell cell = new PdfPCell( new Phrase( str, font ));
		cell.setHorizontalAlignment( Element.ALIGN_CENTER );
		if ( spanCount > 1 ) {
			cell.setColspan( spanCount );
		}
	    table.addCell( cell );
	    
	    return cell;
	}

	public void addTitle( Paragraph prg, String str ) {
		addParagraph( prg, str, titleFont );
	}
	public void addSubtitle( Paragraph prg, String str ) {
		addParagraph( prg, str, subtitleFont );
	}
	
	public void addParagraph( Paragraph prg, String str, Font font ) {
		prg.add( new Paragraph( str, font ) );
	}
	

}
