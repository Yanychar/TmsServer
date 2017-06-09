package com.c2point.tms.web.reporting.pdf.documents;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.util.DateUtil;
import com.c2point.tms.web.application.TmsApplication;
import com.c2point.tms.web.reporting.checkinout.AggregateItem;
import com.c2point.tms.web.reporting.checkinout.DateItem;
import com.c2point.tms.web.reporting.checkinout.PersonItem;
import com.c2point.tms.web.reporting.checkinout.ProjectItem;
import com.c2point.tms.web.reporting.checkinout.ProjectsReport;
import com.c2point.tms.web.reporting.pdf.PdfDocTemplate;
import com.c2point.tms.web.ui.reportview.checkinout.ReportCheckInOutModel;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Table;

public class ProjectsCheckInOutReportPdf extends PdfDocTemplate {

	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( ProjectsCheckInOutReportPdf.class.getName());

	private ProjectsReport pr;
	private ReportCheckInOutModel model;
	
	public ProjectsCheckInOutReportPdf( TmsApplication app, ProjectsReport pr, ReportCheckInOutModel model ) {
		super( app );
		
		this.pr		= pr;
		this.model 	= model;
	}
	
	@Override
	protected void printTitlePage() throws Exception {

		nextLine();

		addTitle( getApp().getResourceStr( "reporting.item.header.projects" ) + " " );
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
		// TODO Auto-generated method stub
		
	}



	@Override
	protected void printContent() throws Exception {

		addTable( createConsolidatedProjectsTable());

		nextLine();
		
		if ( model.isProjectPersonnelFlag()) {
			addTable( createPersonnelTable());
		}
		
	}
	
	public Table createConsolidatedProjectsTable() throws Exception { 
		
		float[] columns = { 80, 250, 80 };
		Table table = new Table( columns );
		table.setWidthPercent(100);
		
		addHeaderCell( table, getApp().getResourceStr( "reporting.projects.caption" ), 2 );
		addHeaderCell( table, getApp().getResourceStr( "general.table.header.hours" ));
		
		if ( pr != null ) {

			for ( AggregateItem item : pr.getChildsList()) {

				ProjectItem prjItem = ( ProjectItem )item;
				
				add( table, prjItem.getProject().getCode());
				add( table, prjItem.getProject().getName());
				add( table, prjItem.getHours());

			}

			addFooterCell( table, getApp().getResourceStr( "reporting.item.total" ), 2 );
			addFooterCell( table, pr.getHours());
			
		}
		
		return table;
	}

	private Table createPersonnelTable() throws Exception { 
		
		float[] columns = { 60, 60, 80, 230, 60 };
		Table table = new Table( columns );
		table.setWidthPercent(100);
		
		if ( pr != null ) {

			// Write header if necessary
			addEmptyCells( table, 5 ).setBorder( Border.NO_BORDER );
	
	
			ProjectItem prjItem;

			for ( AggregateItem item : pr.getChildsList()) {

				prjItem = ( ProjectItem )item;
				
				if ( prjItem.getHours() > 0 ) {
/*
					addHeaderCell( table, prjItem.getProject().getCode())
						.setBorder( Border.NO_BORDER )
						.setBorderTop( new SolidBorder( 3 ))
						.setBorderBottom( new SolidBorder( 1 ));

					addHeaderCell( table, prjItem.getProject().getName()
							+ " ( "
							+ String.format( "%.1f", prjItem.getHours())
							+ " " + getApp().getResourceStr( "general.table.header.hours" ) + " )", 3 )
						.setBorder( Border.NO_BORDER )
						.setBorderTop( new SolidBorder( 3 ))
						.setBorderBottom( new SolidBorder( 1 ));
					
					addHeaderCell( table, getApp().getResourceStr( "general.table.header.hours" ))
						.setBorder( Border.NO_BORDER )
						.setBorderTop( new SolidBorder( 3 ))
						.setBorderBottom( new SolidBorder( 1 ));
*/
					add( table, "AAA" )
						.setBorder( Border.NO_BORDER )
						.setBorderTop( new SolidBorder( 3 ))
						.setBorderBottom( new SolidBorder( 1 ));
					add( table, "BBB", 3 )
						.setBorder( Border.NO_BORDER )
						.setBorderTop( new SolidBorder( 3 ))
						.setBorderBottom( new SolidBorder( 1 ));
					add( table, "FFF" )
						.setBorder( Border.NO_BORDER )
						.setBorderTop( new SolidBorder( 3 ))
						.setBorderBottom( new SolidBorder( 1 ));
					
					if ( prjItem.getChilds().size() > 0 ) {
						// Write header if necessary
						
						// List all personnel Report for this Project
						
						PersonItem personItem;
						
						for ( AggregateItem item2 : prjItem.getChildsList()) {

							personItem = ( PersonItem )item2;
							
							if ( personItem.getHours() > 0 ) {


								add( table, " " )
									.setBorder( Border.NO_BORDER );
										
								add( table, personItem.getUser().getLastAndFirstNames(), 3 )
									.setBorder( Border.NO_BORDER );
								
							
								add( table, personItem.getHours())
									.setBorder( Border.NO_BORDER );

//								/*					
								
								if ( personItem.getChilds().size() > 0 && model.isProjectDateFlag()) {
									// Write header if necessary
									
									// List all personnel Report for this Project
									
									DateItem dateItem;
									
									for ( AggregateItem item3 : personItem.getChildsList()) {
										
										dateItem = ( DateItem )item3;
										
										if ( dateItem.getHours() > 0 ) {
	
											addEmptyCells( table, 5 ).setBorder( Border.NO_BORDER );
											
											addEmptyCells( table, 2 ).setBorder( Border.NO_BORDER );
											
											add( table, dateItem.getDate())
												.setBorder( Border.NO_BORDER );
											
											add( table, "" )
												.setBorder( Border.NO_BORDER );
										
											add( table, dateItem.getHours())
												.setBorder( Border.NO_BORDER );
										
										}
									}
								}
//*/
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
