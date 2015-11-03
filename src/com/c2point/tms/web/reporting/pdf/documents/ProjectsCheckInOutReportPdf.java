package com.c2point.tms.web.reporting.pdf.documents;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.util.DateUtil;
import com.c2point.tms.util.StringUtils;
import com.c2point.tms.web.application.TmsApplication;
import com.c2point.tms.web.reporting.pdf.PdfTemplate;
import com.c2point.tms.web.reporting.checkinout.AggregateItem;
import com.c2point.tms.web.reporting.checkinout.DateItem;
import com.c2point.tms.web.reporting.checkinout.PersonItem;
import com.c2point.tms.web.reporting.checkinout.ProjectItem;
import com.c2point.tms.web.reporting.checkinout.ProjectsReport;
import com.c2point.tms.web.ui.reportview.checkinout.ReportCheckInOutModel;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

public class ProjectsCheckInOutReportPdf extends AbstractReportPdf {

	private static Logger logger = LogManager.getLogger( ProjectsCheckInOutReportPdf.class.getName());

	private ProjectsReport pr;
	private ReportCheckInOutModel model;
	
	public ProjectsCheckInOutReportPdf( TmsApplication app, ProjectsReport pr, ReportCheckInOutModel model ) {
		super( app );
		
		this.pr		= pr;
		this.model 	= model;
	}
	
	
	
	public PdfTemplate create() { 
		
		PdfTemplate doc = new PdfTemplate();
		
		try {
			
			Paragraph docHeader = new Paragraph();
			
			doc.nextLine( docHeader );
			addTitle( docHeader, app.getResourceStr( "reporting.item.header.projects" ) + " " );
			addTitle( docHeader, model.getOrganisation().getName());
			doc.nextLine( docHeader );
			addSubtitle( docHeader, app.getResourceStr( "reporting.item.header.period" ) + " " 
							+ DateUtil.dateToString( model.getStartDate()) 
							+ " - " 
							+ DateUtil.dateToString( model.getEndDate()));

			doc.nextLine( docHeader );
	
			doc.getDocument().add( docHeader );

			doc.getDocument().add( createConsolidatedProjectsTable());

			doc.nextLine();
			
			if ( model.isProjectPersonnelFlag()) {
				doc.getDocument().add( createPersonnelTable());
			}

    	} catch ( Exception e ) {                
    		logger.error( "Cannot create iText.Document and/or PdfWriter!\n" + e  );
    	} finally {
    		if ( doc != null ) {
    			doc.endDoc();
    		}            
    	}
		
		
		
		
		return doc;
	}
	
	public PdfPTable createConsolidatedProjectsTable() throws Exception { 
		
		PdfPTable table = new PdfPTable( 3 );
		
		table.setTotalWidth( new float[]{ 80, 250, 80 });
		table.setLockedWidth( true );
		
		addHeaderCell( table, app.getResourceStr( "reporting.projects.caption" ), 2 );
		addHeaderCell( table, app.getResourceStr( "general.table.header.hours" ));
		
		if ( pr != null ) {
			PdfPCell cell;
			for ( AggregateItem item : pr.getChildsList()) {

				ProjectItem prjItem = ( ProjectItem )item;
				
				table.addCell( new PdfPCell( new Phrase( StringUtils.padRightSpaces( prjItem.getProject().getCode(), 8 ))));
				
				table.addCell( prjItem.getProject().getName());
				
				cell = new PdfPCell( new Phrase( String.format( "%.1f", prjItem.getHours())));
				cell.setPaddingRight( 10 );
				cell.setHorizontalAlignment( Element.ALIGN_RIGHT );
				table.addCell( cell );

				
			}

			cell = new PdfPCell( new Phrase( app.getResourceStr( "reporting.item.total" ), headerFontBig ));
			cell.setHorizontalAlignment( Element.ALIGN_RIGHT );
			cell.setColspan( 2 );
			table.addCell( cell );
			
			cell = new PdfPCell( new Phrase( String.format( "%.1f", pr.getHours()), headerFontBig ));
			cell.setPaddingRight( 10 );
			cell.setHorizontalAlignment( Element.ALIGN_RIGHT );
			table.addCell( cell );

			
		}
    
		table.setHeaderRows( 1 );
		
		return table;
	}

	private PdfPTable createPersonnelTable() throws Exception { 
		
		PdfPTable table = new PdfPTable( 5 );
		
		table.setTotalWidth( new float[]{ 60, 60, 80, 230, 60 });
		table.setLockedWidth( true );
		table.getDefaultCell().setBorder( Rectangle.NO_BORDER );
		
		if ( pr != null ) {
			PdfPCell cell;
			// Write header if necessary
			cell = new PdfPCell( new Phrase( "" ));
			cell.setColspan( 5 );
			cell.setBorder( Rectangle.NO_BORDER );
			table.addCell( cell );
	
	
			ProjectItem prjItem;

			for ( AggregateItem item : pr.getChildsList()) {

				prjItem = ( ProjectItem )item;
				
				if ( prjItem.getHours() != 0 ) {

					cell = new PdfPCell( new Phrase( StringUtils.padRightSpaces( prjItem.getProject().getCode(), 8 ), headerFontBig )); 
					cell.setBorder( Rectangle.TOP + Rectangle.BOTTOM );
					cell.setBorderWidthTop( 3f );
					cell.setUseBorderPadding( true );					
					table.addCell( cell );

					cell = new PdfPCell( new Phrase( 
							prjItem.getProject().getName()
								+ " ( "
								+ String.format( "%.1f", prjItem.getHours())
								+ " " + app.getResourceStr( "general.table.header.hours" ) + " )",
							headerFontBig )); 
					cell.setColspan( 3 );
					cell.setBorder( Rectangle.TOP + Rectangle.BOTTOM );
					cell.setBorderWidthTop( 3f );
					cell.setUseBorderPadding( true );					
					table.addCell( cell );
					
					cell = new PdfPCell( new Phrase( app.getResourceStr( "general.table.header.hours" ), headerFontBig ));
					cell.setBorder( Rectangle.TOP + Rectangle.BOTTOM );
					cell.setBorderWidthTop( 3f );
					cell.setUseBorderPadding( true );					
					table.addCell( cell );
					
					if ( prjItem.getChilds().size() > 0 ) {
						// Write header if necessary
						
						// List all personnel Report for this Project
						
						PersonItem personItem;
						
						for ( AggregateItem item2 : prjItem.getChildsList()) {

							personItem = ( PersonItem )item2;

							cell = new PdfPCell( new Phrase( "" ));
							cell.setBorder( Rectangle.NO_BORDER );
							table.addCell( cell );
							
							cell = new PdfPCell( new Phrase( StringUtils.padRightSpaces( personItem.getUser().getCode(), 8 ))); 
							cell.setBorder( cell.getBorder() - Rectangle.RIGHT );
							cell.setBorder( Rectangle.NO_BORDER );
							table.addCell( cell );
							
							cell = new PdfPCell( new Phrase( personItem.getUser().getLastAndFirstNames())); 
							cell.setColspan( 2 );
							cell.setBorder( cell.getBorder() - Rectangle.LEFT );
							cell.setBorder( Rectangle.NO_BORDER );
							table.addCell( cell );
						
							cell = new PdfPCell( new Phrase( String.format( "%.1f", personItem.getHours())));
							cell.setPaddingRight( 10 );
							cell.setHorizontalAlignment( Element.ALIGN_RIGHT );
							cell.setBorder( Rectangle.NO_BORDER );
							table.addCell( cell );

							if ( personItem.getChilds().size() > 0 && model.isProjectDateFlag()) {
								// Write header if necessary
								
								// List all personnel Report for this Project
								
								DateItem dateItem;
								
								for ( AggregateItem item3 : personItem.getChildsList()) {
									
									dateItem = ( DateItem )item3;

									cell = new PdfPCell( new Phrase( "" ));
									cell.setColspan( 5 );
									cell.setBorder( Rectangle.NO_BORDER );
									table.addCell( cell );
									
									cell = new PdfPCell( new Phrase( "" ));
									cell.setColspan( 2 );
									cell.setBorder( Rectangle.NO_BORDER );
									table.addCell( cell );
									
									cell = new PdfPCell( new Phrase( DateUtil.dateToString( dateItem.getDate())));
									cell.setHorizontalAlignment( Element.ALIGN_CENTER );
									cell.setBorder( Rectangle.NO_BORDER );
									table.addCell( cell );
									
									cell = new PdfPCell( new Phrase( "" ));
									cell.setBorder( Rectangle.NO_BORDER );
									table.addCell( cell );
								
									cell = new PdfPCell( new Phrase( String.format( "%.1f", dateItem.getHours())));
									cell.setPaddingRight( 10 );
									cell.setHorizontalAlignment( Element.ALIGN_RIGHT );
									cell.setBorder( Rectangle.NO_BORDER );
									table.addCell( cell );
									
								}
							}
						}
					}
					
					cell = new PdfPCell( new Phrase( "" ));
					cell.setColspan( 5 );
					cell.setBorder( Rectangle.NO_BORDER );
					table.addCell( cell );
				
				}
			}
		}
	    
//		table.setHeaderRows( 1 );
		
		return table;
	}
	
}
