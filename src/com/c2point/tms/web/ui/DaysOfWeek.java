package com.c2point.tms.web.ui;

import java.util.Calendar;

public enum DaysOfWeek {
	MON, TUE, WED, THU, FRI, SAT, SUN;
	
	public static final DaysOfWeek convertFromCalendarDOW( int calValue ) {
		
		switch ( calValue ) {
			case Calendar.MONDAY:
				return MON;
			case Calendar.TUESDAY:
				return TUE;
			case Calendar.WEDNESDAY:
				return WED;
			case Calendar.THURSDAY:
				return THU;
			case Calendar.FRIDAY:
				return FRI;
			case Calendar.SATURDAY:
				return SAT;
			case Calendar.SUNDAY:
				return SUN;
		}
		throw new IllegalArgumentException ( "Incorrect value for Calendar.DAY_OF_WEEK" );
	 
	}
	public final int convertToCalendarDOW() {
		
		switch ( this ) {
			case MON:
				return Calendar.MONDAY;
			case TUE:
				return Calendar.TUESDAY;
			case WED:
				return Calendar.WEDNESDAY;
			case THU:
				return Calendar.THURSDAY;
			case FRI:
				return Calendar.FRIDAY;
			case SAT:
				return Calendar.SATURDAY;
			case SUN:
				return Calendar.SUNDAY;
		}
		return -1;
	 
	}
}
