package com.c2point.tms.web.ui.approveview.model;

import java.util.Collection;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.AbstractReport;
import com.c2point.tms.entity.Project;
import com.c2point.tms.entity.TaskReport;
import com.c2point.tms.entity.TravelReport;
import com.c2point.tms.entity.TravelType;
import com.c2point.tms.util.DateUtil;

public class ProjectHolder {
	
	private static Logger logger = LogManager.getLogger( ProjectHolder.class.getName());

	private Date	date;
	private Project project;
	private float	hours;
	private int		homeDistance;
	private int		workDistance;
	
	private SortedMap< String, TaskReport > taskReports = new TreeMap< String, TaskReport >();
	private SortedMap< String, TravelReport > travelReports = new TreeMap< String, TravelReport >();
	
	private int 	noValidated;
	private int		approved;
	private int		rejected;
	private int		processed;
	
	private ProjectHolder() {
		
		this.date = null;
		this.project = null;

		this.hours = 0;
		this.homeDistance = 0;
		this.workDistance = 0;

		this.noValidated = 0;
		this.approved = 0;
		this.rejected = 0;
		this.processed = 0;

	}

	private ProjectHolder( Date date ) {
		this();
		this.date = date;
	}

	public ProjectHolder( AbstractReport report) {

		this( report.getDate());
		
		if ( report instanceof TaskReport ) {
			
			this.project = (( TaskReport )report ).getProjectTask().getProject();
			
		} else if ( report instanceof TravelReport ) {
			
			this.project = (( TravelReport )report ).getProject();
			
		} 

	}
	
	public String getKey() {
		return DateUtil.dateNoDelimToString( this.date ) 
				+ ( this.project != null ? this.project.getCode().trim().toUpperCase() : "" );
	}
	
	public static String getKey( AbstractReport report ) {
		
		return DateUtil.dateNoDelimToString( report.getDate()) 
				+ ( report.getProject() != null ? report.getProject().getCode().trim().toUpperCase() : "" );
	}
	
	public Date getDate() { return date; }
	public Project getProject() { return project; }
	public float getHours() { return hours; }
	public int getHomeDistance() { return homeDistance; }
	public int getWorkDistance() { return workDistance; }
	public int getDistance() { return getHomeDistance() + getWorkDistance(); }
	
	public Collection< TaskReport> getTaskReports() { return taskReports.values(); } 
	public Collection< TravelReport> getTravelReports() { return travelReports.values(); } 
	
	public int getNoValidated() { return noValidated; }
	public int getApproved() 	{ return approved; }
	public int getRejected() 	{ return rejected; }
	public int getProcessed() 	{ return processed; }
	
	// Business methods
	public boolean addReport( AbstractReport report ) {

		if ( report instanceof TaskReport ) {
			
			return addReport(( TaskReport )report );
			
		} else if ( report instanceof TravelReport ) {
		
			return addReport(( TravelReport )report );
			
		}
		
		return false;
	}

	public boolean updateReport( AbstractReport report ) {

		boolean bRes = false;
		
		if ( report instanceof TaskReport ) {
			
			bRes =  updateReport(( TaskReport )report );
			
		} else if ( report instanceof TravelReport ) {
		
			bRes =  updateReport(( TravelReport )report );
			
		}
/*		
		if ( bRes ) {
		
			updateCounters();
		}
*/		
		return bRes;
	}

	
	
	
	
	// Sort by project id +  report type
	public void sort() {
/*	
		
		Collections.sort( this, new Comparator<AbstractReport>() {
			
			@Override
			public int compare( AbstractReport report_1, AbstractReport report_2 ) {
				String arg1 = null;
				String arg2 = null;
				
				try {
					if ( report_1 instanceof TaskReport ) {
						arg1 = (( TaskReport )report_1 ).getProjectTask().getProject().getCode();
					} else if ( report_1 instanceof TravelReport ) {
						arg1 = (( TravelReport )report_1 ).getProject().getCode();
					}
				} catch ( Exception e ) {
					logger.error( "Something wrong in Report 1:" + ( report_1 != null && report_1.getUniqueReportId() != null ? report_1.getUniqueReportId() : "???" ));
				}
				try {
					if ( report_2 instanceof TaskReport ) {
						arg2 = (( TaskReport )report_2 ).getProjectTask().getProject().getCode();
					} else if ( report_1 instanceof TravelReport ) {
						arg2 = (( TravelReport )report_2 ).getProject().getCode();
					}
				} catch ( Exception e ) {
					logger.error( "Something wrong in Report 2:" + ( report_2 != null && report_2.getUniqueReportId() != null ? report_2.getUniqueReportId() : "???" ));
				}


				if ( arg1 == null ) return -1;
				if ( arg2 == null ) return 1;
				
		        int res = arg1.compareToIgnoreCase( arg2 );
		        
		        if ( res == 0 ) {
		        	// Compare by report Type
					if ( report_1 instanceof TaskReport ) {
						res = -1;
					}
		        }
		        
				return res;
			}

		});
*/
	}

	public void clear() {
		this.date = null;
		this.project = null;
		this.hours = 0;
		this.homeDistance = 0;
		this.workDistance = 0;
		
		this.taskReports.clear();
		this.travelReports.clear();
	}

	private boolean addReport( TaskReport report ) {
		boolean bRes = false;
		
			
		if ( !taskReports.containsKey( report.getUniqueReportId())) {

			taskReports.put( report.getUniqueReportId(), report );
			bRes = true;
		}
		
		logger.debug ( "Task Report was added to ProjectHolder: " + ( bRes ? "success" : "failed!" ));
		
		return bRes;
	}

	private boolean updateReport( TaskReport report ) {
		boolean bRes = false;
		
			
		TaskReport oldReport = taskReports.remove( report.getUniqueReportId());
		
		if ( oldReport != null ) {
			logger.debug ( "Task Report was deleted (for update purposes) from ProjectHolder" );

			bRes = addReport( report );
			
			logger.debug ( "Task Report was updated to ProjectHolder: " + ( bRes ? "success" : "failed!" ));
		}

		
		return bRes;
	}

	private boolean addReport( TravelReport report ) {
		boolean bRes = false;
		
		if ( !travelReports.containsKey( report.getUniqueReportId())) {

			travelReports.put( report.getUniqueReportId(), report );
			bRes = true;
		}
			
		logger.debug ( "Travel Report was added to ProjectHolder: " + ( bRes ? "success" : "failed!" ));
		
		return bRes;
	}

	private boolean updateReport( TravelReport report ) {
		boolean bRes = false;
		
			
		TravelReport oldReport = travelReports.remove( report.getUniqueReportId());
		
		if ( oldReport != null ) {
			logger.debug ( "Travel Report was deleted (for update purposes) from ProjectHolder" );

			bRes = addReport( report );
			
			logger.debug ( "Travel Report was updated to ProjectHolder: " + ( bRes ? "success" : "failed!" ));
		}

		
		return bRes;
	}

	public void updateCounters() {

		this.noValidated = 0;
		this.approved = 0;
		this.rejected = 0;
		this.processed = 0;
		
		this.hours = 0;
		this.homeDistance = 0;
		this.workDistance = 0;
		
		for ( TaskReport report : taskReports.values()) {
			
			addAprovalCounter( report );
			addHoursCounter( report );
		}

		for ( TravelReport report : travelReports.values()) {
			
			addAprovalCounter( report );			
			addDistanceCounter( report );
		}
	
	}
	
	private void addAprovalCounter( AbstractReport report ) { 

		if ( report != null ) {
			switch ( report.getApprovalFlagType()) {
				case TO_CHECK:
					this.noValidated++;
					break;
				case APPROVED:
					this.approved++;
					break;
				case REJECTED:
					this.rejected++;
					break;
				case PROCESSED:
					this.processed++;
					break;
			}
		}
	}
	
	private void addHoursCounter( TaskReport report ) {

		if ( report != null ) {
			this.hours += report.getHours();
		}
		
	}

	private void addDistanceCounter( TravelReport report ) {
		
		if ( report != null ) {
			if ( report.getTravelType() == TravelType.HOME ) {

				this.homeDistance += report.getDistance();
			
			} else if ( report.getTravelType() == TravelType.WORK ) {
			
				this.workDistance += report.getDistance();
				
			}
		}

	}
	

		
}

