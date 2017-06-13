package com.c2point.tms.web.reporting.pdf.documents;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.util.DateUtil;
import com.c2point.tms.web.application.TmsApplication;
import com.c2point.tms.web.reporting.checkinout.AggregateItem;
import com.c2point.tms.web.reporting.checkinout.DateItem;
import com.c2point.tms.web.reporting.checkinout.PersonItem2;
import com.c2point.tms.web.reporting.checkinout.ProjectItem2;
import com.c2point.tms.web.reporting.checkinout.UsersReport;
import com.c2point.tms.web.reporting.pdf.PdfDocTemplate;
import com.c2point.tms.web.ui.reportview.checkinout.ReportCheckInOutModel;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;

public class PersonnelCheckInOutReportPdf extends PdfDocTemplate {
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( PersonnelCheckInOutReportPdf.class.getName());

	private UsersReport pr;
	private ReportCheckInOutModel model;
	
	public PersonnelCheckInOutReportPdf( TmsApplication app, UsersReport pr, ReportCheckInOutModel model ) {
		super( app );
		
		this.pr		= pr;
		this.model 	= model;
	}
	
	@Override
	protected void printTitlePage() throws Exception {
		nextLine();

		addTitle( getApp().getResourceStr( "reporting.item.header.personnel" ) + " " );
		addTitle( model.getOrganisation().getName());
		nextLine();

		addSubtitle( getApp().getResourceStr( "reporting.item.header.period" ) + " " 
						+ DateUtil.dateToString( model.getStartDate()) 
						+ " - " 
						+ DateUtil.dateToString( model.getEndDate()));

		nextLine();

	}

	@Override
	protected void printLastPage() throws Exception {
	}

	@Override
	protected void printContent() throws Exception {

		addTable( createConsolidatedPersonnelTable());

		nextLine();
		
		if ( model.isPersonnelProjectsFlag()) {
			addTable( createProjectsTable());
		}
	}
	
	public Table createConsolidatedPersonnelTable() throws Exception { 
		
		float[] columns = { 80, 250, 80 };
		Table table = new Table( columns );
		table.setWidthPercent(100);
		
		addHeaderCell( table, getApp().getResourceStr( "general.table.header.employee" ), 2 );
		addHeaderCell( table, getApp().getResourceStr( "general.table.header.hours" ));
		
		if ( pr != null ) {

			for ( AggregateItem item : pr.getChildsList()) {

				PersonItem2 personItem = ( PersonItem2 )item;
				
				if ( personItem.getMinutes() > 0 ) {
				
					add( table, personItem.getUser().getCode());
					add( table, personItem.getUser().getLastAndFirstNames());
					add( table, personItem.getHours());
				}
				
			}

			addFooterCell( table, getApp().getResourceStr( "reporting.item.total" ), 2 );
			addFooterCell( table, pr.getHours());

			
		}
    
		return table;
	}

	private Table createProjectsTable() throws Exception { 
		
		float[] columns = { 60, 60, 80, 200, 90 };
		Table table = new Table( columns );
		table.setWidthPercent(100);
		
		if ( pr != null ) {

			// Write header if necessary
			addEmptyCells( table, 5 ).setBorder( Border.NO_BORDER );
	
			PersonItem2 personItem;

			for ( AggregateItem item : pr.getChildsList()) {

				personItem = ( PersonItem2 )item;
				
				if ( personItem.getHours() != 0 ) {

					
					add( table, personItem.getUser().getCode(), HorizontalAlignment.CENTER, headerFontBig, 14 )
					.setBorder( Border.NO_BORDER )
					.setBorderTop( new SolidBorder( 3 ))
					.setBorderBottom( new SolidBorder( 1 ));
				addCell( table, personItem.getUser().getLastAndFirstNames(), 3, headerFontBig, 14, false )
					.setBorder( Border.NO_BORDER )
					.setBorderTop( new SolidBorder( 3 ))
					.setBorderBottom( new SolidBorder( 1 ));
				add( table, getApp().getResourceStr( "general.table.header.hours" ) 
						+ " ( "
						+ String.format( "%.1f", personItem.getHours())
						+ " )"
						, HorizontalAlignment.CENTER, headerFontBig, 14 )
					.setBorder( Border.NO_BORDER )
					.setBorderTop( new SolidBorder( 3 ))
					.setBorderBottom( new SolidBorder( 1 ));
					
					if ( personItem.getChilds().size() > 0 ) {
						// Write header if necessary
						
						// List all personnel Report for this Project
						
						ProjectItem2 projectItem;

						for ( AggregateItem item2 : personItem.getChildsList()) {

							projectItem = ( ProjectItem2 )item2;

							if ( personItem.getHours() > 0 ) {
								
								add( table, " " )
									.setBorder( Border.NO_BORDER );
								
								add( table, projectItem.getProject().getCode())
									.setBorder( Border.NO_BORDER );
								
								add( table, projectItem.getProject().getName(), 2 )
									.setBorder( Border.NO_BORDER );
							
								add( table, projectItem.getHours())
								.setBorder( Border.NO_BORDER );
	
								if ( projectItem.getChilds().size() > 0 && model.isPersonnelDateFlag()) {
									// Write header if necessary
									
									// List all personnel Report for this Project
									
									DateItem dateItem;
									
									for ( AggregateItem item3 : projectItem.getChildsList()) {
										
										dateItem = ( DateItem )item3;
	
										if ( dateItem.getHours() > 0 ) {
											
											addEmptyCells( table, 5 ).setBorder( Border.NO_BORDER );
											addEmptyCells( table, 2 ).setBorder( Border.NO_BORDER );
											
											add( table, dateItem.getDate()).setBorder( Border.NO_BORDER );
											add( table, "" ).setBorder( Border.NO_BORDER );
											add( table, dateItem.getHours()).setBorder( Border.NO_BORDER );
											
										}
									}
								}
							}
						}
					}
					
					addEmptyCells( table, 5 ).setBorder( Border.NO_BORDER );
				
				}
			}
		}
	    
		
		return table;
	}
	
}
