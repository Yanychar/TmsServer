package com.c2point.tms.web.resources.tools;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LangPacks {

	private static Logger logger = LogManager.getLogger( LangPacks.class.getName());

	public final static Locale LOCALE_EN = new Locale("en", "FI");
	public final static Locale LOCALE_FI = new Locale("fi", "FI");
	public final static Locale LOCALE_ES = new Locale("et", "FI");
	public final static Locale LOCALE_SV = new Locale("sv", "FI");
	public final static Locale LOCALE_RU = new Locale("ru", "FI");

	public final Locale DEFAULT_LOCALE = LOCALE_EN;
	
	private static String prefix_empty = "empty_"; 
	private static String prefix_comment = "comment_"; 
	
	private Map<String, LocalizedProperty> map;  
	
	
	public LangPacks() {
		
		map = new LinkedHashMap< String, LocalizedProperty >();
	}
	
	public boolean add() {
		map.put( createUniqueKey( prefix_empty ), new LocalizedProperty());
		return true;
	}
	public boolean add( String comment ) {
		map.put( createUniqueKey( prefix_comment ), new LocalizedProperty( comment ));
		return true;
	}
	
	public boolean add( String key, Locale locale, String value ) {
		
		LocalizedProperty prop = map.get( key );
		
		if ( prop == null ) {
			prop = new LocalizedProperty();
			map.put( key, prop );
		}
		
		boolean bRes = prop.set( locale, value );
			
		if ( !bRes ) {
			logger.error( "          ... for property '" + key + "'" );
			
			return false;
		}

		return true;
	}
	
	public Collection<LocalizedProperty> values() { return map.values(); }
	public Set<String> keys() { return map.keySet(); }
	
	public LocalizedProperty get( String key ) {
		
		return map.get( key );
	}
	
	private int uniqueKey = 1;
	private String createUniqueKey( String prefix ) {
		String key;
		
		do {
			key = prefix + Integer.toString( uniqueKey );
			uniqueKey++;
		} while( map.containsKey( key ));
		
		return key;
	}

	public int size() {
		return map.size();
	}

	public void print() {
		
		Iterator<String> iter = keys().iterator();
		String key;
		int count = 0, err = 0;
		while( iter.hasNext()) {
			key = iter.next();
			
//			if ( key )
			
			LocalizedProperty lp =  get( key );

			if ( lp.getType() == LocalizedProperty.RecType.EMPTY ) {
				logger.debug( "" );
				count++;
			} else if ( lp.getType() == LocalizedProperty.RecType.COMMENT ) {
				logger.debug( lp.getComment());
				count++;
			} else if ( lp.getType() == LocalizedProperty.RecType.PROPERTY ) {
//				logger.debug( key + "=" + lp.get( LOCALE_EN ));
				logger.debug( "'" + key + "' :" );
				logger.debug( "    =" + lp.get( LangPacks.LOCALE_EN ));
				logger.debug( "      =" + lp.get( LangPacks.LOCALE_FI ));
				logger.debug( "        =" + lp.get( LangPacks.LOCALE_ES ));
				logger.debug( "          =" + lp.get( LangPacks.LOCALE_RU ));
				logger.debug( "            =" + lp.get( LangPacks.LOCALE_SV ));
				count++;
			} else {
				err++;
			}
		}
		
		logger.debug( "*** Handled: " + count + " recs. Error: " + err );
	
	}

}
