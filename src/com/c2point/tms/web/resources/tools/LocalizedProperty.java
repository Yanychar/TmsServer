package com.c2point.tms.web.resources.tools;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LocalizedProperty {
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( LocalizedProperty.class.getName());

	public enum RecType { COMMENT, EMPTY, PROPERTY, UNKNOWN }
	
	private RecType type;
	private String comment;
	
	private Map<Locale, String> map;  
	
	public LocalizedProperty() {
		setType( RecType.EMPTY );
	}
	
	public LocalizedProperty( String comment ) {
		setType( RecType.COMMENT );
		this.comment = comment;
	}
	
	
	
	public RecType getType() { return this.type; }
	public String getComment() { return this.comment; }
	private void setType( RecType type ) { this.type = type; }
	
	public boolean set( Locale locale, String value ) {
		setType( RecType.PROPERTY );
		
		if ( map == null ) {
			map = new HashMap<Locale, String>();
			
		}
		
		if ( map.containsKey( locale )) {
			String oldValue = map.get( locale );
			if ( oldValue == null && value == null 
				|| 
				oldValue != null && oldValue.compareTo( value ) == 0 ) {
				
			} else {
//				logger.debug( "Value shall be updated. Locale " + locale );
//				logger.debug( "  Old: '" + oldValue + "'. New value: '" + value + "'" );
//				map.put( locale, value );
			}
		} else {
			map.put( locale, value );
		}
		
		
		return true;
	}
	
	public String get( Locale locale ) {
		return map.get( locale );
	}

	

	// Return csv string ordered by according to the locale  with specified delimiter
	public String exportAsCsv( String key, Locale [] order, String delimiter ) {
		
		String res, value;
		
		if ( getType() == LocalizedProperty.RecType.COMMENT ) {
			res = getComment();
		} else if ( getType() == LocalizedProperty.RecType.PROPERTY ) {
			res = ( key != null ) ? key : "";

			value = null;
			for ( Locale locale : order ) {
				if ( locale != null ) {
					value = get( locale );
				}
				if ( value == null || value.length() == 0 ) {
					value = "*** UNDEFINED!!! ***";
				}
				res = res.concat( delimiter + value ); 
			}

		} else {
			res = "";
		}
		
		return res;
		
	}
}
