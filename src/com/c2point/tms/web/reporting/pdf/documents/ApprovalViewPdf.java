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
import com.c2point.tms.web.reporting.pdf.PdfTemplate;
import com.c2point.tms.web.ui.approveview.model.ApproveModel;
import com.c2point.tms.web.ui.approveview.model.ProjectHolder;
import com.c2point.tms.web.ui.approveview.model.TmsUserHolder;
import com.c2point.tms.web.util.ToStringConverter;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

public class ApprovalViewPdf extends AbstractReportPdf {

	private static Logger logger = LogManager.getLogger( ApprovalViewPdf.class.getName());
	
	private   ApproveModel model;
	
	public ApprovalViewPdf( TmsApplication app, ApproveModel model ) {
		super( app );

		this.model = model;
	}
	
	public PdfTemplate create() { 
		
		PdfTemplate doc = new PdfTemplate();
		
		Paragraph docHeader = null;
		
		try {
			
			docHeader = new Paragraph();
			
			doc.nextLine( docHeader );
			addTitle( docHeader, app.getResourceStr( "menu.item.report.time" ));
			doc.nextLine( docHeader );

			addSubtitle( docHeader, app.getResourceStr( "general.table.header.employee" ) + ": "
					+ model.getSelectedUser().getTmsUser().getFirstAndLastNames());
			
			addSubtitle( docHeader, app.getResourceStr( "reporting.item.header.period" ) + " " 
					+ DateUtil.dateToString( model.getStartDate()) 
					+ " - " 
					+ DateUtil.dateToString( model.getEndDate()));
			doc.nextLine( docHeader );
	
			doc.getDocument().add( docHeader );

			doc.getDocument().add( createTimeReportTable());
			
			doc.newPage();
			
			docHeader = new Paragraph();
			
			doc.nextLine( docHeader );
			addTitle( docHeader, app.getResourceStr( "menu.item.report.travel" ));
			doc.nextLine( docHeader );
			
			addSubtitle( docHeader, app.getResourceStr( "general.table.header.employee" ) + ": "
					+ model.getSelectedUser().getTmsUser().getFirstAndLastNames());
			
			addSubtitle( docHeader, app.getResourceStr( "reporting.item.header.period" ) + " " 
					+ DateUtil.dateToString( model.getStartDate()) 
					+ " - " 
					+ DateUtil.dateToString( model.getEndDate()));
			doc.nextLine( docHeader );
	
			doc.getDocument().add( docHeader );
			
			doc.getDocument().add( createTravelReportTable());
			
    	} catch ( Exception e ) {                
    		logger.error( "Cannot create iText.Document and/or PdfWriter!" );
    	} finally {
			try {
				doc.endDoc();
			} catch ( Exception e ) {
			}
			try {
				docHeader.clear();
			} catch ( Exception e ) {
			}
    	}
		
		return doc;
	}

	private PdfPTable createTimeReportTable() throws Exception { 
		
		PdfPTable table = new PdfPTable( 7 );
		
		table.setTotalWidth( new float[]{ 65, 50, 120, 70, 120, 55, 80 });
		table.setLockedWidth( true );
		
		addHeaderCell( table, app.getResourceStr( "general.table.header.date" ));
		addHeaderCell( table, app.getResourceStr( "general.table.header.project" ), 2 );
		addHeaderCell( table, app.getResourceStr( "general.table.header.task" ), 2 );
		addHeaderCell( table, app.getResourceStr( "general.table.header.hours" ));
		addHeaderCell( table, app.getResourceStr( "general.table.header.status" ));
		
		List<TaskReport> reportsList = new ArrayList<TaskReport>();

		TmsUserHolder uh = model.getSelectedUser();
		
		if ( uh != null && uh.values().size() > 0 ) {

			for ( ProjectHolder ph : uh.values()) {

				reportsList.addAll( ph.getTaskReports());
				
			}
				
		}
			
		if ( reportsList != null ) {
			
			PdfPCell cell;
			String str;
			for ( TaskReport report : sortByDate( reportsList )) {
				table.addCell( DateUtil.dateToString( report.getDate()));
				
				try {
					str = StringUtils.padRightSpaces( report.getProjectTask().getProject().getCode(), 8 );
				} catch ( Exception e ) {
					str = "???";
				}
				cell = new PdfPCell( new Phrase( str )); 
				cell.setBorder( cell.getBorder() - Rectangle.RIGHT );
				table.addCell( cell );

				try {
					str = report.getProjectTask().getProject().getName();
				} catch ( Exception e ) {
					str = "*** ??? ***";
				}
				cell = new PdfPCell( new Phrase( str )); 
				cell.setBorder( cell.getBorder() - Rectangle.LEFT );
				table.addCell( cell );
				
				try {
					str = StringUtils.padRightSpaces( report.getProjectTask().getCodeInProject(), 12 );
				} catch ( Exception e ) {
					str = "???";
				}
				cell = new PdfPCell( new Phrase( str )); 
				cell.setBorder( cell.getBorder() - Rectangle.RIGHT );
				table.addCell( cell );

				try {
					str = report.getProjectTask().getTask().getName();
				} catch ( Exception e ) {
					str = "*** ??? ***";
				}
				cell = new PdfPCell( new Phrase( str )); 
				cell.setBorder( cell.getBorder() - Rectangle.LEFT );
				table.addCell( cell );
				
				cell = new PdfPCell( new Phrase( Float.toString( report.getHours())));
				cell.setHorizontalAlignment( Element.ALIGN_CENTER );
				table.addCell( cell );
				
				cell = new PdfPCell( new Phrase( ToStringConverter.convertToString( app, report.getApprovalFlagType())));
				cell.setHorizontalAlignment( Element.ALIGN_CENTER );
				table.addCell( cell );
				
			}
		}
    
		table.setHeaderRows( 1 );
		
		return table;
	}

	private PdfPTable createTravelReportTable() throws Exception { 
		
		PdfPTable table = new PdfPTable( 7 );
		
		table.setTotalWidth( new float[]{ 65, 50, 120, 60, 60, 140, 70 });
		table.setLockedWidth( true );
		
		addHeaderCell( table, app.getResourceStr( "general.table.header.date" ));
		addHeaderCell( table, app.getResourceStr( "general.table.header.project" ), 2 );
		addHeaderCell( table, app.getResourceStr( "general.table.header.type" ) );
		addHeaderCell( table, app.getResourceStr( "general.table.header.distance" ) );
		addHeaderCell( table, app.getResourceStr( "general.table.header.route" ));
		addHeaderCell( table, app.getResourceStr( "general.table.header.status" ));

		List<TravelReport> reportsList = new ArrayList<TravelReport>();

		TmsUserHolder uh = model.getSelectedUser();
		
		if ( uh != null && uh.values().size() > 0 ) {

			for ( ProjectHolder ph : uh.values()) {

				reportsList.addAll( ph.getTravelReports());
				
			}
				
		}
			
		if ( reportsList != null ) {
			PdfPCell cell;
			String str;
			for ( TravelReport report : sortByDate( reportsList )) {
				table.addCell( DateUtil.dateToString( report.getDate()));
				
				try {
					str = StringUtils.padRightSpaces( report.getProject().getCode(), 8 );
				} catch ( Exception e ) {
					str = "???";
				}
				cell = new PdfPCell( new Phrase( str )); 
				cell.setBorder( cell.getBorder() - Rectangle.RIGHT );
				table.addCell( cell );

				try {
					str = report.getProject().getName();
				} catch ( Exception e ) {
					str = "*** ??? ***";
				}
				cell = new PdfPCell( new Phrase( str )); 
				cell.setBorder( cell.getBorder() - Rectangle.LEFT );
				table.addCell( cell );
				
				cell = new PdfPCell( new Phrase( ToStringConverter.convertToString( app, report.getTravelType())));
				cell.setHorizontalAlignment( Element.ALIGN_CENTER );
				table.addCell( cell );

				cell = new PdfPCell( new Phrase( Integer.toString( report.getDistance()) + " km" ));
				cell.setHorizontalAlignment( Element.ALIGN_RIGHT );
				table.addCell( cell );

				table.addCell( report.getRoute());
				
				cell = new PdfPCell( new Phrase( ToStringConverter.convertToString( app, report.getApprovalFlagType())));
				cell.setHorizontalAlignment( Element.ALIGN_CENTER );
				table.addCell( cell );
				
			}
		}
    
		table.setHeaderRows( 1 );
		
		return table;
	}

/*	
	private List<TaskReport> sortTaskReport( List<TaskReport> arList ) {
		return ( TaskReport
	}
*/	
	private <T extends AbstractReport> List<T> sortByDate( List<T> arList ) {
		
		Collections.sort( arList, new Comparator<AbstractReport>() {
		    public int compare( AbstractReport ar1, AbstractReport ar2 ) {
		    	
				if ( ar1.getDate() == null ) return -1;
				if ( ar2.getDate() == null ) return 1;
				
		        return ar1.getDate().compareTo( ar2.getDate());
		    }
		});
		
		return arList;
	}
	

}
