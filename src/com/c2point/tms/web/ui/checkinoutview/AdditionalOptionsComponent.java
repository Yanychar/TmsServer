package com.c2point.tms.web.ui.checkinoutview;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.web.ui.PeriodSelectionComponent;
import com.c2point.tms.web.ui.checkinoutview.model.CheckInOutModel;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

@SuppressWarnings("serial")
public class AdditionalOptionsComponent extends PeriodSelectionComponent {
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( AdditionalOptionsComponent.class.getName());

//	private CheckInOutModel model;

	public AdditionalOptionsComponent( CheckInOutModel model ) {
		super( model.getApp(), model );
//		this.model = model;

		initView();
	}

	private void initView() {
		
		setCaption( null );
		this.addStyleName( Runo.PANEL_LIGHT );
		
        AbstractOrderedLayout superL = (( AbstractOrderedLayout )this.getContent());
        VerticalLayout vl = new VerticalLayout();
        
        vl.addComponent( superL );
        
        this.setContent( vl );

	}
	
	protected void startDateChanged( ValueChangeEvent event ) {
		super.startDateChanged( event );
//		model.clearReportLists();

//		model.fullReadReports();
	}
	
	protected void endDateChanged( ValueChangeEvent event ) {
		super.endDateChanged( event );
//		model.clearReportLists();

//		model.fullReadReports();
	}
	
}
