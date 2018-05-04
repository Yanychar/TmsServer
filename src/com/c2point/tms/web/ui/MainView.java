package com.c2point.tms.web.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.configuration.Versions;
import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.access.SecurityContext;
import com.c2point.tms.entity.access.SupportedFunctionType;
import com.c2point.tms.web.application.TmsApplication;
import com.c2point.tms.web.ui.approveview.ApproveView;
import com.c2point.tms.web.ui.checkinoutview.CheckInOutView;
import com.c2point.tms.web.ui.company.CompaniesManagementView;
import com.c2point.tms.web.ui.companytools.CompanyToolsView;
import com.c2point.tms.web.ui.projectsview.ProjectsView;
import com.c2point.tms.web.ui.reportsmgmt.timereports.TimeReportsView;
import com.c2point.tms.web.ui.reportsmgmt.travelreports.TravelReportsView;
import com.c2point.tms.web.ui.reportview.ReportModuleFactory;
import com.c2point.tms.web.ui.reportview.ReportModuleIF;
import com.c2point.tms.web.ui.stuff.PersonalProfileDialog;
import com.c2point.tms.web.ui.stuff.PersonalProfileModel;
import com.c2point.tms.web.ui.stuff.StuffManagementView;
import com.c2point.tms.web.ui.subcontracting.SubcontractingView;
import com.c2point.tms.web.ui.tools.TMSToolsView;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

public class MainView extends VerticalLayout { //implements Organisation.PropertyChangedListener {

	private static final long serialVersionUID = 1L;

	private static Logger logger = LogManager.getLogger( MainView.class.getName());

	private TabSheet mainTabSheet;
	private SubTabSheet myOwnTabSheet;
	private SubTabSheet myTeamTabSheet;
	private SubTabSheet myCompanyTabSheet;
	private SubTabSheet myAdminTabSheet;
	
	private TmsApplication app;

	
	public MainView( TmsApplication app ) {
		this.app = app;
	}
	
	public void initWindow() {
		setSizeFull();
		setSpacing( true );
		
		addComponent( getTitleComponent()  );
		getTabComponent();
		addComponent( mainTabSheet );
		setExpandRatio( mainTabSheet, 1.0F );
		
	}

	private Component getTitleComponent() {

		HorizontalLayout titleLayout = new HorizontalLayout();
		titleLayout.setWidth( "100%" );
		titleLayout.setHeight( "40px" );
		titleLayout.setMargin( new MarginInfo( false, true, false, true )); // Enable horizontal margins

/*		
		Label titleLabel = new Label( app.getResourceStr( "mainWindow.label" ));
		titleLabel.setStyleName( Runo.LABEL_H1 );
*/		
		Label titleLabel = new Label( "<h1>" + app.getResourceStr( "mainWindow.label" ) + "</h1>", ContentMode.HTML );
//		titleLabel.setStyleName( Runo.LABEL_H1 );

		
		
//		titleLayout.addComponent( titleLabel );

		Label titleVersion = new Label( "<b>ver " + Versions.getSwVersion() + "</b>", ContentMode.HTML );
//		titleVersion.setStyleName( Runo.LABEL_H2 );

		
		
		HorizontalLayout labelLayout = new HorizontalLayout();
		labelLayout.setSizeUndefined();
		labelLayout.setSpacing( true );
		
		labelLayout.addComponent( titleLabel );
		labelLayout.addComponent( titleVersion );
		labelLayout.setComponentAlignment( titleVersion, Alignment.BOTTOM_RIGHT );
		
		
		titleLayout.addComponent( labelLayout );
		titleLayout.setComponentAlignment( labelLayout, Alignment.MIDDLE_LEFT );
		
		
		
		
//		titleLayout.setComponentAlignment( titleLabel, Alignment.MIDDLE_LEFT);
		
		final MenuBar menubar = new MenuBar();
		titleLayout.addComponent( menubar );		
		
		MenuBar.MenuItem usermenu = menubar.addItem( 
				app.getSessionData().getUser().getFirstAndLastNames(), new ThemeResource( "../runo/icons/16/arrow-down.png"), null);
		
		MenuBar.Command settingsCommand = new MenuBar.Command() {

			private static final long serialVersionUID = 1L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				logger.debug( "User Setting has been selected" );
				
				PersonalProfileModel model = new PersonalProfileModel( app );
				
				app.addWindow( new PersonalProfileDialog( model ));
				
				
			}
			
		};
		@SuppressWarnings("serial")
		MenuBar.Command logoutCommand = new MenuBar.Command() {

			@Override
			public void menuSelected(MenuItem selectedItem) {
				logger.debug( "Logout has been selected" );
				app.close();
				
			}
			
		};
		

		usermenu.addItem( "Settings", null, settingsCommand );
		usermenu.addItem( "Logout",   null, logoutCommand );
		
		titleLayout.addComponent( menubar );		
		titleLayout.setComponentAlignment( menubar, Alignment.MIDDLE_RIGHT );
		
		
		
		return titleLayout;
	}

	/*
	 * Create 'Own' tab 
	 */
	private boolean fillOwnTab() {

		myOwnTabSheet = new SubTabSheet();
//		myOwnTabSheet.addStyleName( Runo.TABSHEET_SMALL );

		myOwnTabSheet.addTab( getMyTimeReportsView(), app.getResourceStr( "menu.item.report.time" ));
		myOwnTabSheet.addTab( getMyTravelReportsView(), app.getResourceStr( "menu.item.report.travel" ));
		
		return true;
		
	}
	
	/*
	 * Create 'Team' tab 
	 */
	private boolean fillTeamTab() {

		boolean isTeamTab = false;
		
		SecurityContext context = this.app.getSessionData().getContext();

		myTeamTabSheet = new SubTabSheet();
	//	myTeamTabSheet.addStyleName( Runo.TABSHEET_SMALL );
	
		if ( context.isRead( SupportedFunctionType.REPORTS_TEAM )) {
			myTeamTabSheet.addTab( getTeamApprovalView(), app.getResourceStr( "menu.item.approval" ));
			isTeamTab = true;
		}
		
		if ( context.isRead( SupportedFunctionType.PRESENCE_TEAM )) {
			myTeamTabSheet.addTab( getTeamCheckInOutView(), app.getResourceStr( "menu.item.presence" ));
			isTeamTab = true;
		}
		if ( context.isRead( SupportedFunctionType.PROJECTS_TEAM )) {
			myTeamTabSheet.addTab( getTeamProjectsView(), app.getResourceStr( "menu.item.projects" ));
			isTeamTab = true;
		}
		if ( context.isRead( SupportedFunctionType.CONSOLIDATE_TEAM )) {
			myTeamTabSheet.addTab(  
					getConsolidatedReportsView( SupportedFunctionType.CONSOLIDATE_TEAM ),
					app.getResourceStr( "menu.item.reports" )
			);
			
			isTeamTab = true;
		}
		
		return isTeamTab;
	}

	/*
	 * Create 'Company' tab 
	 */
	@SuppressWarnings("unused")
	private boolean fillCompanyTab() {

		boolean isCompanyTab = false;
		
		SecurityContext context = this.app.getSessionData().getContext();

		myCompanyTabSheet = new SubTabSheet();
//		myCompanyTabSheet.addStyleName( Runo.TABSHEET_SMALL );

		if ( context.isRead( SupportedFunctionType.REPORTS_COMPANY )) {
			myCompanyTabSheet.addTab( getCompanyApprovalView(), app.getResourceStr( "menu.item.approval" ));
			isCompanyTab = true;
		}
		
		if ( context.isRead( SupportedFunctionType.PRESENCE_COMPANY )) {
			myCompanyTabSheet.addTab( getCompanyCheckInOutView(), app.getResourceStr( "menu.item.presence" ));
			isCompanyTab = true;
		}
		
		if ( context.isRead( SupportedFunctionType.PROJECTS_COMPANY )) {
			myCompanyTabSheet.addTab( getCompanyProjectsView(), app.getResourceStr( "menu.item.projects" ));
			isCompanyTab = true;
		}
		
		if ( context.isRead( SupportedFunctionType.CONSOLIDATE_COMPANY )) {
			myCompanyTabSheet.addTab( 
					getConsolidatedReportsView( SupportedFunctionType.CONSOLIDATE_COMPANY ),
					app.getResourceStr( "menu.item.reports" )
			);
			isCompanyTab = true;
		}
		
		if ( context.isRead( SupportedFunctionType.PERSONNEL_COMPANY )) {
			myCompanyTabSheet.addTab( getCompanyPeopleView(), app.getResourceStr( "menu.item.people" ));
			isCompanyTab = true;
		}
		
		if ( context.isAccessible( SupportedFunctionType.SETTINGS_COMPANY ) ||
			 context.isAccessible( SupportedFunctionType.ACCESS_RIGHTS_COMPANY ) ||
			 context.isAccessible( SupportedFunctionType.IMPORTEXPORT_COMPANY )) {
			myCompanyTabSheet.addTab( getCompanyToolsView(), app.getResourceStr( "menu.item.tools" ));
			isCompanyTab = true;
		}
		
		if ( false ) {
			myCompanyTabSheet.addTab( getSubcontractingView(), "Subcontracting" );
			isCompanyTab = true;
		}
			
		
		
		return isCompanyTab;
	}

	/*
	 * Create 'Admin' tab 
	 */
	private boolean fillAdminTab() {

		boolean isAdminTab = false;
		
		SecurityContext context = this.app.getSessionData().getContext();

		myAdminTabSheet = new SubTabSheet();
//		myAdminTabSheet.addStyleName( Runo.TABSHEET_SMALL );
		
		if ( context.isRead( SupportedFunctionType.TMS_MANAGEMENT )) {
			myAdminTabSheet.addTab( getAdminCompaniesView(), app.getResourceStr( "menu.item.companies" ));
			if ( context.isRead( SupportedFunctionType.SETTINGS_TMS )) {
				myAdminTabSheet.addTab( getTMSToolsView(), app.getResourceStr( "menu.item.tools" ));
			}
			isAdminTab = true;
		}
		
		return isAdminTab;
	}

	private Component getTabComponent() {

		
		mainTabSheet = new MainTabSheet();
		mainTabSheet.setSizeFull();
		
		// Add 'Own' tab
		if ( fillOwnTab()) {
			mainTabSheet.addTab( myOwnTabSheet, app.getResourceStr( "menu.item.own" ));
		}

		// Add Team tab
		if ( fillTeamTab()) {
			mainTabSheet.addTab( myTeamTabSheet, app.getResourceStr( "menu.item.team" ));
		}
		
		// Add Company tab
		if ( fillCompanyTab()) {
			mainTabSheet.addTab( myCompanyTabSheet, app.getResourceStr( "menu.item.company" ));
		}
		
		// Add Admin tab
		if ( fillAdminTab()) {
			mainTabSheet.addTab( myAdminTabSheet, app.getResourceStr( "menu.item.admin" ));
		}
		
		return mainTabSheet;
	}
	
	
	private AbstractMainView getMyTimeReportsView() {
		TimeReportsView view = new TimeReportsView( app, SupportedFunctionType.REPORTS_OWN );
		
		return view;
	}

	private AbstractMainView getMyTravelReportsView() {
		TravelReportsView view = new TravelReportsView( app );
		
		return view;
	}

	private AbstractMainView getTeamApprovalView() {
		ApproveView view = new ApproveView( app, SupportedFunctionType.REPORTS_TEAM );
		
		return view;
	}

	private AbstractMainView getTeamCheckInOutView() {
		CheckInOutView view = new CheckInOutView( app, SupportedFunctionType.PRESENCE_TEAM );
		
		return view;
	}

	private AbstractMainView getTeamProjectsView() {
		ProjectsView view = new ProjectsView( app, SupportedFunctionType.PROJECTS_TEAM );
		
		return view;
	}

	@SuppressWarnings("unused")
	private AbstractMainView getInProgressView() {
		InProgressView view = new InProgressView( app );
		
		return view;
	}

	private AbstractMainView getAdminCompaniesView() {
		CompaniesManagementView view = new CompaniesManagementView( app, SupportedFunctionType.TMS_MANAGEMENT );
		return view;
	}

	private AbstractMainView getTMSToolsView() {
		TMSToolsView view = new TMSToolsView( app );
		
		return view;
	}
/*
	private AbstractMainView getTestView() {
		TestView view = new TestView( app );
		
		return view;
	}
*/

	private AbstractMainView getCompanyApprovalView() {
		ApproveView view = new ApproveView( app, SupportedFunctionType.REPORTS_COMPANY );
		
		return view;
	}

	private AbstractMainView getCompanyCheckInOutView() {
		CheckInOutView view = new CheckInOutView( app, SupportedFunctionType.PRESENCE_COMPANY );
		
		return view;
	}

	private AbstractMainView getCompanyProjectsView() {

		ProjectsView view = new ProjectsView( app, SupportedFunctionType.PROJECTS_COMPANY );
		
		return view;
	}

	private static boolean REPORT_CHECKINOUT_DEFAILT_VALUE = false;
	
	private AbstractMainView getConsolidatedReportsView( SupportedFunctionType type ) {

		// Read what method to use for Consolidated Reporting
		
		boolean useCheckInOutModule;
		
		try {
			Organisation org = app.getSessionData().getUser().getOrganisation();

			useCheckInOutModule = Boolean.parseBoolean( 
							org.getProperty( "company.consolidate.reports.checkinout" ));

			if ( logger.isDebugEnabled()) 
				logger.debug( "Property 'company.consolidate.reports.checkinout' was read. == "
							  + useCheckInOutModule );
						
		} catch ( Exception e ) {
			if ( logger.isDebugEnabled()) 
				logger.debug( "Cannot read 'company.consolidate.reports.checkinout' proprty. "
							  + "Will be set to default: " + REPORT_CHECKINOUT_DEFAILT_VALUE );
			
			useCheckInOutModule = REPORT_CHECKINOUT_DEFAILT_VALUE;

		}
		
		ReportModuleFactory factory = new ReportModuleFactory();
		
		ReportModuleIF reportModule = factory.getReportModule(
				useCheckInOutModule,
				app, 
				app.getSessionData().getUser().getOrganisation(), 
				type );
		
		AbstractMainView result = reportModule.getConfiguredReportView(); 
		if ( type == SupportedFunctionType.CONSOLIDATE_TEAM ) {
		
//			teamTab = result;
		
		} else if ( type == SupportedFunctionType.CONSOLIDATE_TEAM ) {
			
//			companyTab = result;
		}
		
		return result;
		
	}

	private AbstractMainView getCompanyPeopleView() {
		StuffManagementView view = new StuffManagementView( app, SupportedFunctionType.PERSONNEL_COMPANY );

		return view;
	}

	private AbstractMainView getCompanyToolsView() {
		CompanyToolsView view = new CompanyToolsView( app );
		
		return view;
	}
	
	private AbstractMainView getSubcontractingView() {
		SubcontractingView view = new SubcontractingView( app, SupportedFunctionType.SUBCONTRACTING_COMPANY );
		
		return view;
	}
	
	
/*
	@Override
	public void propertyWasChanged( String name, String value ) {

		if ( name != null && name.compareToIgnoreCase( "company.consolidate.reports.checkinout" ) == 0 ) {
			
			propertiesWereChanged();
		}
				
		
	}

	@Override
	public void propertiesWereChanged() {

		if ( logger.isDebugEnabled()) logger.debug( "PropertiesWereChanged event has been received by MainView" );
		
		Tab tab = myTeamTabSheet.getTab( teamTab );
		int tabIndex = myTeamTabSheet.getTabPosition( tab );
		
		myTeamTabSheet.removeTab( tab );
		
		myTeamTabSheet.addTab(   
				getConsolidatedReportsView( SupportedFunctionType.CONSOLIDATE_TEAM ),
				app.getResourceStr( "menu.item.reports" ),
				null,
				tabIndex
		);
				
		tab = myCompanyTabSheet.getTab( companyTab );
		tabIndex = myCompanyTabSheet.getTabPosition( tab );
		
		myCompanyTabSheet.removeTab( tab );
		
		myCompanyTabSheet.addTab(   
				getConsolidatedReportsView( SupportedFunctionType.CONSOLIDATE_COMPANY ),
				app.getResourceStr( "menu.item.reports" ),
				null,
				tabIndex
		);

		
	}
*/


}

