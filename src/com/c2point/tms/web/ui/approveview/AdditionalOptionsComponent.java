package com.c2point.tms.web.ui.approveview;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.web.ui.PeriodSelectionComponent;
import com.c2point.tms.web.ui.approveview.model.ApproveModel;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

@SuppressWarnings("serial")
public class AdditionalOptionsComponent extends PeriodSelectionComponent {
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( AdditionalOptionsComponent.class.getName());

	private ApproveModel model;

	private CheckBox cbToCheck;
	private CheckBox cbRejected;
	private CheckBox cbApproved;
	private CheckBox cbProcessed;
	
	public AdditionalOptionsComponent( ApproveModel model, ClickListener printClickListener ) {
		super( model.getApp(), model, printClickListener );
		
		this.model = model;

		initView();
	}

	private void initView() {
		
		setCaption( null );
		this.addStyleName( Runo.PANEL_LIGHT );
		

		cbToCheck = new CheckBox();
		cbRejected = new CheckBox();
		cbApproved = new CheckBox();
		cbProcessed = new CheckBox();
		
		
		cbToCheck.setCaption( app.getResourceStr( "approve.options.tocheck" ));
		cbRejected.setCaption( app.getResourceStr( "approve.options.rejected" ));
		cbApproved.setCaption( app.getResourceStr( "approve.options.approved" ));
		cbProcessed.setCaption( app.getResourceStr( "approve.options.processed" ));
		
		cbToCheck.setImmediate( true );
		cbRejected.setImmediate( true );
		cbApproved.setImmediate( true );
		cbProcessed.setImmediate( true );
		
		GridLayout optionsL = new GridLayout( 2, 2 );
		
		optionsL.setMargin( new MarginInfo( false, false, false, false ));
		optionsL.setSpacing( true );
		
		
		
		optionsL.addComponent( cbToCheck, 	0, 0 );
		optionsL.addComponent( cbRejected, 	0, 1 );
		optionsL.addComponent( cbApproved,	1, 0 );
		optionsL.addComponent( cbProcessed, 	1, 1 );

        
        
        AbstractOrderedLayout superL = (( AbstractOrderedLayout )this.getContent());
        VerticalLayout vl = new VerticalLayout();
        
        vl.addComponent( superL );
        vl.addComponent( optionsL );
        
        this.setContent( vl );

        dateFromModel();

		cbToCheck.addValueChangeListener( new ValueChangeListener() {
			@Override
			public void valueChange( com.vaadin.data.Property.ValueChangeEvent event) {
				model.getFilter().setToCheck(( Boolean )event.getProperty().getValue()); 

				model.resetReportList();
		    }
		});
		
		cbRejected.addValueChangeListener( new ValueChangeListener() {
			@Override
			public void valueChange( com.vaadin.data.Property.ValueChangeEvent event) {
				model.getFilter().setRejected(( Boolean )event.getProperty().getValue()); 

				model.resetReportList();
		    }
		});
		
		cbApproved.addValueChangeListener( new ValueChangeListener() {
			@Override
			public void valueChange( com.vaadin.data.Property.ValueChangeEvent event) {
				model.getFilter().setApproved(( Boolean )event.getProperty().getValue()); 

				model.resetReportList();
		    }
		});
		
		cbProcessed.addValueChangeListener( new ValueChangeListener() {
			@Override
			public void valueChange( com.vaadin.data.Property.ValueChangeEvent event) {
				model.getFilter().setProcessed(( Boolean )event.getProperty().getValue()); 

				model.resetReportList();
		    }
		});
		
	}
	
	private void dateFromModel(){
		cbToCheck.setValue( model.getFilter().isToCheck() );
		cbRejected.setValue( model.getFilter().isRejected() );
		cbApproved.setValue( model.getFilter().isApproved() );
		cbProcessed.setValue( model.getFilter().isProcessed() );
	}

	protected void startDateChanged( ValueChangeEvent event ) {
		super.startDateChanged( event );
//		model.clearReportLists();

		model.fullReadReports();
	}
	
	protected void endDateChanged( ValueChangeEvent event ) {
		super.endDateChanged( event );
//		model.clearReportLists();

		model.fullReadReports();
	}
	
}
