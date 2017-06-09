package com.c2point.tms.web.reporting.pdf.documents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.AbstractReport;
import com.c2point.tms.entity.TaskReport;
import com.c2point.tms.entity.TravelReport;
import com.c2point.tms.util.DateUtil;
import com.c2point.tms.util.StringUtils;
import com.c2point.tms.web.application.TmsApplication;
import com.c2point.tms.web.reporting.pdf.PdfDocTemplate;
import com.c2point.tms.web.ui.approveview.model.ApproveModel;
import com.c2point.tms.web.ui.approveview.model.ProjectHolder;
import com.c2point.tms.web.ui.approveview.model.TmsUserHolder;
import com.c2point.tms.web.util.ToStringConverter;
import com.itextpdf.layout.element.Table;


public class ApprovalViewPdf extends PdfDocTemplate {

	private static Logger logger = LogManager.getLogger( ApprovalViewPdf.class.getName());
	
	private   ApproveModel model;
	
	public ApprovalViewPdf( TmsApplication app, ApproveModel model ) {
		super( app );

		this.model = model;
	}
	
	@Override
	protected void printTitlePage() {
		logger.debug( "Report: Start Title creation...!" );
			
		nextLine();
		
		addTitle( getApp().getResourceStr( "menu.item.report.time" ));
		nextLine();

		addSubtitle( getApp().getResourceStr( "general.table.header.employee" ) + ": "
				+ model.getSelectedUser().getTmsUser().getFirstAndLastNames());
		
		addSubtitle( getApp().getResourceStr( "reporting.item.header.period" ) + " " 
				+ DateUtil.dateToString( model.getStartDate()) 
				+ " - " 
				+ DateUtil.dateToString( model.getEndDate()));
		nextLine();
		
		logger.debug( "Report: End Title creation...!" );
	}
	@Override
	protected void printLastPage() {
		logger.debug( "Report: Start LastPage creation...!" );
		
		logger.debug( "Report: End LastPage creation...!" );
	}
	@Override
	protected void printContent() throws Exception {
		logger.debug( "Report: Start Content creation...!" );
		
		addTable( createTimeReportTable());
		
		newPage();
		
		
		nextLine();
		addTitle( getApp().getResourceStr( "menu.item.report.travel" ));
		nextLine();
		
		addSubtitle( getApp().getResourceStr( "general.table.header.employee" ) + ": "
				+ model.getSelectedUser().getTmsUser().getFirstAndLastNames());
		
		addSubtitle( getApp().getResourceStr( "reporting.item.header.period" ) + " " 
				+ DateUtil.dateToString( model.getStartDate()) 
				+ " - " 
				+ DateUtil.dateToString( model.getEndDate()));
		nextLine();

		addTable( createTravelReportTable());
		
		logger.debug( "Report: End Content creation...!" );
	}

	
	
	private <T extends AbstractReport> java.util.List<T> sortByDate( java.util.List<T> arList ) {
		
		Collections.sort( arList, new Comparator<AbstractReport>() {
		    public int compare( AbstractReport ar1, AbstractReport ar2 ) {
		    	
				if ( ar1.getDate() == null ) return -1;
				if ( ar2.getDate() == null ) return 1;
				
		        return ar1.getDate().compareTo( ar2.getDate());
		    }
		});
		
		return arList;
	}

	private Table createTimeReportTable() throws Exception { 
		
		float[] columns = { 65, 170, 190, 55, 80 };
		Table table = new Table( columns );
		table.setWidthPercent(100);
		
//		table.setLockedWidth( true );
		
		addHeaderCell( table, getApp().getResourceStr( "general.table.header.date" ));
		addHeaderCell( table, getApp().getResourceStr( "general.table.header.project" ));
		addHeaderCell( table, getApp().getResourceStr( "general.table.header.task" ));
		addHeaderCell( table, getApp().getResourceStr( "general.table.header.hours" ));
		addHeaderCell( table, getApp().getResourceStr( "general.table.header.status" ));
		
		List<TaskReport> reportsList = new ArrayList<TaskReport>();

		TmsUserHolder uh = model.getSelectedUser();
		
		if ( uh != null && uh.values().size() > 0 ) {

			for ( ProjectHolder ph : uh.values()) {

				reportsList.addAll( ph.getTaskReports());
				
			}
				
		}
			
		if ( reportsList != null ) {
			
			String str;
			for ( TaskReport report : sortByDate( reportsList )) {
				add( table, report.getDate());
				
				try {
					str = StringUtils.padRightSpaces( report.getProjectTask().getProject().getCode(), 8 )
						+ report.getProjectTask().getProject().getName();
				} catch ( Exception e ) {
					str = "???";
				}
//				cell.setBorder( border ); //cell.getBorder() - Rectangle.RIGHT );
				add( table, str );

				
				try {
					str = StringUtils.padRightSpaces( report.getProjectTask().getCodeInProject(), 12 )
						+ report.getProjectTask().getTask().getName();
				} catch ( Exception e ) {
					str = "???";
				}
//				cell.setBorder( border ); //cell.getBorder() - Rectangle.RIGHT );
				add( table, str );

				add( table, report.getHours());
				
				add( table, ToStringConverter.convertToString( getApp(), report.getApprovalFlagType()) );
				
			}
		}
    
		
		return table;
	}

	private Table createTravelReportTable() throws Exception { 
		
		float[] columns = { 65, 170, 60, 60, 140, 70 };
		Table table = new Table( columns );
		table.setWidthPercent(100);
		
//		table.setLockedWidth( true );
		
		addHeaderCell( table, getApp().getResourceStr( "general.table.header.date" ));
		addHeaderCell( table, getApp().getResourceStr( "general.table.header.project" ));
		addHeaderCell( table, getApp().getResourceStr( "general.table.header.type" ) );
		addHeaderCell( table, getApp().getResourceStr( "general.table.header.distance" ) );
		addHeaderCell( table, getApp().getResourceStr( "general.table.header.route" ));
		addHeaderCell( table, getApp().getResourceStr( "general.table.header.status" ));

		List<TravelReport> reportsList = new ArrayList<TravelReport>();

		TmsUserHolder uh = model.getSelectedUser();
		
		if ( uh != null && uh.values().size() > 0 ) {

			for ( ProjectHolder ph : uh.values()) {

				reportsList.addAll( ph.getTravelReports());
				
			}
				
		}
			
		if ( reportsList != null ) {
			String str;
			for ( TravelReport report : sortByDate( reportsList )) {

				add( table, report.getDate());
				
				try {
					str = StringUtils.padRightSpaces( report.getProject().getCode(), 8 )
						+ report.getProject().getName();
				} catch ( Exception e ) {
					str = "???";
				}
				add( table, str );

				add( table, ToStringConverter.convertToString( getApp(), report.getTravelType()));

				add( table, report.getDistance(), " km" );

				add( table, report.getRoute());
				
				add( table, ToStringConverter.convertToString( getApp(), report.getApprovalFlagType()));
				
			}
		}
    
		return table;
	}

	

}
