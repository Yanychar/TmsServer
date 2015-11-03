package com.c2point.tms.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DateUtil {
	private static Logger logger = LogManager.getLogger( DateUtil.class.getName());

//	private static SimpleDateFormat dateFormatter = new SimpleDateFormat( "dd.MM.yyyy" );
//    private static SimpleDateFormat dateNoDelimFormatter = new SimpleDateFormat( "ddMMyyyy" );
//    private static SimpleDateFormat timeFormatter = new SimpleDateFormat( "HH:mm" );
//    private static SimpleDateFormat timeNoDelimFormatter = new SimpleDateFormat( "HHmm" );
//    private static SimpleDateFormat dateTimeFormatter = new SimpleDateFormat( "ddMMyyyyHHmm" );
    
    // Time shift from CET
//    private static int hourShift = 2;
    private static TimeZone helsinkiTZ = TimeZone.getTimeZone( "Europe/Helsinki" );
    private static TimeZone local = TimeZone.getDefault();

    public static Locale currentLocale = new Locale( "fi" );
    
    
	public static Date getDate() {
//		long  timeVal = new Date().getTime() + 1000 *60 *60 * hourShift;
    	long timeVal = System.currentTimeMillis();
    	long shift = helsinkiTZ.getOffset( timeVal ) - local.getOffset( timeVal );
		
    	if ( logger.isDebugEnabled()) {
    		logger.debug( "Server time: " + new Date());
    		logger.debug( "Hki time: " + new Date( timeVal + shift ));
    	}
		return new Date( timeVal + shift );
	}

/* Access to Date Formatters */
	public static DateFormat getDateFormatYear4digit() {
		return new SimpleDateFormat( "dd.MM.yyyy" );
	}
	

/* Time Conversion functions  */
	public static String timeToString() {
		return dateToString( getDate());
	}
	public static String timeToString( Date date ) {
		return ( date != null ? new SimpleDateFormat( "HH:mm" ).format( date ) : "" );
	}
	public static String timeNoDelimToString() {
		return new SimpleDateFormat( "HHmm" ).format( getDate());
	}
	public static String timeNoDelimToString( Date date ) {
		return ( date != null ? new SimpleDateFormat( "HHmm" ).format( date ) : null );
	}
	public static Date stringNoDelimToTime( String timeString ) throws ParseException {
		Date time;
		
		if ( timeString == null )
			return null;
		
	    SimpleDateFormat timeNoDelimFormatter = new SimpleDateFormat( "HHmm" );

	    if ( timeNoDelimFormatter.isLenient()) timeNoDelimFormatter.setLenient( false );
		time = timeNoDelimFormatter.parse( timeString );
		
		return time;
	}

	public static Date stringToTime( String timeString ) throws ParseException {
		return stringToTime( null, timeString );
	}
	public static Date stringToTime( Date date, String timeString ) throws ParseException {
		
		Date time;

/*		
		if ( timeString == null )
			return null;
*/		
		SimpleDateFormat timeFormatter = new SimpleDateFormat( "HH:mm" );
		if ( timeFormatter.isLenient()) timeFormatter.setLenient( false );
		
		try {
			time = timeFormatter.parse( timeString );
		} catch ( Exception e ) {
			if ( logger.isDebugEnabled()) logger.debug( "" );
			time = timeFormatter.parse( "00:00" );
		}
		
		if ( date != null ) {
			Calendar newCal = Calendar.getInstance();
			newCal.setTime( date );
			
			Calendar timeCal = Calendar.getInstance();
			timeCal.setTime( time );
			
			newCal.set( Calendar.HOUR_OF_DAY, timeCal.get( Calendar.HOUR_OF_DAY ));
			newCal.set( Calendar.MINUTE, timeCal.get( Calendar.MINUTE ));
			
			return newCal.getTime(); 
		}
		
		return time;
	}
	
	
/* Date conversion functions  */	
	public static String dateToString() {
		return dateToString( getDate());
	}
	public static String dateToString( Date date ) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat( "dd.MM.yyyy" );
		return ( date != null ? dateFormatter.format( date ) : null );
	}
	public static String dateNoDelimToString() {
		return new SimpleDateFormat( "ddMMyyyy" ).format( getDate());
	}
	public static String dateNoDelimToString( Date date ) {
		return ( date != null ? new SimpleDateFormat( "ddMMyyyy" ).format( date ) : null );
	}
	public static Date stringNoDelimToDate( String dateString ) throws ParseException {
		Date date;
		
		if ( dateString == null )
			return null;
		
		SimpleDateFormat dateNoDelimFormatter = new SimpleDateFormat( "ddMMyyyy" );
		
		if ( dateNoDelimFormatter.isLenient()) dateNoDelimFormatter.setLenient( false );
		date = dateNoDelimFormatter.parse( dateString );
		
		return date;
	}

	
/* Date And Time conversion functions  */	
	public static String dateAndTimeToString() {
		return new SimpleDateFormat( "ddMMyyyyHHmm" ).format( getDate());
	}
	public static String dateAndTimeToString( Date date ) {
		return ( date != null ? new SimpleDateFormat( "ddMMyyyyHHmm" ).format( date ) : null );
	}
	public static Date stringToDateAndTime( String dateTimeString ) throws ParseException {
		Date date;
		
		if ( dateTimeString == null )
			return null;
		
		SimpleDateFormat dateTimeFormatter = new SimpleDateFormat( "ddMMyyyyHHmm" );
		
		if ( dateTimeFormatter.isLenient()) dateTimeFormatter.setLenient( false );
		date = dateTimeFormatter.parse( dateTimeString );
		
		return date;
	}

/* Other Date related routines */	
	// Returns difference in days
	public static int differenceInDays( Date date1, Date date2 ) {
		
		int result = 0;
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		
		cal1.setTime( date1 );
		cal2.setTime( date2 );
		
		result = ( cal2.get( Calendar.YEAR ) - cal1.get( Calendar.YEAR )) * 366 + cal2.get( Calendar.DAY_OF_YEAR )- cal1.get( Calendar.DAY_OF_YEAR );
		
		return result;
	}

	// Compare Date only (without time)
	public static int compareDateOnly( Date date1, Date date2 ) {
		int result = 0;
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		
		cal1.setTime( date1 );
		cal2.setTime( date2 );
		
		if ( cal1.get( Calendar.YEAR ) == cal2.get( Calendar.YEAR )) {
			if ( cal1.get( Calendar.DAY_OF_YEAR ) != cal2.get( Calendar.DAY_OF_YEAR )) {
				result = ( cal1.get( Calendar.DAY_OF_YEAR ) < cal2.get( Calendar.DAY_OF_YEAR )) ? -1 : 1;
			}
		} else {
			result = ( cal1.get( Calendar.YEAR ) < cal2.get( Calendar.YEAR )) ? -1 : 1;
		}
	
		return result;
	}

	// Get First day of week that is N weeks ahead/back from current week
	public static Calendar getFirstDayOfWeek( Calendar current, int weekShift ) {
		Calendar cal = Calendar.getInstance( currentLocale );
		cal.setTimeInMillis( current.getTimeInMillis());
		
		cal.set( Calendar.HOUR_OF_DAY, 0);
		cal.set( Calendar.MINUTE, 0);
		cal.set( Calendar.SECOND, 0);
		cal.set( Calendar.MILLISECOND, 0);
		
		cal.add( Calendar.DAY_OF_YEAR, weekShift * 7 ); 
		cal.set( Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek()); 
		
		return cal;
	}
	public static Calendar getFirstDayOfWeek( Calendar current ) {
		return getFirstDayOfWeek( current, 0 );
	}
	public static Calendar getFirstDayOfWeek( Date date, int weekShift ) {
		Calendar cal = Calendar.getInstance( currentLocale );
		cal.setTime( date );
		
		return getFirstDayOfWeek( cal, weekShift );
	}
	public static Calendar getFirstDayOfWeek( Date date ) {
		return getFirstDayOfWeek( date, 0 );
	}
	public static Calendar getFirstDayOfWeek() {
		return getFirstDayOfWeek( Calendar.getInstance(), 0 );
	}

	public static Date addDays( Date date, int days ) {

		Calendar cal = Calendar.getInstance( currentLocale );
		
		cal.setTimeInMillis( date.getTime());
		
		cal.add( Calendar.DAY_OF_MONTH, days );
		
		return cal.getTime();
	}


	public static Date getStartOfDay() {
	    return getStartOfDay( getDate());
	}

	public static Date getStartOfDay(Date date) {
	    return DateUtils.truncate(date, Calendar.DATE);
	}

	public static Date getEndOfDay() {
	    return getEndOfDay( getDate());
	}

	public static Date getEndOfDay(Date date) {
	    return DateUtils.addMilliseconds(DateUtils.ceiling(date, Calendar.DATE), -1);
	}

	private static long hoursPart( long mins ) {
		return mins / 60;
	}

	private static long minsPart( long mins ) {
		return mins % 60;
	}

	public static String getHourMinsString( long mins, String hoursStr, String minsStr ) {
		
//		return hoursPart( mins ) + model.getApp().getResourceStr( "approve.edit.hours.short" ) + " " 
//			 + minsPart( mins ) + model.getApp().getResourceStr( "approve.edit.minutes.short" );
		
		return hoursPart( mins ) + hoursStr + " " + minsPart( mins ) + minsStr;
	}


}
