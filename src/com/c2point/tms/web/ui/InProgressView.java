package com.c2point.tms.web.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.web.application.TmsApplication;
import com.c2point.tms.web.ui.projectsview.ProjectView;
import com.vaadin.shared.ui.AlignmentInfo.Bits;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;

public class InProgressView extends AbstractMainView {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( ProjectView.class.getName());
	
	private static InProgressView instance = null;
	
	public static InProgressView getInstance( TmsApplication app ) {
		if ( instance == null )
			instance = new InProgressView( app );
		
		return instance;
	}

	public static InProgressView getInstance() {
		return 	getInstance( null );
	}
	
	
	public InProgressView( TmsApplication app ) {
		super( app );
        
	}

	@Override
	protected void initUI() {
		this.setSizeFull();
		this.setSpacing( true );

		Label content = new Label( "" );
		content.setContentMode( ContentMode.HTML );
		content.setValue( this.getTmsApplication().getResourceStr( "inprogress.title" ));
		
		this.addComponent( content );
		this.setComponentAlignment( content, new Alignment( Bits.ALIGNMENT_VERTICAL_CENTER | Bits.ALIGNMENT_HORIZONTAL_CENTER));
	}

	@Override
	protected void initDataAtStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initDataReturn() {
		// TODO Auto-generated method stub
		
	}

}
