package com.c2point.tms.web.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Lang {

	public static final Locale LOCALE_NONE = new Locale("-");


	public static final Locale LOCALE_FI = new Locale("fi", "FI");

	public static final Locale LOCALE_ET = new Locale("et", "FI");

	public static final Locale LOCALE_EN = new Locale("en", "FI");

	public static final Locale LOCALE_SV = new Locale("sv", "FI");

	public static final Locale LOCALE_RU = new Locale("ru", "FI");

	public static final Locale DEFAULT_LOCALE = LOCALE_FI;

	
	public static List<Locale> getAvailableLocales() {
		ArrayList<Locale> locales = new ArrayList<Locale>();
		locales.add( LOCALE_FI );
		locales.add( LOCALE_EN );
//		locales.add( LOCALE_SV );
		locales.add( LOCALE_ET );
		locales.add( LOCALE_RU );

		return locales;
	}
	
	/**
	 * Load resource bundle for given Locale.
	 * 
	 * @param l
	 *            Locale of the resource bundle
	 * @return ResourceBundle or null if not found.
	 */
	public static ResourceBundle loadBundle(String bundleName, Locale l) {
		ResourceBundle b = null;
		try {
			b = ResourceBundle.getBundle(bundleName, l);
		} catch ( MissingResourceException e ) {
			b = null;
		}
		if (b == null) {
			System.err.println("Translations " + bundleName + " not found for locale '" + l + "'");
		} else {
			// System.err.println("Loaded translations " + bundleName
			// + " for locale '" + l + "'");
		}
		return b;
	}

}
