package com.c2point.tms.tools.exprt;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.AbstractReport;
import com.c2point.tms.entity.ApprovalFlagType;
import com.c2point.tms.entity.TaskReport;
import com.c2point.tms.entity.TravelReport;
import com.c2point.tms.entity.TravelType;
import com.c2point.tms.util.DateUtil;
import com.c2point.tms.util.StringUtils;

public class SS_RecordsWriter extends UHR_RecordsWriter {
	private static Logger logger = LogManager.getLogger( SS_RecordsWriter.class.getName());

	public SS_RecordsWriter() {
		super();
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
			
				// 3st field:  	Erittely.txt - employee number 
				String userCode = StringUtils.padLeftZero( report.getUser().getCode(), 5 );

				// 4st field:  	Erittely.txt - task code 
				String taskCode = StringUtils.padLeftZero( report.getProjectTask().getTask().getCode(), 6 );
			
				// 5st field:  	Erittely.txt - hours 
				String hoursStr = StringUtils.padLeftZero( report.getHours() * 100, 6 );
			
				// 6st field:  	Erittely.txt - pcs
				String pcs = StringUtils.padLeftZero( 0, 10 );
			
				// 7st field:  	Erittely.txt - cost center = " 305", not yet used  
				String costCenter = StringUtils.padLeftChar( COST_CENTER_CODE, LENGTH_COST_CENTER, ' ' );

				// 8st field:  	Erittely.txt - wage type code  
				String wageCode;
				// Report Normal time
				wageCode = StringUtils.padLeftChar( NORMAL_HOURS_WAGE_CODE, LENGTH_WAGE_CODE, ' ' );
				
				writeTaskRecord( dateStr, countGroup, userCode, taskCode, hoursStr, pcs, costCenter, wageCode );

			} catch (IOException e) {
				logger.error( "Cannot write one record data to the file. Line #" + ( this.getTotal() + 1 ));
				this.recordRejected();
				return false;
			}
		} else if ( abstractReport != null && abstractReport instanceof TravelReport ) {
			try {
				TravelReport report = ( TravelReport )abstractReport;
				if ( report.getTravelType() == TravelType.HOME && report.getDistance() <= 5 ) {
					report.setApprovalFlagType( ApprovalFlagType.REJECTED );
					this.recordProcessed();
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
				String pcs = StringUtils.padLeftZero( report.getDistance(), 10 );
			
				// 7st field:  	Erittely.txt - cost center = blanks, not yet used  
				String costCenter = StringUtils.padLeftChar( 305, 4, ' ' );
	
				// 8st field:  	Erittely.txt - wage type code  
				String wageCode = StringUtils.padLeftChar( getWageTypeCodeForTravelReport( report ), 4, ' ' );
				
				writeTaskRecord( dateStr, countGroup, userCode, taskCode, hoursStr, pcs, costCenter, wageCode );

			} catch (IOException e) {
				logger.error( "Cannot write one record data to the file. Line #" + ( this.getTotal() + 1 ));
				this.recordRejected();
				return false;
			}
		} else {
			logger.error( "Wrong report type!!!" );
			this.recordRejected();
			return false;
		}
		
		this.recordProcessed();
		
		return true;
	}

	private String getTaskNumberForTravelReport( TravelReport report ) {
		
		return "0";
	}
	
	private String getHoursForTravelReport( TravelReport report ) {
		
		return "0";
	}
	
	private String getWageTypeCodeForTravelReport( TravelReport report ) {
		String wageCode = "123";
		
		return wageCode;
	}
	
}
