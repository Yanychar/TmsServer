package com.c2point.tms.web.ui.companytools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.access.SecurityContext;
import com.c2point.tms.entity.access.SupportedFunctionType;
import com.c2point.tms.web.application.TmsApplication;
import com.c2point.tms.web.ui.AbstractMainView;
import com.c2point.tms.web.ui.SideBarComponent;
import com.c2point.tms.web.ui.accessrights.AccessRightsModel;
import com.c2point.tms.web.ui.accessrights.AccessRightsView;
import com.c2point.tms.web.ui.tools.ExportToolView;
import com.c2point.tms.web.ui.tools.ImportToolView;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.Runo;

@SuppressWarnings("serial")
public class CompanyToolsView extends AbstractMainView implements ClickListener {

	private static Logger logger = LogManager.getLogger( CompanyToolsView.class.getName());

	private Organisation 			org;
	
	private HorizontalSplitPanel	hrSplitPanel;
	private SideBarComponent		sideBar;
	
	private int		accRightsId;
	private int		settingsId;
	private int		importId;
	private int		exportId;
	private int		activeButtonId;
	
	public CompanyToolsView( TmsApplication app ) {
		super( app );
		this.org = null;
		logger.debug( "CompanyToolsView has been created" );
	}
	
	@Override
	public void initUI() {

		this.setSizeFull();
		this.setSpacing( true );
		
		hrSplitPanel = new HorizontalSplitPanel();        
		hrSplitPanel.setSplitPosition( 20, Sizeable.UNITS_PERCENTAGE );        
		hrSplitPanel.setSizeFull();        
		hrSplitPanel.setLocked( true );
		hrSplitPanel.addStyleName( Runo.SPLITPANEL_SMALL );

		
		sideBar = new SideBarComponent();

		SecurityContext context = this.getTmsApplication().getSessionData().getContext();
		
		if ( context.isAccessible( SupportedFunctionType.ACCESS_RIGHTS_COMPANY )) {
			accRightsId = sideBar.addItemButton( this.getTmsApplication().getResourceStr( "tools.sidebar.item.access" ));
			logger.debug( "  AccessRights button has been created" );
		}
/*
		if ( context.isAccessible( SupportedFunctionType.SETTINGS_COMPANY )) {
			settingsId = sideBar.addItemButton( this.getTmsApplication().getResourceStr( "tools.sidebar.item.settings" ));
			logger.debug( "  Settings button have been created" );
		}
*/		
		if ( context.isAccessible( SupportedFunctionType.IMPORTEXPORT_COMPANY )) {
			importId = sideBar.addItemButton( this.getTmsApplication().getResourceStr( "tools.sidebar.item.import" ));
			exportId = sideBar.addItemButton( this.getTmsApplication().getResourceStr( "tools.sidebar.item.export" ));
			logger.debug( "  Import & Export buttons have been created" );
		}
		
		sideBar.addListener( this );
		
//		sideBar.clickButton( accRightsId );
		
//		hrSplitPanel.setFirstComponent( sideBar );
		
		addComponent( hrSplitPanel );
				
//		initDataAtStart();		
		
		
	}

	@Override
	protected void initDataAtStart() {
		logger.debug( "At Start" );

		hrSplitPanel.setFirstComponent( sideBar );

		sideBar.clickButton( accRightsId );
		
	}

	@Override
	protected void initDataReturn() {
		logger.debug( "At Return" );
		
		sideBar.clickButton( activeButtonId );
		
	}

	@Override
	public void buttonClick(ClickEvent event) {
		
		logger.debug( "Event data:  " + event.getButton().getData().getClass().getName());
		logger.debug( this );
		if ( event.getButton().getData() instanceof Integer ) {
			
			int buttonId = ( Integer )event.getButton().getData();
			
			logger.debug( "Button with id = " + buttonId + " has been pressed" );

			if ( buttonId == accRightsId ) {
				logger.debug( "Access Rights window has been activated" );
				
				hrSplitPanel.setSecondComponent( getAccessRightsPanel() );
			
			} else if ( buttonId == settingsId ) {
				logger.debug( "Settings window has been activated" );

				hrSplitPanel.setSecondComponent( getSettingsPanel() );
				
			} else if ( buttonId == importId ) {
				logger.debug( "Import window has been activated" );

				hrSplitPanel.setSecondComponent( getImportPanel() );
				
			} else if ( buttonId == exportId ) {
				logger.debug( "Export window has been activated" );

				hrSplitPanel.setSecondComponent( getExportPanel() );
				
			} else {
				logger.error( "Unknow button has been pressed" );
			}
		
			activeButtonId = buttonId; 
		}
		
		
	}

	private Panel getAccessRightsPanel() {
		if ( this.org == null ) {
			this.org = getTmsApplication().getSessionData().getUser().getOrganisation();
		}
		
		AccessRightsModel model = new AccessRightsModel( 
											getTmsApplication(), 
											org );
		
		
		AccessRightsView view = new AccessRightsView( model );
		
		
		
		return view;
	}
	
	private Panel getSettingsPanel() {
		Panel panel = new Panel( "Settings" );
//		panel.setStyleName( Runo.PANEL_LIGHT);
		panel.requestRepaint();
		
		return panel;
	}

	private Panel getImportPanel() {

		if ( this.org == null ) {
			this.org = getTmsApplication().getSessionData().getUser().getOrganisation();
		}

		ImportToolView view = new ImportToolView( getTmsApplication(), org ); 
		
		return view;
	}
	
	private Panel getExportPanel() {

		ExportToolView view = new ExportToolView( getTmsApplication(), org ); 
		
		return view;
	}

	public String toString() {
		return "Button value are [ "
				 + accRightsId + ", "
				 + settingsId + ", "
				 + importId + ", "
				 + exportId + "]";
	}
}

