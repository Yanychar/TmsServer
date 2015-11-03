package com.c2point.tms.web.ui.company.model;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.datalayer.AuthenticationFacade;
import com.c2point.tms.datalayer.DataFacade;
import com.c2point.tms.datalayer.OrganisationFacade;
import com.c2point.tms.datalayer.ShowType;
import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.TmsAccount;
import com.c2point.tms.entity.TmsUser;
import com.c2point.tms.entity.access.SecurityContext;
import com.c2point.tms.entity.access.SupportedFunctionType;
import com.c2point.tms.tools.exprt.DataExportProcessor;
import com.c2point.tms.tools.imprt.DataImportProcessor;
import com.c2point.tms.tools.imprt.v10PersonsImportProcessor;
import com.c2point.tms.tools.imprt.v10ProjectsImportProcessor;
import com.c2point.tms.util.StringUtils;
import com.c2point.tms.web.application.TmsApplication;
import com.c2point.tms.web.ui.AbstractModel;
import com.c2point.tms.web.ui.listeners.OrganisationAddedListener;
import com.c2point.tms.web.ui.listeners.OrganisationChangedListener;
import com.c2point.tms.web.ui.listeners.OrganisationDeletedListener;
import com.c2point.tms.web.ui.listeners.OrganisationListChangedListener;
import com.c2point.tms.web.ui.listeners.SelectionChangedListener;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;

@SuppressWarnings("serial")
public class CompaniesMgmtModel extends AbstractModel implements Property.ValueChangeListener {

	private static Logger logger = LogManager.getLogger( CompaniesMgmtModel.class.getName());

	// TODO
	// Provide customization by Setting for code creation
	// Possible parameters
 	private int 	COMPANY_CODE_LENGTH 	= 4;
 	private int 	PERSONNEL_CODE_LENGTH 	= 6;
 	private boolean	CODE_LEAD_ZERO 			= true;
	
	
	private Collection<Organisation>	listOrganisations;
	private Organisation				selectedOrganisation;

 	// Filtering options
 	private ShowType showDeleted;	// Show deleted entities
	
	public CompaniesMgmtModel( TmsApplication app ) {
		super( app );

		setToShowFilter( SupportedFunctionType.TMS_MANAGEMENT );
		
	 	setShowDeleted( ShowType.CURRENT );
		
	}

	public void initModel() {

		TmsUser sessionOwner = getSessionOwner();

		if ( sessionOwner == null ) {
			logger.error( "User defined in the Session was not found!!!" );
			return;
		}
		
		if ( sessionOwner.getOrganisation() == null ) {
			logger.error( "No Organisation has been defined for the user: '" + sessionOwner.getFirstAndLastNames() + "'!!!" );
			return;
		}
		
		listOrganisations = OrganisationFacade.getInstance().getOrganisations( getShowDeleted());
		
		fireOrganisationListChanged();
	}
	
	public Collection<Organisation> getOrganisationList() {
	
		if ( listOrganisations == null ) {
			listOrganisations = new ArrayList< Organisation >(); 
		}
		
		return listOrganisations;
	}

	public Organisation getSelectedOrganisation() {
		return this.selectedOrganisation;
	}
	
	public void selectOrganisation( Organisation organisation ) {
		this.selectedOrganisation = organisation;
		if ( logger.isDebugEnabled()) logger.debug( "Organisation was set as selected: " + selectedOrganisation );
		fireSelectionChanged();
	}
	
	public ShowType getShowDeleted() { return showDeleted; 	}
	public void setShowDeleted(ShowType showDeleted) { this.showDeleted = showDeleted; }

	public void addChangedListener( OrganisationAddedListener listener ) {
		listenerList.add( OrganisationAddedListener.class, listener);
	}
	public void addChangedListener( OrganisationChangedListener listener ) {
		listenerList.add( OrganisationChangedListener.class, listener);
	}
	public void addChangedListener( OrganisationDeletedListener listener ) {
		listenerList.add( OrganisationDeletedListener.class, listener);
	}
	public void addChangedListener( SelectionChangedListener listener ) {
		listenerList.add( SelectionChangedListener.class, listener);
	}
	public void addChangedListener( OrganisationListChangedListener listener ) {
		listenerList.add( OrganisationListChangedListener.class, listener);
	}

	public void deleteListener( OrganisationAddedListener listener ) { listenerList.remove( OrganisationAddedListener.class, listener ); }
	public void deleteListener( OrganisationChangedListener listener ) { listenerList.remove( OrganisationChangedListener.class, listener ); }
	public void deleteListener( OrganisationDeletedListener listener ) { listenerList.remove( OrganisationDeletedListener.class, listener ); }
	public void deleteListener( SelectionChangedListener listener ) { listenerList.remove( SelectionChangedListener.class, listener ); }
	public void deleteListener( OrganisationListChangedListener listener ) { listenerList.remove( OrganisationListChangedListener.class, listener ); }

	
	private void fireOrganisationAdded( Organisation organisation ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == OrganisationAddedListener.class) {
	    		(( OrganisationAddedListener )listeners[ i + 1 ] ).wasAdded( organisation );
	         }
	     }
	}

	private void fireOrganisationChanged( Organisation organisation ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == OrganisationChangedListener.class) {
	    		(( OrganisationChangedListener )listeners[ i + 1 ] ).wasChanged( organisation );
	         }
	     }
		
	}

	private void fireOrganisationDeleted( Organisation organisation ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == OrganisationDeletedListener.class) {
	    		(( OrganisationDeletedListener )listeners[ i + 1 ] ).wasDeleted( organisation );
	         }
	     }
		
	}

	protected void fireOrganisationListChanged() {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == OrganisationListChangedListener.class) {
	    		(( OrganisationListChangedListener )listeners[ i + 1 ] ).listWasChanged();
	         }
	     }
	 }
	
	private void fireSelectionChanged() {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == SelectionChangedListener.class) {
	    		(( SelectionChangedListener )listeners[ i + 1 ] ).selectionChanged();
	         }
	     }
	}


	@Override
	public void valueChange( ValueChangeEvent event ) {
		this.selectedOrganisation = ( Organisation )event.getProperty().getValue();
		if ( logger.isDebugEnabled()) logger.debug( "Organisation was set as selected: " + this.selectedOrganisation );
		fireSelectionChanged();
	}	

	public boolean codeExists( Organisation organisation ) {
		return codeExists( organisation.getCode());
	}
	public boolean codeExists( String code ) {
		
		if ( code != null && code .length() > 0 && listOrganisations != null && listOrganisations.size() > 0 ) {
			for ( Organisation  organisation : listOrganisations ) {
				if ( organisation != null && organisation.getCode() != null && organisation.getCode().compareToIgnoreCase( code ) == 0 ) {
					return true;
				}
			}
		}
		
		return false;
	}
/*
	public Organisation addOrganisation() {
		return addOrganisation( selectedOrganisation );
	}
*/	
	
	public Organisation addOrganisation( Organisation organisation ) {
		Organisation result = null;

		// Cannot add the Project with code existed already
		
		if ( !codeExists( organisation.getCode())) {
			
			Organisation newOrganisation = OrganisationFacade.getInstance().addOrganisation( organisation );
			if ( newOrganisation != null ) {
				
				// Now Security groups and security assignments can be done
				if ( newOrganisation != null ) {
					
					listOrganisations.add( newOrganisation ); 
					fireOrganisationListChanged();
					selectOrganisation( newOrganisation );				
				}
				
				result = newOrganisation;
			}
			
								
		} else {
			logger.error( "Cannot add the Organisation because the Organisation with the same Code already exists already!" );
		}

		return result;
	}
/*
	public Organisation updateOrganisation() {
		return updateOrganisation( selectedOrganisation );
	}
*/	
	public Organisation updateOrganisation( Organisation organisation ) {
		Organisation result = null;

		// Update DB
		if ( organisation != null ) {

			try {
				Organisation newOrganisation = OrganisationFacade.getInstance().updateOrganisation( organisation );
				
				fireOrganisationChanged( newOrganisation );
				
				result = newOrganisation;
								
			} catch ( Exception e ) {
				logger.error( "Failed to update Organisation: " + organisation );
				logger.error( e );
			}
			
			
		}
		
		return result;
	}

	public boolean removeOrganisation( Organisation organisation ) {
		boolean bRes = false;

		// Delete here means mark as deleted but leave in DB
		// Deleted organisation shall not be visible in OrganisationLists
		if ( organisation != null ) {
			// Mark as deleted
			organisation.setDeleted();
			
			

			try {
				//Store in DB
				bRes = OrganisationFacade.getInstance().deleteOrganisation( organisation );
				
				if ( bRes ) {
					// Delete from model
					if ( listOrganisations.remove( organisation )) {				
						// Fire 'deleted' event
						fireOrganisationDeleted( organisation );
					} else {
						logger.error( "Organisation mas not found in the model to be deleted!" );
					}
					
					bRes = true;
				} else {
					logger.error( "DB failed to update deletion status!" );
				}
								
			} catch ( Exception e ) {
				logger.error( "Failed to update Organisation: " + organisation );
				logger.error( e );
			}
			
			
		} else {
			logger.error( "Organisation cannot be null for deletion!" );
		}
		
		
		// 
		
		return bRes;
	}

/*	
	public boolean importOrgansation() {
		boolean res = false;
		
		if ( this.selectedOrganisation != null ) {

			Organisation org = DataFacade.getInstance().find( Organisation.class, selectedOrganisation.getId());
			if ( org != null ) {
				boolean resPersonExport;
				boolean resProjectExport;
				
				DataImportProcessor processor;
	
				logger.debug( "Start data IMPORT for Organisation: '" + org.getName() + "'" );
				
				// Import Personnel
				processor = new v10PersonsImportProcessor();
				resPersonExport = processor.process( org )
						;
				// Import projects
				processor = new v10ProjectsImportProcessor();

				resProjectExport = processor.process( org );
			
				res = resPersonExport && resProjectExport;
				
			} else {
				logger.error( "Did not find Organisation: '" + selectedOrganisation.getName() + "' in DB." );
			}
			
		}
		
		return res;
	}
	
	public boolean exportOrgansation() {
		boolean res = false;

		if ( this.selectedOrganisation != null ) {

			DataExportProcessor processor;

			Organisation org = DataFacade.getInstance().find( Organisation.class, selectedOrganisation.getId());
			if ( org != null ) {
				logger.debug( "Start data EXPORT for Organisation: '" + org.getName() + "'" );

				processor = DataExportProcessor.getExportProcessor( org );
				if ( processor != null ) {
//					res = processor.process( org );
				}
				
			} else {
				logger.error( "Did not find Organisation: '" + selectedOrganisation.getName() + "' in DB." );
			}
			
		}
		
		
		return res;
	}
*/	

	public void setCompanyToShow( SupportedFunctionType fromMenu ) {

		if ( getToShowFilter() != fromMenu ) { 

			SecurityContext context = getSessionOwner().getContext();
			
			if ( fromMenu == SupportedFunctionType.TMS_MANAGEMENT  
				&& 
				 context.isAccessible( SupportedFunctionType.TMS_MANAGEMENT )) {
				
				setToShowFilter( fromMenu );
			}
		}
			
	}

	
	public String generateCompanyCode() {
		
		int tmpCount = listOrganisations.size() + 1;
		String generatedCode = createStrCode( tmpCount++, COMPANY_CODE_LENGTH );
		
		while ( codeExists( generatedCode )) {
			generatedCode = createStrCode( tmpCount++, COMPANY_CODE_LENGTH );
		}
		
		return generatedCode;
		
	}

	public String generateServiceOwnerCode( Organisation org ) {

		int tmpCount = 1;
		String generatedCode = createStrCode( tmpCount++, PERSONNEL_CODE_LENGTH );
		
		while ( org.getUsers().containsKey( generatedCode )) {
			generatedCode = createStrCode( tmpCount++, PERSONNEL_CODE_LENGTH );
		}
		
		return generatedCode;
		
	}
	
	private String createStrCode( int i, int length ) {

		// Parameters
	 	// boolean CODE_DIGI_ONLY	= true;
	 	// int 	CODE_LENGTH 		= 6;
	 	// boolean	CODE_LEAD_ZERO 	= true;
		
		String code;
		
		if ( CODE_LEAD_ZERO ) {
			code = StringUtils.padLeftZero( i, length );
		} else {
			code = Integer.toString( i );
		}

        logger.debug( "Generated Code = '" + code +  "'" );
		
		return code;
		
	}

	public boolean isUniqueUsrname( String usrname, TmsUser serviceOwner ) {
		
		TmsAccount  account = AuthenticationFacade.getInstance().findByUserName( usrname );
		
		if ( account != null && account.getUser() != null && account.getUser().getId() != serviceOwner.getId()) {
			return false;
		}
		
		return true;
	}

}
