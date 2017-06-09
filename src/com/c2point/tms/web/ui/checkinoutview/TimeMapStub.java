package com.c2point.tms.web.ui.checkinoutview;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.CheckInOutRecord;
import com.c2point.tms.entity.GeoCoordinates;
import com.c2point.tms.util.DateUtil;
import com.c2point.tms.util.location.GeoDistanceValidator;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;

public class TimeMapStub {

	private static Logger 		logger = LogManager.getLogger( TimeMapStub.class.getName());

	private GeoDistanceValidator.ValidationResult	geoValidationResultIN;
	private GeoDistanceValidator.ValidationResult	geoValidationResultOUT;

	private double			warningDistance = -1;

	public enum ShowType { NONE, IN, OUT, ALL };

	private CheckInOutRecord record;

	public TimeMapStub( CheckInOutRecord record ) {

		super();

		this.record = record;
		
		validateWarningFlag();
		
	}

	public Label getTimeComponent( ShowType checkInFlag ) {
		
		Label timeLabel = new Label( "", ContentMode.HTML );
		
		// Get time and format it
		String	timeString = getTimeString( checkInFlag );

		if ( getWarning( checkInFlag ) == GeoDistanceValidator.ValidationResult.TOO_FAR) {
			timeLabel.setValue( "<b><font color=\"red\">" + timeString + "</font></b>" );
		} else {
			timeLabel.setValue( timeString );
		}
		
		
		return timeLabel;
	}
	
	public TimeMapComponent getMapComponent( ShowType checkInFlag, boolean showTime ) {

		TimeMapComponent tmComp = new TimeMapComponent( this.record );
		
		if ( showTime ) {

			Label timeL = getTimeComponent( checkInFlag );
			
			tmComp.addTime( timeL );
		}

		if ( showIcon( checkInFlag )) {
			tmComp.addMap();
		}

		
		
		
		return tmComp;
	}

	private void validateWarningFlag() {

		GeoCoordinates baseC = record.getProject().getGeo();
		GeoCoordinates inC = record.getCheckInGeo();
		GeoCoordinates outC = record.getCheckOutGeo();

		GeoDistanceValidator geoValidator = new GeoDistanceValidator( warningDistance );
			
		this.geoValidationResultIN = geoValidator.validate( inC, baseC );

		this.geoValidationResultOUT = geoValidator.validate( outC, baseC );
		

	}

	
	private String getTimeString( ShowType checkInFlag ) {

		String	timeString = null;
		String	shiftString = null;

		if ( checkInFlag == ShowType.IN ) {

			try {
				timeString = DateUtil.timeToString( this.record.getDateCheckedIn());
			} catch ( Exception e ) {
				timeString = null;
			}

		} else if ( checkInFlag == ShowType.OUT ) {

			try {
				timeString = DateUtil.timeToString( this.record.getDateCheckedOut());
			} catch ( Exception e ) {
				timeString = null;
			}

			try {
				int tmp = DateUtil.differenceInDays( record.getDateCheckedIn(), record.getDateCheckedOut());
				if ( tmp > 0 ) {
					shiftString = Integer.toString( tmp );
				}

			} catch ( Exception e ) {
				shiftString = null;
			}
		}

		return ( timeString != null ? timeString : "" ) + ( shiftString != null ? "(" + shiftString + ")" : "" );
	}

	private boolean showIcon( ShowType checkInFlag ) {

//		GeoCoordinates baseC = record.getProject().getGeo();
		GeoCoordinates inOutC;

		boolean bShow = false;

//		bShow = ( baseC != null && baseC.isValid());

		if ( checkInFlag == ShowType.IN || checkInFlag == ShowType.ALL ) {

			inOutC = record.getCheckInGeo();

			bShow =    record.getDateCheckedIn() != null
					&& ( bShow || inOutC != null && inOutC.isValid());

		} else if ( checkInFlag == ShowType.OUT || checkInFlag == ShowType.ALL ) {

			inOutC = record.getCheckOutGeo();

			bShow =    record.getDateCheckedOut() != null
					&& ( bShow || inOutC != null && inOutC.isValid());

		}

		return bShow;
		
	}


	public void setupWarningDistance( double warningDistance ) {

		this.warningDistance = warningDistance; 
		
		validateWarningFlag();
		
//		updateTime();
//		updateMap();
	}

	private GeoDistanceValidator.ValidationResult getWarning( ShowType checkInFlag ) {

		if ( checkInFlag == ShowType.IN  ) {
			return geoValidationResultIN;
		} else if ( checkInFlag == ShowType.OUT  ) {
			return geoValidationResultOUT;
		}
		
		// In case of wrong flag
		logger.error( "Cannot be other flag than IN or OUT" );
		
		return GeoDistanceValidator.ValidationResult.NO_RESULT;  
		
		
	}

}
