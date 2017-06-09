package com.c2point.tms.web.reporting.pdf.documents;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.TravelType;
import com.c2point.tms.util.DateUtil;
import com.c2point.tms.web.application.TmsApplication;
import com.c2point.tms.web.reporting.pdf.PdfDocTemplate;
import com.c2point.tms.web.reporting.taskandtravel.PrjItem;
import com.c2point.tms.web.reporting.taskandtravel.ProjectsReport;
import com.c2point.tms.web.reporting.taskandtravel.TaskItem;
import com.c2point.tms.web.reporting.taskandtravel.TravelItem;
import com.c2point.tms.web.ui.reportview.tasktravel.ReportTaskTravelModel;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.Property;

public class ProjectsReportPdf extends PdfDocTemplate {

	private static Logger logger = LogManager.getLogger( ProjectsReportPdf.class.getName());

	private ProjectsReport pr;
	private ReportTaskTravelModel model;
	
	public ProjectsReportPdf( TmsApplication app, ProjectsReport pr, ReportTaskTravelModel model ) {
		super( app );
		
		this.pr		= pr;
		this.model 	= model;
	}
	
	@Override
	protected void printTitlePage() throws Exception {
		logger.debug( "Report: Start Title creation...!" );

		
		nextLine();
		
		addTitle( getApp().getResourceStr( "reporting.item.header.projects" ) + " " );
		addTitle( model.getOrganisation().getName());
		nextLine();
		
		addSubtitle( getApp().getResourceStr( "reporting.item.header.period" ) + " " 
						+ DateUtil.dateToString( model.getStartDate()) 
						+ " - " 
						+ DateUtil.dateToString( model.getEndDate()));

		nextLine();
		
		logger.debug( "Report: End Title creation...!" );
	}



	@Override
	protected void printLastPage() throws Exception {
		logger.debug( "Report: Start LastPage creation...!" );
		
		logger.debug( "Report: End LastPage creation...!" );
	}



	@Override
	protected void printContent() throws Exception {
		logger.debug( "Report: Start Content creation...!" );

		addTable( createConsolidatedProjectsTable());

		nextLine();
		
		addTable( createProjectsTable()); 
		
		logger.debug( "Report: End Content creation...!" );
	}
	
	
	public Table createConsolidatedProjectsTable() throws Exception { 
		
		float[] columns = { 150, 60, 80, 80 };
		Table table = new Table( columns );
		table.setWidthPercent(100);
		
		addHeaderCell( table, getApp().getResourceStr( "reporting.projects.caption" ));
		addHeaderCell( table, getApp().getResourceStr( "general.table.header.hours" ));
		addHeaderCell( table, getApp().getResourceStr( "reporting.item.tyomatka" ));
		addHeaderCell( table, getApp().getResourceStr( "reporting.item.tyoajo" ));
		
		if ( pr != null ) {
			for ( PrjItem item : pr.values()) {
				add( table, item.getProject().getName());
				add( table,item.getHours());
				add( table,item.getMatka());
				add( table,item.getAjo());
			}
			addFooterCell( table, getApp().getResourceStr( "reporting.item.total" ));
			addFooterCell( table, String.format( "%.1f", pr.getHours()));
			addFooterCell( table, Integer.toString( pr.getMatka()));
			addFooterCell( table, Integer.toString( pr.getAjo()));
			
			
			
		}
    
		return table;
	}

	private Table createProjectsTable() throws Exception { 
		
		float[] columns = { 40, 40, 140, 60, 60, 60 };
		Table table = new Table( columns );
		table.setWidthPercent(100);
		table.setProperty(Property.BORDER, Border.NO_BORDER);
		
		if ( pr != null ) {
			// Write header if necessary
			addEmptyCells( table, 6 ).setBorder( Border.NO_BORDER );

			add( table, getApp().getResourceStr( "reporting.projects.and.tasks.caption" ), 3 )
				.setBorder( Border.NO_BORDER )
				.setBorderTop( new SolidBorder( 3 ))
				.setBorderBottom( new SolidBorder( 1 ));
	
			add( table, getApp().getResourceStr( "general.table.header.hours" ), 3 )
				.setBorder( Border.NO_BORDER )
				.setBorderTop( new SolidBorder( 3 ))
				.setBorderBottom( new SolidBorder( 1 ));
	
			add( table, getApp().getResourceStr( "reporting.item.tyomatka" ), 3 )
				.setBorder( Border.NO_BORDER )
				.setBorderTop( new SolidBorder( 3 ))
				.setBorderBottom( new SolidBorder( 1 ));
			
			add( table, getApp().getResourceStr( "reporting.item.tyoajo" ), 3 )
				.setBorder( Border.NO_BORDER )
				.setBorderTop( new SolidBorder( 3 ))
				.setBorderBottom( new SolidBorder( 1 ));

			for ( PrjItem prjItem : pr.values()) {
				
				if ( prjItem.getHours() != 0 || prjItem.getMatka() != 0 || prjItem.getAjo() != 0 ) {
				
					add( table, prjItem.getProject().getCode()).setBorder( Border.NO_BORDER );
					add( table, prjItem.getProject().getName(), 2 ).setBorder( Border.NO_BORDER );
					add( table, prjItem.getHours()).setBorder( Border.NO_BORDER );
					add( table, prjItem.getMatka()).setBorder( Border.NO_BORDER );
					add( table, prjItem.getAjo()).setBorder( Border.NO_BORDER );

					if ( prjItem.getTaskItems().size() > 0 && model.isTasksFlag_2()) {
						// Write header if necessary
						
						// List all Time Task reports
						for ( TaskItem taskItem : prjItem.getTaskItems()) {
							
							add( table, "" ).setBorder( Border.NO_BORDER );
							add( table, taskItem.getTask().getCode()).setBorder( Border.NO_BORDER );
							add( table, taskItem.getTask().getName()).setBorder( Border.NO_BORDER );
							add( table, taskItem.getHours()).setBorder( Border.NO_BORDER );
							addEmptyCells( table, 2 ).setBorder( Border.NO_BORDER );
						}
					}
					// List all traveling
					if ( prjItem.getTravelItems().size() > 0 && model.isTravelFlag_2()) {
						// Write header if necessary
	
						add( table, getApp().getResourceStr( "reporting.item.travel" ), HorizontalAlignment.CENTER, subheaderFontBig, 12 )
							.setBorder( Border.NO_BORDER );
						
						addEmptyCells( table, 5 ).setBorder( Border.NO_BORDER );

						// List all Travels
						for ( TravelItem travelItem : prjItem.getTravelItems()) {
							
							add( table, "" ).setBorder( Border.NO_BORDER );
						
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
	    
//		table.setHeaderRows( 1 );
		
		return table;
	}

}
