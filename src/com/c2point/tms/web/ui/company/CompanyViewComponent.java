package com.c2point.tms.web.ui.company;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.Organisation;
import com.c2point.tms.web.ui.ValueLabel;
import com.c2point.tms.web.ui.company.model.CompaniesMgmtModel;
import com.c2point.tms.web.ui.listeners.OrganisationChangedListener;
import com.c2point.tms.web.ui.listeners.SelectionChangedListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.Runo;

@SuppressWarnings("serial")
public class CompanyViewComponent extends GridLayout implements SelectionChangedListener, OrganisationChangedListener {

	private static Logger logger = LogManager.getLogger( CompanyViewComponent.class.getName());

	private CompaniesMgmtModel	model;

    private ValueLabel 	companyCode;
    private ValueLabel 	name;
    private ValueLabel 	tunnus;
    private ValueLabel 	address;
    private ValueLabel 	phone;
    private ValueLabel 	companyEmail;
	private ValueLabel 	info;
	private ValueLabel 	owner;
	
	public CompanyViewComponent( CompaniesMgmtModel model ) {

		super( 2, 11 );
		this.model = model;
		
		initView();
		
		model.addChangedListener(( SelectionChangedListener ) this );
		model.addChangedListener(( OrganisationChangedListener ) this );        
	}
	
	private void initView() {

		setSpacing( true );
		setMargin( true );
		setSizeFull();
		setColumnExpandRatio( 1, 1 );		
		
	    companyCode = new ValueLabel();
	    name = new ValueLabel();
	    tunnus = new ValueLabel();
	    address = new ValueLabel();
	    phone = new ValueLabel();
	    companyEmail = new ValueLabel();
		info = new ValueLabel();
		owner = new ValueLabel();
				
		Label header = new Label( model.getApp().getResourceStr( "general.edit.company" ));
		header.addStyleName( Runo.LABEL_H2 );		
		addComponent( header, 0, 0 );

		addComponent( new Label( "<hr/>", Label.CONTENT_XHTML ), 									0, 	1, 1, 1 );
		addComponent( new Label( model.getApp().getResourceStr( "general.edit.code" ) + ":" ),		0,	2 );
		addComponent( new Label( model.getApp().getResourceStr( "general.edit.name" ) + ":" ),		0,	3 );
		addComponent( new Label( model.getApp().getResourceStr( "company.edit.tunnus" ) + ":" ),	0,	4 );
		addComponent( new Label( model.getApp().getResourceStr( "company.edit.address" ) + ":" ),	0,	5 );
		addComponent( new Label( model.getApp().getResourceStr( "company.edit.phone" ) + ":" ),		0,	6 );
		addComponent( new Label( model.getApp().getResourceStr( "company.edit.email" ) + ":" ),		0,	7 );
		addComponent( new Label( model.getApp().getResourceStr( "company.edit.info" ) + ":" ),		0,	8 );
		addComponent( new Label( "<hr/>", Label.CONTENT_XHTML ), 									0, 	9, 1, 9 );
		addComponent( new Label( model.getApp().getResourceStr( "company.edit.owner" ) + ":" ),		0, 10 );
	
		getComponent( 0, 0 ).setWidth( "10em" );
		
		addComponent( companyCode, 	1, 	2 );
		addComponent( name,			1, 	3 );
		addComponent( tunnus,		1, 	4 );
		addComponent( address,		1, 	5 );
		addComponent( phone,		1, 	6 );
		addComponent( companyEmail,	1, 	7 );
		addComponent( info,			1, 	8 );
		addComponent( owner,		1, 10 );
	
		setVisible( false );
	
	}

	@Override
	public void selectionChanged() {
		if ( logger.isDebugEnabled()) logger.debug( "Selection was changed in CompaniesList" );
		showOrganisation( model.getSelectedOrganisation() );
	}
	
	@Override
	public void wasChanged( Organisation organisation ) {
		if ( logger.isDebugEnabled()) logger.debug( "Organisation was changed: " + organisation );
		showOrganisation( organisation );
	}
	
	public void showOrganisation( Organisation organisation ) {

		if ( organisation != null ) {
			setVisible( true );

		    companyCode.setValue( organisation.getCode());
		    name.setValue( organisation.getName());
		    tunnus.setValue( organisation.getTunnus());
		    address.setValue( organisation.getAddress());
		    phone.setValue( organisation.getPhone());
		    companyEmail.setValue( organisation.getEmail());
			info.setValue( organisation.getInfo());
			owner.setValue( organisation.getServiceOwner() != null ? organisation.getServiceOwner().getFirstAndLastNames() : "" );

		} else {
			setVisible( false );

		    companyCode.setValue( "" );
		    name.setValue( "" );
		    tunnus.setValue( "" );
		    address.setValue( "" );
		    phone.setValue( "" );
		    companyEmail.setValue( "" );
			info.setValue( "" );
			owner.setValue( "" );
			
		}

	}
	
}
