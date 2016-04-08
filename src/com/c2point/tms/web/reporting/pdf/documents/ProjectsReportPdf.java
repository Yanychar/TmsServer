package com.c2point.tms.web.reporting.pdf.documents;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.TravelType;
import com.c2point.tms.util.DateUtil;
import com.c2point.tms.util.StringUtils;
import com.c2point.tms.web.application.TmsApplication;
import com.c2point.tms.web.reporting.pdf.PdfTemplate;
import com.c2point.tms.web.reporting.taskandtravel.PrjItem;
import com.c2point.tms.web.reporting.taskandtravel.ProjectsReport;
import com.c2point.tms.web.reporting.taskandtravel.TaskItem;
import com.c2point.tms.web.reporting.taskandtravel.TravelItem;
import com.c2point.tms.web.ui.reportview.tasktravel.ReportTaskTravelModel;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

public class ProjectsReportPdf extends AbstractReportPdf {

	private static Logger logger = LogManager.getLogger( ProjectsReportPdf.class.getName());

	private ProjectsReport pr;
	private ReportTaskTravelModel model;
	
	public ProjectsReportPdf( TmsApplication app, ProjectsReport pr, ReportTaskTravelModel model ) {
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
			
			doc.getDocument().add( createProjectsTable()); 

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
		
		PdfPTable table = new PdfPTable( 4 );
		
		table.setTotalWidth( new float[]{ 150, 60, 80, 80 });
		table.setLockedWidth( true );
		
		addHeaderCell( table, app.getResourceStr( "reporting.projects.caption" ));
		addHeaderCell( table, app.getResourceStr( "general.table.header.hours" ));
		addHeaderCell( table, app.getResourceStr( "reporting.item.tyomatka" ));
		addHeaderCell( table, app.getResourceStr( "reporting.item.tyoajo" ));
		
		if ( pr != null ) {
			PdfPCell cell;
			for ( PrjItem item : pr.values()) {
				table.addCell( item.getProject().getName());
				
				cell = new PdfPCell( new Phrase( String.format( "%.1f", item.getHours())));
				cell.setPaddingRight( 10 );
				cell.setHorizontalAlignment( Element.ALIGN_RIGHT );
				table.addCell( cell );

				cell = new PdfPCell( new Phrase( Integer.toString( item.getMatka())));
				cell.setPaddingRight( 10 );
				cell.setHorizontalAlignment( Element.ALIGN_RIGHT );
				table.addCell( cell );
				
				cell = new PdfPCell( new Phrase( Integer.toString( item.getAjo())));
				cell.setHorizontalAlignment( Element.ALIGN_RIGHT );
				cell.setPaddingRight( 10 );
				table.addCell( cell );
				
			}

			cell = new PdfPCell( new Phrase( app.getResourceStr( "reporting.item.total" )));
			cell.setHorizontalAlignment( Element.ALIGN_RIGHT );
			table.addCell( cell );
			
			cell = new PdfPCell( new Phrase( String.format( "%.1f", pr.getHours()), headerFontBig ));
			cell.setPaddingRight( 10 );
			cell.setHorizontalAlignment( Element.ALIGN_RIGHT );
			table.addCell( cell );

			cell = new PdfPCell( new Phrase( Integer.toString( pr.getMatka()), headerFontBig ));
			cell.setPaddingRight( 10 );
			cell.setHorizontalAlignment( Element.ALIGN_RIGHT );
			table.addCell( cell );
			
			cell = new PdfPCell( new Phrase( Integer.toString( pr.getAjo()), headerFontBig ));
			cell.setPaddingRight( 10 );
			cell.setHorizontalAlignment( Element.ALIGN_RIGHT );
			table.addCell( cell );
			
			
			
		}
    
		table.setHeaderRows( 1 );
		
		return table;
	}

	private PdfPTable createProjectsTable() throws Exception { 
		
		PdfPTable table = new PdfPTable( 7 );
		
		table.setTotalWidth( new float[]{ 40, 40, 140, 60, 60, 60, 60 });
		table.setLockedWidth( true );
		table.getDefaultCell().setBorder( Rectangle.NO_BORDER );
		
		if ( pr != null ) {
			PdfPCell cell;
			// Write header if necessary
			cell = new PdfPCell( new Phrase( "" ));
			cell.setColspan( 7 );
			cell.setBorder( Rectangle.NO_BORDER );
			table.addCell( cell );
	
			cell = new PdfPCell( new Phrase( app.getResourceStr( "reporting.projects.and.tasks.caption" )));
			cell.setColspan( 3 );
			cell.setBorder( Rectangle.TOP + Rectangle.BOTTOM );
			cell.setBorderWidthTop( 3f );
	//		cell.setBorderWidthBottom( 1f );
			cell.setUseBorderPadding( true );					
			table.addCell( cell );
	
			cell = new PdfPCell( new Phrase( app.getResourceStr( "general.table.header.hours" )));
			cell.setBorder( Rectangle.TOP + Rectangle.BOTTOM );
			cell.setBorderWidthTop( 3f );
	//		cell.setBorderWidthBottom( 1f );
			cell.setUseBorderPadding( true );					
			table.addCell( cell );
	
			cell = new PdfPCell( new Phrase( app.getResourceStr( "reporting.item.measure" )));
			cell.setBorder( Rectangle.TOP + Rectangle.BOTTOM );
			cell.setBorderWidthTop( 3f );
//			cell.setBorderWidthBottom( 1f );
			cell.setUseBorderPadding( true );					
			table.addCell( cell );

			cell = new PdfPCell( new Phrase( app.getResourceStr( "reporting.item.tyomatka" )));
			cell.setBorder( Rectangle.TOP + Rectangle.BOTTOM );
			cell.setBorderWidthTop( 3f );
	//		cell.setBorderWidthBottom( 1f );
			cell.setUseBorderPadding( true );					
			table.addCell( cell );
			
			cell = new PdfPCell( new Phrase( app.getResourceStr( "reporting.item.tyoajo" )));
			cell.setBorder( Rectangle.TOP + Rectangle.BOTTOM );
			cell.setBorderWidthTop( 3f );
	//		cell.setBorderWidthBottom( 1f );
			cell.setUseBorderPadding( true );					
			table.addCell( cell );
			for ( PrjItem prjItem : pr.values()) {
				
				if ( prjItem.getHours() != 0 || prjItem.getMatka() != 0 || prjItem.getAjo() != 0 ) {
				
					cell = new PdfPCell( new Phrase( StringUtils.padRightSpaces( prjItem.getProject().getCode(), 8 ))); 
					cell.setBorder( cell.getBorder() - Rectangle.RIGHT );
					cell.setBorder( Rectangle.TOP );
					cell.setBorderWidthTop( 0.5f );
					table.addCell( cell );
					
					cell = new PdfPCell( new Phrase( prjItem.getProject().getName())); 
					cell.setColspan( 2 );
					cell.setBorder( cell.getBorder() - Rectangle.LEFT );
					cell.setBorder( Rectangle.TOP );
					cell.setBorderWidthTop( 0.5f );
					table.addCell( cell );
				
					cell = new PdfPCell( new Phrase( String.format( "%.1f", prjItem.getHours())));
					cell.setPaddingRight( 10 );
					cell.setHorizontalAlignment( Element.ALIGN_RIGHT );
					cell.setBorder( Rectangle.TOP );
					cell.setBorderWidthTop( 0.5f );
					table.addCell( cell );

					cell = new PdfPCell();
					cell.setBorder( Rectangle.TOP );
					cell.setBorderWidthTop( 0.5f );
					table.addCell( cell );
					
					cell = new PdfPCell( new Phrase( Integer.toString( prjItem.getMatka())));
					cell.setPaddingRight( 10 );
					cell.setHorizontalAlignment( Element.ALIGN_RIGHT );
					cell.setBorder( Rectangle.TOP );
					cell.setBorderWidthTop( 0.5f );
					table.addCell( cell );
					
					cell = new PdfPCell( new Phrase( Integer.toString( prjItem.getAjo())));
					cell.setHorizontalAlignment( Element.ALIGN_RIGHT );
					cell.setPaddingRight( 10 );
					cell.setBorder( Rectangle.TOP );
					cell.setBorderWidthTop( 0.5f );
					table.addCell( cell );
					if ( prjItem.getTaskItems().size() > 0 && model.isTasksFlag_2()) {
						// Write header if necessary
						
						// List all Time Task reports
						for ( TaskItem taskItem : prjItem.getTaskItems()) {
							
							cell = new PdfPCell();
							cell.setBorder( Rectangle.NO_BORDER );
							table.addCell( cell );
						
							cell = new PdfPCell( new Phrase( StringUtils.padRightSpaces( taskItem.getTask().getCode(), 8 ))); 
							cell.setBorder( cell.getBorder() - Rectangle.RIGHT );
							cell.setBorder( Rectangle.NO_BORDER );
							table.addCell( cell );
							cell = new PdfPCell( new Phrase( taskItem.getTask().getName())); 
							cell.setBorder( cell.getBorder() - Rectangle.LEFT );
							cell.setBorder( Rectangle.NO_BORDER );
							table.addCell( cell );
						
							cell = new PdfPCell( new Phrase( String.format( "%.1f", taskItem.getHours())));
							cell.setPaddingRight( 10 );
							cell.setHorizontalAlignment( Element.ALIGN_RIGHT );
							cell.setBorder( Rectangle.NO_BORDER );
							table.addCell( cell );

							if ( taskItem.getNumValue() > 0 ) {
								cell = new PdfPCell( new Phrase( String.format( "%.1f", taskItem.getNumValue()) 
										+ " " + taskItem.getNumValueMeasure()
									));
								
							} else {
								cell = new PdfPCell();
							}
							cell.setPaddingRight( 10 );
							cell.setHorizontalAlignment( Element.ALIGN_RIGHT );
							cell.setBorder( Rectangle.NO_BORDER );
							table.addCell( cell );

							cell = new PdfPCell();
							cell.setColspan( 2 );
							cell.setBorder( Rectangle.NO_BORDER );
							table.addCell( cell );
						}
					}
					// List all traveling
					if ( prjItem.getTravelItems().size() > 0 && model.isTravelFlag_2()) {
						// Write header if necessary
	
						cell = new PdfPCell( new Phrase( app.getResourceStr( "reporting.item.travel" ), subheaderFontBig ));
						cell.setColspan( 2 );
						cell.setBorder( Rectangle.NO_BORDER );
						table.addCell( cell );
						
						cell = new PdfPCell();
						cell.setColspan( 5 );
						cell.setBorder( Rectangle.NO_BORDER );
						table.addCell( cell );

						// List all Travels
						for ( TravelItem travelItem : prjItem.getTravelItems()) {
							
							cell = new PdfPCell();
							cell.setBorder( Rectangle.NO_BORDER );
							table.addCell( cell );
						
							cell = new PdfPCell( new Phrase( travelItem.getReport().getRoute())); 
							cell.setBorder( cell.getBorder() - Rectangle.RIGHT );
							cell.setColspan( 3 );
							cell.setBorder( Rectangle.NO_BORDER );
							table.addCell( cell );
						
							table.addCell( "" );

							cell = new PdfPCell( new Phrase( Integer.toString( travelItem.getDistance())));
							cell.setPaddingRight( 10 );
							cell.setHorizontalAlignment( Element.ALIGN_RIGHT );
							cell.setBorder( Rectangle.NO_BORDER );
							if ( travelItem.getTravelType() == TravelType.HOME ) {
								table.addCell( cell );
								table.addCell( "" );
							} else {
								table.addCell( "" );
								table.addCell( cell );
							}
							
						}
					}
				
				}
			}
		}
	    
//		table.setHeaderRows( 1 );
		
		return table;
	}
	
}
