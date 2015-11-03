package com.c2point.tms.web.ui.accessrights;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.access.AccessRight;
import com.c2point.tms.entity.access.SecurityGroup;
import com.c2point.tms.entity.access.SupportedFunctionType;
import com.c2point.tms.web.ui.listeners.SGSelectionChangedListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;

@SuppressWarnings("serial")
public class AccessRightsGrid extends GridLayout implements SGSelectionChangedListener,
															ValueChangeListener {

	private static Logger logger = LogManager.getLogger( AccessRightsGrid.class.getName());

	private int	columns 	= 1 + 3;
	private int	rows 		= 1 + 8;
	
	private String NO_ACCESS;
	private String READ_ONLY;
	private String READ_WRITE;
	
	private AccessRightsModel 	model;
	
	private boolean initialization;
	
	private boolean showTms;
	private boolean readOnlyTms;
	private boolean readOnlyCompany;

	public AccessRightsGrid( AccessRightsModel model ) {
		super();
		
		this.model = model;

		NO_ACCESS = model.getApp().getResourceStr( "access.type.no" );
		READ_ONLY = model.getApp().getResourceStr( "access.type.r" );
		READ_WRITE = model.getApp().getResourceStr( "access.type.rw" );
		
		showTms = false;
		readOnlyTms = true;
		readOnlyCompany = true;
		
		calculateRowsCols();
		
		initUI();

		dateToView();

		model.addChangedListener( this );
	}

	private void calculateRowsCols() {
		
		if ( model.getSecurityContext().isAccessible( SupportedFunctionType.TMS_MANAGEMENT )
				&&
			 !model.isSingleOrg()) {
			
			columns++;
			rows++;
			showTms = true;
			
		}
		if ( model.getSecurityContext().isWrite( SupportedFunctionType.ACCESS_RIGHTS_COMPANY )) {
			readOnlyCompany = false;
		}

		if ( model.getSecurityContext().isWrite( SupportedFunctionType.ACCESS_RIGHTS_TMS )) {
			readOnlyTms = false;
		}

		
		this.setColumns( columns );
		this.setRows( rows );
	}
	private void initUI() {
	
		// Set up the scope headers (OWN, TEAM, COMPANY)
		addHeader();
		// Set up functions name column
		addFunctions();
		
	}
	
	private void dateToView() {
		SecurityGroup group = model.getSelectedGroup();
		
		if ( group != null ) {
			int row, col;
			ComboBox combo;
			AccessRight ar;
			
			initialization = true;

			for ( SupportedFunctionType type : SupportedFunctionType.values()) {
				row = functionToRow( type );
				col = functionToCol( type );
				
				if ( row > 0 && col > 0 && row < rows && col < columns ) {
					combo = ( ComboBox )this.getComponent( col, row );
					if ( combo == null ) {
						// No ComboBox yet. Shall be added
						combo = new ComboBox();
						
						combo.setWidth( "10em" );
//						selector.setItemCaptionMode( Select.ITEM_CAPTION_MODE_EXPLICIT );
//						selector.setFilteringMode( Filtering.FILTERINGMODE_STARTSWITH );
						combo.setImmediate( true );        
						combo.setNullSelectionAllowed( false );
						
						combo.addItem( NO_ACCESS );
						combo.addItem( READ_ONLY );
						combo.addItem( READ_WRITE );

						this.addComponent( combo, col, row );
						combo.addValueChangeListener( this );
						
						logger.debug( "  ComboBox added into position: [ " + row + ", " + col + " ]" );
					}
					combo.setReadOnly( false );
					
					ar = group.getRights( type );
					if ( ar != null ) {
						combo.setData( ar );
						
						if ( ar.isWrite()) {
							combo.select( READ_WRITE );
							logger.debug( "   AccessRights for '" + type + "': WRITE&read"  );
						} else if ( ar.isRead()) {
							combo.select( READ_ONLY );
							logger.debug( "   AccessRights for '" + type + "': READ only"  );
						} else {
							combo.select( NO_ACCESS );
							logger.debug( "   AccessRights for '" + type + "': NO ACCESS"  );
						}
					} else {
						logger.debug( "   AccessRights for '" + type + "': NOT FOUND"  );
						// TODO
						// Add access rights here
					}

					if ( type == SupportedFunctionType.ACCESS_RIGHTS_TMS
							|| 
						 type == SupportedFunctionType.SETTINGS_TMS
							|| 
						 type == SupportedFunctionType.IMPORTEXPORT_TMS
							|| 
						 type == SupportedFunctionType.TMS_MANAGEMENT ) {
						
						combo.setReadOnly( readOnlyTms );
					} else {
						combo.setReadOnly( readOnlyCompany );
					}
					
				} else {
					logger.debug( "   AccessRights for '" + type + "': NOT ALLOWED"  );
				}
				
			}

			initialization = false;
			
		} else {
//			logger.error( "No SecurityGroup selected. All Access Rights are 'No access'" );
		}
		
	}

	private void addHeader() {
		// Set up the scope headers (OWN, TEAM, COMPANY)
		this.addComponent( new Label( model.getApp().getResourceStr( "access.scope.own" )), 1, 0 );
		this.addComponent( new Label( model.getApp().getResourceStr( "access.scope.team" )), 2, 0 );
		this.addComponent( new Label( model.getApp().getResourceStr( "access.scope.company" )), 3, 0 );
		
		if ( showTms ) {
			this.addComponent( new Label( model.getApp().getResourceStr( "access.scope.tms" )), 4, 0 );
		}
	}

	private void addFunctions() {

		// Set up functions name column
		this.addComponent( new Label( model.getApp().getResourceStr( "access.function.presence" )), 0, 1 );
		this.addComponent( new Label( model.getApp().getResourceStr( "access.function.reports" )), 0, 2 );
		this.addComponent( new Label( model.getApp().getResourceStr( "access.function.projects" )), 0, 3 );
		this.addComponent( new Label( model.getApp().getResourceStr( "access.function.personnel" )), 0, 4 );
		this.addComponent( new Label( model.getApp().getResourceStr( "access.function.consolidates" )), 0, 5 );
		this.addComponent( new Label( model.getApp().getResourceStr( "access.function.rights" )), 0, 6 );
		this.addComponent( new Label( model.getApp().getResourceStr( "access.function.settings" )), 0, 7 );
		this.addComponent( new Label( model.getApp().getResourceStr( "access.function.impexp" )), 0, 8 );
		
		if ( showTms ) {
			this.addComponent( new Label( model.getApp().getResourceStr( "access.function.tms" )), 0, 9 );
		}
		
	}
	
	private int functionToRow( SupportedFunctionType type ) {
		int ret = -1;
		
		switch ( type ) {
			case PRESENCE_OWN: {
				ret = 1;
				break;
			}
			case PRESENCE_TEAM: {
				ret = 1;
				break;
			}
			case PRESENCE_COMPANY: {
				ret = 1;
				break;
			}
			case REPORTS_OWN: {
				ret = 2;
				break;
			}
			case REPORTS_TEAM: {
				ret = 2;
				break;
			}
			case REPORTS_COMPANY: {
				ret = 2;
				break;
			} 
			case PROJECTS_OWN: {
				ret = 3;
				break;
			} 
			case PROJECTS_TEAM: {
				ret = 3;
				break;
			}
			case PROJECTS_COMPANY: {
				ret = 3;
				break;
			}
			case PERSONNEL_OWN: {
				ret = 4;
				break;
			}
			case PERSONNEL_TEAM: {
				ret = 4;
				break;
			}
			case PERSONNEL_COMPANY: {
				ret = 4;
				break;
			}
			case CONSOLIDATE_OWN: {
				ret = 5;
				break;
			}
			case CONSOLIDATE_TEAM: {
				ret = 5;
				break;
			}
			case CONSOLIDATE_COMPANY: {
				ret = 5;
				break;
			}
			case ACCESS_RIGHTS_COMPANY: {
				ret = 6;
				break;
			}
			case ACCESS_RIGHTS_TMS: {
				ret = 6;
				break;
			}
			case SETTINGS_COMPANY: {
				ret = 7;
				break;
			}
			case SETTINGS_TMS: {
				ret = 7;
				break;
			}
			case IMPORTEXPORT_COMPANY: {
				ret = 8;
				break;
			}
			case IMPORTEXPORT_TMS: {
				ret = 8;
				break;
			}
			case TMS_MANAGEMENT: {
				if ( showTms ) {
					ret = 9;
				} else {
					ret = -1;
				}
				break;
			}
		
		}
		
		return ret;
	}
	
	private int functionToCol( SupportedFunctionType type ) {
		int ret = -1;
		
		switch ( type ) {
			case PRESENCE_OWN: {
				ret = 1;
				break;
			}
			case PRESENCE_TEAM: {
				ret = 2;
				break;
			}
			case PRESENCE_COMPANY: {
				ret = 3;
				break;
			}
			case REPORTS_OWN: {
				ret = 1;
				break;
			}
			case REPORTS_TEAM: {
				ret = 2;
				break;
			}
			case REPORTS_COMPANY: {
				ret = 3;
				break;
			} 
			case CONSOLIDATE_OWN: {
				ret = 1;
				break;
			}
			case CONSOLIDATE_TEAM: {
				ret = 2;
				break;
			}
			case CONSOLIDATE_COMPANY: {
				ret = 3;
				break;
			}
			case PROJECTS_OWN: {
				ret = 1;
				break;
			} 
			case PROJECTS_TEAM: {
				ret = 2;
				break;
			}
			case PROJECTS_COMPANY: {
				ret = 3;
				break;
			}
			case PERSONNEL_OWN: {
				ret = 1;
				break;
			}
			case PERSONNEL_TEAM: {
				ret = 2;
				break;
			}
			case PERSONNEL_COMPANY: {
				ret = 3;
				break;
			}
			case ACCESS_RIGHTS_COMPANY: {
				ret = 3;
				break;
			}
			case ACCESS_RIGHTS_TMS: {
				if ( model.getSecurityContext().isAccessible( SupportedFunctionType.TMS_MANAGEMENT )) {
					ret = 4;
				} else {
					ret = -1;
				}
				break;
			}
			case SETTINGS_COMPANY: {
				ret = 3;
				break;
			}
			case SETTINGS_TMS: {
				if ( model.getSecurityContext().isAccessible( SupportedFunctionType.TMS_MANAGEMENT )) {
					ret = 4;
				} else {
					ret = -1;
				}
				break;
			}
			case IMPORTEXPORT_COMPANY: {
				ret = 3;
				break;
			}
			case IMPORTEXPORT_TMS: {
				if ( model.getSecurityContext().isAccessible( SupportedFunctionType.TMS_MANAGEMENT )) {
					ret = 4;
				} else {
					ret = -1;
				}
				break;
			}
			case TMS_MANAGEMENT: {
				if ( showTms ) {
					ret = 4;
				} else {
					ret = -1;
				}
				break;
			}
		
		}
		
		return ret;
	}
	
	@Override
	public void selectionChanged( SecurityGroup group ) {
		logger.debug( "Received selectionChanged event from Model." );
		dateToView();
		
	}

	@Override
	public void valueChange(ValueChangeEvent event) {

		logger.debug( "Received AccessRight ComboBox selection event. Sender: " + event.getProperty().getClass().getName());
		
		
		if ( event.getProperty() instanceof ComboBox ) {
			AccessRight ar = ( AccessRight )(( ComboBox )event.getProperty()).getData();  

			String arStr = ( String )(( ComboBox )event.getProperty()).getValue();
			if ( arStr != null ) {
				if ( arStr.compareToIgnoreCase( READ_ONLY ) == 0 ) {
					ar.setRead();
					ar.clearWrite();
					if ( !initialization ) model.updated();
				} else if ( arStr.compareToIgnoreCase( READ_WRITE ) == 0 ) {
					ar.setRead();
					ar.setWrite();
					if ( !initialization ) model.updated();
				} else {
					ar.clearRead();
					ar.clearWrite();
					if ( !initialization ) model.updated();
				}
			} else {
				logger.error( "Did not find AccessRights record attached to ComboBox!" );
				ar.clearRead();
				ar.clearWrite();
				if ( !initialization ) model.updated();
			}
		}
		
	}
	
	
}
