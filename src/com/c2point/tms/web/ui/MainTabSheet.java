package com.c2point.tms.web.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.ui.TabSheet;

@SuppressWarnings("serial")
public class MainTabSheet extends TabSheet implements TabSheet.SelectedTabChangeListener {
	private static Logger logger = LogManager.getLogger( MainTabSheet.class.getName());

	public MainTabSheet() {
		super();
//		addStyleName( Runo.TABSHEET_SMALL );
		
		this.setSizeFull();
		
		this.addListener( this );	
	}

	@Override
	public void selectedTabChange( SelectedTabChangeEvent event ) {

		// Determine what Tab has been selected
		final TabSheet source = (TabSheet) event.getSource();
		if ( source == this ) {
			Tab tab = source.getTab( source.getSelectedTab());
			logger.debug( "Tab has been selected: '" + tab.getCaption() + "'" );
			if ( source.getSelectedTab() instanceof AbstractMainView ) {
				AbstractMainView av = ( AbstractMainView )source.getSelectedTab();
				logger.debug( "  View from Tab: " + av.getClass().getName());
				av.initData();
			} else if ( source.getSelectedTab() instanceof MainTabSheet ) {
				MainTabSheet ts = ( MainTabSheet )source.getSelectedTab();
				if ( ts.getSelectedTab() instanceof AbstractMainView ) {
					AbstractMainView av = ( AbstractMainView )ts.getSelectedTab();
					logger.debug( "  View from Subtab: " + av.getClass().getName());
					av.initData();
				} else {
					logger.debug( "  Unknown View from subtab: " + ts.getSelectedTab().getClass().getName());
				}
			} else {
				logger.debug( "  Unknown View from Tab: " + (( MainTabSheet )source.getSelectedTab()).getSelectedTab().getClass().getName());
			}
		}
	}
	
}
