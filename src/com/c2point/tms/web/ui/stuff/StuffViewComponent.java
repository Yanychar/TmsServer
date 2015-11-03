package com.c2point.tms.web.ui.stuff;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.TmsUser;
import com.c2point.tms.web.ui.ValueLabel;
import com.c2point.tms.web.ui.listeners.SelectionChangedListener;
import com.c2point.tms.web.ui.listeners.UserChangedListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;

@SuppressWarnings("serial")
public class StuffViewComponent extends GridLayout implements SelectionChangedListener, UserChangedListener {

	private static Logger logger = LogManager.getLogger( StuffViewComponent.class.getName());

	private static ThemeResource managerIcon;

	private StuffMgmtModel	model;

	private ValueLabel	code;
	private ValueLabel	fName;
	private ValueLabel	mName;
	private ValueLabel	lName;

	private ValueLabel	address;
	private ValueLabel	kelaCode;
	private ValueLabel	taxCode;
	private ValueLabel	email;
	private ValueLabel	mobile;




	private ValueLabel	lineManager;

	private Label		isLineManager;
	private Label		isProjectManager;

	private ValueLabel	secGroup;


	public StuffViewComponent( StuffMgmtModel model ) {
		super( 2, 17);
		this.model = model;

		initView();

		model.addChangedListener(( SelectionChangedListener ) this );
		model.addChangedListener(( UserChangedListener ) this );

	}

	private void initView() {

		setSpacing( true );
		this.setMargin( true );
		setSizeFull();
		setColumnExpandRatio( 1, 1 );

		code = new ValueLabel();
		fName = new ValueLabel();
		mName = new ValueLabel();
		lName = new ValueLabel();

		address = new ValueLabel();
		kelaCode = new ValueLabel();
		taxCode = new ValueLabel();
		email = new ValueLabel();
		mobile = new ValueLabel();

		lineManager = new ValueLabel();
		isLineManager = new Label();
		isProjectManager = new Label();
		secGroup = new ValueLabel();

		addComponent( new Label( model.getApp().getResourceStr( "general.edit.code" ) + ":" ), 0, 0 );
		addComponent( new Label( model.getApp().getResourceStr( "general.edit.fname" )), 0, 1 );
		addComponent( new Label( model.getApp().getResourceStr( "general.edit.mname" )), 0, 2 );
		addComponent( new Label( model.getApp().getResourceStr( "general.edit.lname" )), 0, 3 );

		addComponent( new Label( "<hr/>", Label.CONTENT_XHTML ), 0, 4, 1, 4);
		addComponent( new Label( "Address:" ), 0, 5 );
		addComponent( new Label( "Social Security Code:" ), 0, 6 );
		addComponent( new Label( "Tax Number:" ), 0, 7 );
		addComponent( new Label( "Email:" ), 0, 8 );
		addComponent( new Label( "Mobile:" ), 0, 9 );

		addComponent( new Label( "<hr/>", Label.CONTENT_XHTML ), 0, 10, 1, 10);
		addComponent( new Label( model.getApp().getResourceStr( "general.edit.mngr" ) + ":" ), 0, 11);
		addComponent( new Label( "<hr/>", Label.CONTENT_XHTML ), 0, 12, 1, 12);
		addComponent( new Label( model.getApp().getResourceStr( "general.edit.linemgmt" ) + ":" ), 0, 13);
		addComponent( new Label( model.getApp().getResourceStr( "general.edit.owner" ) + ":" ), 0, 14);
		addComponent( new Label( "<hr/>", Label.CONTENT_XHTML ), 0, 15, 1, 15);
		addComponent( new Label( model.getApp().getResourceStr( "general.edit.group" ) + ":" ), 0, 16 );


		getComponent( 0, 0 ).setWidth( "10em" );

		addComponent( code, 			1, 0 );
		addComponent( fName, 			1, 1 );
		addComponent( mName, 			1, 2 );
		addComponent( lName, 			1, 3 );
		addComponent( address, 			1, 5 );
		addComponent( kelaCode, 		1, 6 );
		addComponent( taxCode, 			1, 7 );
		addComponent( email, 			1, 8 );
		addComponent( mobile, 			1, 9 );
		addComponent( lineManager, 		1, 11 );
		addComponent( isLineManager,	1, 13 );
		addComponent( isProjectManager, 1, 14 );
		addComponent( secGroup, 		1, 16 );

		managerIcon = new ThemeResource( "icons/24/selected24.png");

		setVisible( false );

	}

	public void selectionChanged() {
		if ( logger.isDebugEnabled()) logger.debug( "Selection was changed in UserList" );
		showUser( model.getSelectedUser() );
	}

	public void wasChanged( TmsUser user ) {
		if ( logger.isDebugEnabled()) logger.debug( "TmsUser was changed: " + user );
		showUser( user );
	}

	public void showUser( TmsUser user ) {

		if ( user != null ) {
			setVisible( true );

			code.setValue( user.getCode());
			fName.setValue( user.getFirstName());
			mName.setValue( user.getMidName());
			lName.setValue( user.getLastName());

			address.setValue( user.getAddress());
			kelaCode.setValue( user.getKelaCode());
			taxCode.setValue( user.getTaxNumber());
			email.setValue( user.getEmail());
			mobile.setValue( user.getMobile());

			lineManager.setValue( user.getManager() != null ? user.getManager().getFirstAndLastNames() : "" );
			isLineManager.setIcon( user.isLineManager() ? managerIcon : null ); //notManagerIcon );
			isProjectManager.setIcon( user.isProjectManager() ? managerIcon : null ); //notManagerIcon );
			secGroup.setValue( user.getContext().getSecGroup() != null ? user.getContext().getSecGroup().getDefName() : "" );

		} else {
			setVisible( false );
			code.setValue( "" );
			fName.setValue( "" );
			mName.setValue( "" );
			lName.setValue( "" );

			address.setValue( "" );
			kelaCode.setValue( "" );
			taxCode.setValue( "" );
			email.setValue( "" );
			mobile.setValue( "" );

			lineManager.setValue( "" );
			isLineManager.setIcon( null );
			isProjectManager.setIcon( null );
			secGroup.setValue( "" );
		}

	}

}
