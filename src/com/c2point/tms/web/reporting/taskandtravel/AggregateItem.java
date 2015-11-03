package com.c2point.tms.web.reporting.taskandtravel;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.ApprovalFlagType;

public class AggregateItem {

	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( AggregateItem.class.getName());

	protected  Map< ApprovalFlagType, Float >		hoursMap;
	protected  Map< ApprovalFlagType, Integer >		tyoajoMap;
	protected  Map< ApprovalFlagType, Integer >		tyomatkaMap;

	private AggregateItem	owner;

	public AggregateItem( AggregateItem	owner ) {
		this.owner = owner;

		this.hoursMap = new HashMap< ApprovalFlagType, Float >();
		this.tyoajoMap = new HashMap< ApprovalFlagType, Integer >();
		this.tyomatkaMap = new HashMap< ApprovalFlagType, Integer >();
		
	}

	public float getHours() {
		return getHours( ApprovalFlagType.APPROVED ) + getHours( ApprovalFlagType.PROCESSED );
	}

	public int getAjo() {
		return getAjo( ApprovalFlagType.APPROVED ) + getAjo( ApprovalFlagType.PROCESSED );
	}

	public int getMatka() {
		return getMatka( ApprovalFlagType.APPROVED ) + getMatka( ApprovalFlagType.PROCESSED );
	}
	
	@Override
	public String toString() {
		return "hours=" + getHours() + ", tyoajo=" + getAjo() + ", tyomatka=" + getMatka();
	}

	public float getHours( ApprovalFlagType type ) {
		Float value = hoursMap.get( type );
		
		return ( value != null ) ? value : 0;
	}

	public int getAjo( ApprovalFlagType type ) {
		Integer value = tyoajoMap.get( type );
		
		return ( value != null ) ? value : 0;
	}

	public int getMatka( ApprovalFlagType type ) {
		Integer value = tyomatkaMap.get( type );
		
		return ( value != null ) ? value : 0;
	}

	public void addHours( ApprovalFlagType type, float hours ) {
		Float value = hoursMap.get( type );
		if ( value == null ) {
			hoursMap.put( type, new Float( hours ));
		} else {
			hoursMap.put( type, value + hours );
		}

		if ( owner != null ) {
			owner.addHours( type, hours );
		}
	}
	
	public void addAjo( ApprovalFlagType type, int distance ) {
		Integer value = tyoajoMap.get( type );
		if ( value == null ) {
			tyoajoMap.put( type, new Integer( distance ));
		} else {
			tyoajoMap.put( type, value + distance );
		}

		if ( owner != null ) {
			owner.addAjo( type, distance );
		}
	}

	public void addMatka( ApprovalFlagType type, int distance ) {
		Integer value = tyomatkaMap.get( type );
		if ( value == null ) {
			tyomatkaMap.put( type, new Integer( distance ));
		} else {
			tyomatkaMap.put( type, value + distance );
		}

		if ( owner != null ) {
			owner.addMatka( type, distance );
		}
	}

	
}
