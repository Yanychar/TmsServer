package com.c2point.tms.web.reporting.pdf.documents;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.TravelType;
import com.c2point.tms.util.DateUtil;
import com.c2point.tms.web.application.TmsApplication;
import com.c2point.tms.web.reporting.pdf.PdfDocTemplate;
import com.c2point.tms.web.reporting.taskandtravel.DateItem;
import com.c2point.tms.web.reporting.taskandtravel.PrjItem;
import com.c2point.tms.web.reporting.taskandtravel.TaskItem;
import com.c2point.tms.web.reporting.taskandtravel.TravelItem;
import com.c2point.tms.web.reporting.taskandtravel.UserItem;
import com.c2point.tms.web.reporting.taskandtravel.UsersReport;
import com.c2point.tms.web.ui.reportview.tasktravel.ReportTaskTravelModel;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.Property;

public class PersonnelReportPdf extends PdfDocTemplate {

	private static Logger logger = LogManager.getLogger( PersonnelReportPdf.class.getName());

	private UsersReport ur;
	private ReportTaskTravelModel model;
	
	public PersonnelReportPdf( TmsApplication app, UsersReport ur, ReportTaskTravelModel model ) {
		super( app );
		
		this.ur		= ur;
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
		// TODO Auto-generated method stub
		
	}



	@Override
	protected void printContent() throws Exception {

		addTable( createConsolidatedPersonTable());

		nextLine();
		
		addTable( createPersonTablePerDate()); 
		
	}
	
	
	public Table createConsolidatedPersonTable() throws Exception { 
		
		float[] columns = { 150, 60, 80, 80 };
		Table table = new Table( columns );
		table.setWidthPercent(100);
		
		addHeaderCell( table, getApp().getResourceStr( "general.table.header.employee" ));
		addHeaderCell( table, getApp().getResourceStr( "general.table.header.hours" ));
		addHeaderCell( table, getApp().getResourceStr( "reporting.item.tyomatka" ));
		addHeaderCell( table, getApp().getResourceStr( "reporting.item.tyoajo" ));
		
		if ( ur != null ) {
			for ( UserItem item : ur.values()) {
				add( table, item.getUser().getLastAndFirstNames());
				
				add( table,item.getHours());

				add( table,item.getMatka());
				
				add( table,item.getAjo());
				
			}

			addFooterCell( table, getApp().getResourceStr( "reporting.item.total" ));
			addFooterCell( table, String.format( "%.1f", ur.getHours()));
			addFooterCell( table, Integer.toString( ur.getMatka()));
			addFooterCell( table, Integer.toString( ur.getAjo()));
			
		}
    
		return table;
	}

	private Table createPersonTablePerDate() throws Exception { 
		
		float[] columns = { 65, 40, 40, 140, 60, 60, 60 };
		Table table = new Table( columns );
		table.setWidthPercent(100);
		table.setProperty(Property.BORDER, Border.NO_BORDER);
		
		if ( ur != null ) {
			for ( UserItem userItem : ur.values()) {
				
				if ( userItem.getHours() != 0 || userItem.getMatka() != 0 || userItem.getAjo() != 0 ) {
					// new line
					if ( model.isDateFlag()) {
							// Write header if necessary
						addEmptyCells( table, 7 ).setBorder( Border.NO_BORDER );
						
						add( table, userItem.getUser().getLastAndFirstNames(), 4 )
							.setBorder( Border.NO_BORDER )
							.setBorderTop( new SolidBorder( 3 ))
							.setBorderBottom( new SolidBorder( 1 ));
							
						add( table, getApp().getResourceStr( "general.table.header.hours" ))
							.setBorder( Border.NO_BORDER )
							.setBorderTop( new SolidBorder( 3 ))
							.setBorderBottom( new SolidBorder( 1 ));
						
						add( table, getApp().getResourceStr( "reporting.item.tyomatka" ))
							.setBorder( Border.NO_BORDER )
							.setBorderTop( new SolidBorder( 3 ))
							.setBorderBottom( new SolidBorder( 1 ));

						add( table, getApp().getResourceStr( "reporting.item.tyoajo" ))
							.setBorder( Border.NO_BORDER )
							.setBorderTop( new SolidBorder( 3 ))
							.setBorderBottom( new SolidBorder( 1 ));
						
						for ( DateItem dateItem : userItem.values()) {
		
							add( table, dateItem.getDate()).setBorder( Border.NO_BORDER );
							addEmptyCells( table, 3 ).setBorder( Border.NO_BORDER );
							add( table, dateItem.getHours()).setBorder( Border.NO_BORDER );
							add( table, dateItem.getMatka()).setBorder( Border.NO_BORDER );
							add( table, dateItem.getAjo()).setBorder( Border.NO_BORDER );
							
							if ( model.isProjectsFlag() || model.isTasksFlag_1() || model.isTravelFlag_1()) {
								// Write header if necessary
									
								for ( PrjItem prjItem : dateItem.values()) {
								
									if ( model.isProjectsFlag() ) {
										
										logger.debug( "PrjItem: " + prjItem );
										
										add( table,  "" ).setBorder( Border.NO_BORDER );
										add( table, prjItem.getProject().getCode()).setBorder( Border.NO_BORDER );
										add( table, prjItem.getProject().getName(), 2 ).setBorder( Border.NO_BORDER );
										add( table, prjItem.getHours()).setBorder( Border.NO_BORDER );
										add( table, prjItem.getMatka()).setBorder( Border.NO_BORDER );
										add( table, prjItem.getAjo()).setBorder( Border.NO_BORDER );
										
									}
									if ( prjItem.getTaskItems().size() > 0 && model.isTasksFlag_1()) {
										// Write header if necessary
										
										// List all Time Task reports
										for ( TaskItem taskItem : prjItem.getTaskItems()) {
											
											addEmptyCells( table, 2 ).setBorder( Border.NO_BORDER );
											add( table, taskItem.getTask().getCode()).setBorder( Border.NO_BORDER );
											add( table, taskItem.getTask().getName()).setBorder( Border.NO_BORDER );
											add( table, taskItem.getHours()).setBorder( Border.NO_BORDER );
											addEmptyCells( table, 2 ).setBorder( Border.NO_BORDER );
										}
									}
									// List all traveling
									if ( prjItem.getTravelItems().size() > 0 && model.isTravelFlag_1()) {
										// Write header if necessary
										add( table,  "" ).setBorder( Border.NO_BORDER );
					
										add( table, getApp().getResourceStr( "reporting.item.travel" ), HorizontalAlignment.CENTER, subheaderFontBig, 12 )
											.setBorder( Border.NO_BORDER );
										
										addEmptyCells( table, 5 ).setBorder( Border.NO_BORDER );

										// List all Travels
										for ( TravelItem travelItem : prjItem.getTravelItems()) {
											
											addEmptyCells( table, 2 ).setBorder( Border.NO_BORDER );
										
											add( table,  travelItem.getReport().getRoute(), 2 ).setBorder( Border.NO_BORDER );
										
											add( table,  "" ).setBorder( Border.NO_BORDER );
			
											if ( travelItem.getTravelType() == TravelType.HOME ) {
												add( table,  travelItem.getDistance()).setBorder( Border.NO_BORDER );
												add( table,  "" ).setBorder( Border.NO_BORDER );
											} else {
												add( table,  "" ).setBorder( Border.NO_BORDER );
												add( table,  travelItem.getDistance()).setBorder( Border.NO_BORDER );
											}
											
										}
									}
								
								}
							}
						
						}
					}
				}
				
			}
		}
	    
		return table;
	}




}
