package com.c2point.tms.tools.exprt;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.AbstractReport;
import com.c2point.tms.entity.ApprovalFlagType;
import com.c2point.tms.entity.TaskReport;
import com.c2point.tms.entity.TravelReport;
import com.c2point.tms.entity.TravelType;
import com.c2point.tms.util.DateUtil;
import com.c2point.tms.util.StringUtils;

public class UHR_RecordsWriter extends ExportValidator {
	private static Logger logger = LogManager.getLogger( UHR_RecordsWriter.class.getName());

	protected float		NORMAL_HOURS 				= 8;
	protected String	NORMAL_HOURS_WAGE_CODE 		= "1";
	protected String	OVERTIME_HOURS_WAGE_CODE 	= "171";
	protected int		LENGTH_WAGE_CODE 			= 4;
	protected String	COST_CENTER_CODE 			= "305";
	protected int		LENGTH_COST_CENTER			= 4;

	protected String 	EOL = "\r\n";
	
	protected HoursStorage hoursMap = null;

	public UHR_RecordsWriter() {
		super();
		this.hoursMap = new HoursStorage();
//		this.EOL = System.getProperty("line.separator");
	}
	
	@Override
	public boolean preProcessRecord( AbstractReport report ) {
		
		ApprovalFlagType flag = report.getApprovalFlagType();
		
		if ( flag != ApprovalFlagType.APPROVED && flag != ApprovalFlagType.PROCESSED ) {
			recordFilteredOut();
			return false;
		}
		
		return true;
	}
	
	@Override
	public boolean processRecord( AbstractReport abstractReport ) {
		
		if ( abstractReport != null && abstractReport instanceof TaskReport ) {
			try {
				TaskReport report = ( TaskReport )abstractReport;
				
				// 1st field:  	Erittely.txt - date
				String dateStr = DateUtil.dateToString( report.getDate());

				// 2st field:  	Erittely.txt - employee counting group 
				String countGroup = StringUtils.padLeftZero( report.getUser().getCountgroup(), 2 );
				// Length of employee counting group code must be 2 exactly
				if ( countGroup == null || countGroup.length() != 2 ) {
					logger.error( "Cannot export Task Report with id: " + abstractReport.getUniqueReportId() + ". Counting Group length != 2" );
					error( "ERROR: Cannot export Task Report with id: " + abstractReport.getUniqueReportId() + ". Counting Group length != 2" );
					this.recordRejected();
					return false;
				}
			
				// 3st field:  	Erittely.txt - employee number 
				String userCode = StringUtils.padLeftZero( report.getUser().getCode(), 5 );

				// 4st field:  	Erittely.txt - task code 
				String taskCode = StringUtils.padLeftZero( report.getProjectTask().getTask().getCode(), 6 );
			
				// 5st field:  	Erittely.txt - hours
				// Length: 6 characters
				String hoursStr;   // Calculated later
			
				// 6st field:  	Erittely.txt - pcs
				// Length: 10 characters
				String pcs = StringUtils.padLeftZero( 0, 10 );
			
				// 7st field:  	Erittely.txt - cost center = " 305", not yet used  
				String costCenter = StringUtils.padLeftChar( COST_CENTER_CODE, LENGTH_COST_CENTER, ' ' );

				// 8st field:  	Erittely.txt - wage type code  
				String wageCode;

				float [] timeToReport = calculateOvertime( userCode, dateStr, report.getHours());
				if ( timeToReport[ 0 ] > 0 ) {
					// Report Normal time
					wageCode = StringUtils.padLeftChar( NORMAL_HOURS_WAGE_CODE, LENGTH_WAGE_CODE, ' ' );
					hoursStr = StringUtils.padLeftZero( timeToReport[ 0 ] * 100, 6 );
					
					writeTaskRecord( dateStr, countGroup, userCode, taskCode, hoursStr, pcs, costCenter, wageCode );
				} 
				if ( timeToReport[ 1 ] > 0 ) {
					// Overtime
					wageCode = StringUtils.padLeftChar( OVERTIME_HOURS_WAGE_CODE, LENGTH_WAGE_CODE, ' ' );
					hoursStr = StringUtils.padLeftZero( timeToReport[ 1 ] * 100, 6 );
					
					writeTaskRecord( dateStr, countGroup, userCode, taskCode, hoursStr, pcs, costCenter, wageCode );
				}
				

			} catch (IOException e) {
				logger.error( "Cannot write one record data to the file. Line #" + ( this.getTotal() + 1 ));
				error( "ERROR: Cannot export Task Report with id: " + abstractReport.getUniqueReportId());
				this.recordRejected();
				return false;
			}
		} else if ( abstractReport != null && abstractReport instanceof TravelReport ) {
			try {
				TravelReport report = ( TravelReport )abstractReport;
				if ( report.getTravelType() == TravelType.HOME && report.getDistance() <= 5 ) {
					report.setApprovalFlagType( ApprovalFlagType.REJECTED );
					return true;
				}
				
				// 1st field:  	Erittely.txt - date
				String dateStr = DateUtil.dateToString( report.getDate());

				// 2st field:  	Erittely.txt - employee counting group 
				String countGroup = StringUtils.padLeftZero( report.getUser().getCountgroup(), 2 );
				// Length of employee counting group code must be 2 exactly
				if ( countGroup == null || countGroup.length() != 2 ) {
					logger.error( "Cannot export Travel Report with id: " + abstractReport.getUniqueReportId() + ". Counting Group length != 2" );
					error( "ERROR: Cannot export Travel Report with id: " + abstractReport.getUniqueReportId() + ". Counting Group length != 2" );
					this.recordRejected();
					return false;
				}
			
				// 3st field:  	Erittely.txt - employee number 
				String userCode = StringUtils.padLeftZero( report.getUser().getCode(), 5 );

				// 4st field:  	Erittely.txt - task code 
				String taskCode = StringUtils.padLeftZero( getTaskNumberForTravelReport( report ), 6 );
			
				// 5st field:  	Erittely.txt - hours 
				String hoursStr = StringUtils.padLeftZero( getHoursForTravelReport( report ), 6 );
			
				// 6st field:  	Erittely.txt - pcs  
				String pcs;
				if ( report.getTravelType() == TravelType.HOME ) {
					pcs = StringUtils.padLeftZero( 1, 10 );
				} else if ( report.getTravelType() == TravelType.WORK ) {
					pcs = StringUtils.padLeftZero( report.getDistance(), 10 );
				} else {
					logger.error( "Travel Report has wrong type!. Line #" + ( this.getTotal() + 1 ));
					pcs = StringUtils.padLeftZero( 0, 10 );
				}
			
				// 7st field:  	Erittely.txt - cost center = blanks, not yet used  
				String costCenter = StringUtils.padLeftChar( 305, 4, ' ' );
	
				// 8st field:  	Erittely.txt - wage type code  
				String wageCode = StringUtils.padLeftChar( getWageTypeCodeForTravelReport( report ), 4, ' ' );

				writeTaskRecord( dateStr, countGroup, userCode, taskCode, hoursStr, pcs, costCenter, wageCode );
				
			} catch (IOException e) {
				logger.error( "Cannot write one record data to the file. Line #" + ( this.getTotal() + 1 ));
				this.recordRejected();
				error( "ERROR: Cannot export Travel Report with id: " + abstractReport.getUniqueReportId());
				return false;
			}
		} else {
			logger.error( "Wrong report type!!!" );
			this.recordRejected();
			error( "ERROR: Cannot export ??? Report with id: " + abstractReport.getUniqueReportId());
			return false;
		}
		
		this.recordProcessed();

		return true;
	}

	@Override
	public boolean postProcessRecord( AbstractReport report ) {
		if ( getEntityManager() == null ) {
			logger.error("Entity Manager is not set!!!" );
			return false;
		}
		
		try {
			report.setApprovalFlagType( ApprovalFlagType.PROCESSED );
			report = getEntityManager().merge( report );
		} catch ( Exception e ) {
		}
		
		return true;
	}
	
	private String getTaskNumberForTravelReport( TravelReport report ) {
		
		return "0";
	}
	
	private String getHoursForTravelReport( TravelReport report ) {
		
		return "0";
	}
	
	private String getWageTypeCodeForTravelReport( TravelReport report ) {
		String wageCode = "";
		if ( report.getTravelType() == TravelType.WORK ) {
			wageCode = "123";
		} else if ( report.getTravelType() == TravelType.HOME ) {
			wageCode = getCodeBasedOnKilometers( report.getDistance());
		}
		
		return wageCode;
	}
	
	private String getCodeBasedOnKilometers( int distance ) {
		String wageCode = "";

		if ( distance <= 5 ) {
			wageCode = "";
		} else if ( distance <= 10 ) {
			wageCode = "118";
		} else if ( distance <= 20 ) {
			wageCode = "321";
		} else if ( distance <= 30 ) {
			wageCode = "322";
		} else if ( distance <= 40 ) {
			wageCode = "323";
		} else if ( distance <= 50 ) {
			wageCode = "324";
		} else if ( distance <= 60 ) {
			wageCode = "325";
		} else if ( distance <= 70 ) {
			wageCode = "326";
		} else if ( distance <= 80 ) {
			wageCode = "327";
		} else if ( distance <= 90 ) {
			wageCode = "328";
		} else if ( distance <= 100 ) {
			wageCode = "329";
		} else if ( distance > 100 ) {
			wageCode = "330";
		}
		
		return wageCode;
	}


	protected void writeTaskRecord( 
			String dateStr, 
			String countGroup,
			String userCode,
			String taskCode,
			String hours,
			String pcs,
			String costCenter,
			String wageCode
									) throws IOException {

		Writer writer = this.getWriter( countGroup );
		if ( writer == null ) {
			throw new IOException( "Writer is null. Was not created properly!" );
		}
		
		
		// 1st field:  	Erittely.txt - date
		writer.write( dateStr );

		// 2st field:  	Erittely.txt - employee counting group 
		writer.write( countGroup );
	
		// 3st field:  	Erittely.txt - employee number 
		writer.write( userCode );

		// 4st field:  	Erittely.txt - task code 
		writer.write( taskCode );
	
		// 5st field:  	Erittely.txt - hours 
		writer.write( hours );
	
		// 6st field:  	Erittely.txt - pcs  
		writer.write( pcs );
	
		// 7st field:  	Erittely.txt - cost center = " 305", not yet used  
		writer.write( costCenter );

		// 8st field:  	Erittely.txt - wage type code  
		writer.write( wageCode );
		
		// EOL  
		writer.write( EOL );
	}
	
	protected void writeTravelRecord( 
			String dateStr, 
			String countGroup,
			String userCode,
			String taskCode,
			String hours,
			String pcs,
			String costCenter,
			String wageCode

			) throws IOException {

		Writer writer = this.getWriter( countGroup );
		if ( writer == null ) {
			throw new IOException( "Writer is null. Was not created properly!" );
		}
		
		
		// 1st field:  	Erittely.txt - date
		writer.write( dateStr );

		// 2st field:  	Erittely.txt - employee counting group 
		writer.write( countGroup );
	
		// 3st field:  	Erittely.txt - employee number 
		writer.write( userCode );

		// 4st field:  	Erittely.txt - task code 
		writer.write( taskCode );
	
		// 5st field:  	Erittely.txt - hours 
		writer.write( hours );
	
		// 6st field:  	Erittely.txt - pcs  
		writer.write( pcs );
	
		// 7st field:  	Erittely.txt - cost center = " 305", not yet used  
		writer.write( costCenter );

		// 8st field:  	Erittely.txt - wage type code  
		writer.write( wageCode );
		
		// EOL  
		writer.write( EOL );
	}
	
	
	
	
	private float [] calculateOvertime( String userCode, String dateStr, float hours ) {
		float overtime [] = new float[ 2 ];
		overtime[ 0 ] = 0;  // Normal hours
		overtime[ 1 ] = 0;  // Overtime

		float hoursCounted = this.hoursMap.getHours( userCode, dateStr );

		if ( hoursCounted >= NORMAL_HOURS ) {
			// Everything is overtime
			overtime[ 1 ] = hours; 
		} else {
			// Part goes to normal hours and the rest to overtime
			float stillPlace = ( NORMAL_HOURS > hoursCounted ? NORMAL_HOURS - hoursCounted : 0 );
			if ( stillPlace >= hours ) {
				overtime[ 0 ] = hours; 
			} else {
				overtime[ 0 ] = stillPlace; 
				overtime[ 1 ] = hours - stillPlace;
			}
		}
		
		this.hoursMap.setHours( userCode, dateStr, hoursCounted + hours );
		
		return overtime;
	}
	
	@SuppressWarnings("serial")
	protected class HoursStorage extends HashMap<String, Float> {
		
		HoursStorage() {
			super( 50 );
		}
		
		float getHours( String code, String dateStr ) {
			Float hours = get( code + dateStr );
			
			return ( hours != null ? hours.floatValue() : 0 );
		}
		
		void setHours( String code, String dateStr, float hours ) {
			put( code + dateStr, hours );
		}
		
		
	}

}
