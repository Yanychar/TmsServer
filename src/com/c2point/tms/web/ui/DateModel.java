package com.c2point.tms.web.ui;

import java.util.Calendar;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.util.DateUtil;

public class DateModel {
	private static Logger logger = LogManager.getLogger( DateModel.class.getName());

	private Calendar startOfWeek;
	private Calendar date;
	
	public DateModel( Date date ) {
		setDate( date );
	}
	
	public void setDate( Date date ) {
		if ( date == null ) {
			date = new Date();
		}
		this.startOfWeek = DateUtil.getFirstDayOfWeek( date );
		this.startOfWeek.set( Calendar.HOUR_OF_DAY, 0 );
		this.startOfWeek.set( Calendar.MINUTE, 0 );
		this.startOfWeek.set( Calendar.SECOND, 0 );
		this.startOfWeek.set( Calendar.MILLISECOND, 0 );
		
		this.date = Calendar.getInstance();
		this.date.setTime( date );
		
		if ( logger.isDebugEnabled()) logger.debug( this );
		
	}
	
	public Calendar getStartOfWeek() {
		return startOfWeek;
	}
	
	public Calendar getDate() {
		return date;
	}
	
	public Calendar getEndOfWeek() {
		
		Calendar endDay = ( Calendar ) startOfWeek.clone(); 
		endDay.add( Calendar.DAY_OF_WEEK, 7 );
		endDay.add( Calendar.MINUTE, -1 );
		return endDay; 
	}
	
	public void nextWeek() {
		this.startOfWeek = DateUtil.getFirstDayOfWeek( startOfWeek, 1 );
	}

	public void prevWeek() {
		this.startOfWeek = DateUtil.getFirstDayOfWeek( startOfWeek, -1 );
	}
	
	public int getWeek() {
		return this.startOfWeek.get( Calendar.WEEK_OF_YEAR );
	}
	
	public String toString() {
		return "Week " + getWeek() 
				+ ". Date: " 
				+ DateUtil.dateToString( getDate().getTime()) 
				+ ". From " 
				+ DateUtil.dateToString( getStartOfWeek().getTime()) 
				+ " till " 
				+ DateUtil.dateToString( getEndOfWeek().getTime());
	}
	public Date getDateOfWeekDay( DaysOfWeek dayOfWeek ) throws IndexOutOfBoundsException {
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis( this.startOfWeek.getTimeInMillis());
		cal.add( Calendar.DATE, dayOfWeek.ordinal() - DaysOfWeek.MON.ordinal());
		return cal.getTime(); 
	}
	
}
