package com.c2point.tms.web.ui.reportsmgmt.travelreports.model;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.TravelReport;
import com.c2point.tms.util.DateUtil;
import com.c2point.tms.web.application.TmsApplication;
import com.c2point.tms.web.ui.AbstractModel;
import com.c2point.tms.web.ui.DateModel;
import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;

@SuppressWarnings("serial")
public class ReportsManagementModel extends AbstractModel 
										implements Property.ValueChangeListener {
	
	private static Logger logger = LogManager.getLogger( ReportsManagementModel.class.getName());

	private DateModel			weekModel;
	private ProjectTreeModel	projectsModel;
	private TravelReportsModel	travelReportsModel;

	
	public ReportsManagementModel( TmsApplication app ) {
		super( app );
		
		weekModel = new DateModel( DateUtil.getDate());
		projectsModel = new ProjectTreeModel( this );
		travelReportsModel = new TravelReportsModel( this );
		
//		initModel();

	}
	
	public void initModel() {

		projectsModel.initModel();
		
		travelReportsModel.initData();
		
	}
	public void reInitModel() {

		projectsModel.reInitModel();
		
		travelReportsModel.reInitData();

	}

	
	
	public Container getProjectModel() {
		return projectsModel;
	}
	
	public DateModel getDateModel() {
		return this.weekModel;
	}
	
	public TravelReportsModel getTravelReportsModel() {
		return this.travelReportsModel;
	}
	
	public String getItemId( TravelReport report ) {
		if ( report != null ) {
			return report.getUniqueReportId();
		}
		logger.error( " TravelReport cannot be null here!" );
		
		return null;
	}

	@Override
	public void valueChange(ValueChangeEvent event) {
		if ( logger.isDebugEnabled()) logger.debug( "Model received ValueChangeEvent event" );
		if ( event.getProperty().getType() == Date.class ) {
			if ( logger.isDebugEnabled()) logger.debug( "   Date received!" );
			if ( logger.isDebugEnabled()) logger.debug( "   " + weekModel );
			weekModel.setDate(( Date )event.getProperty().getValue());
			reInitModel();
//				fireWeekChanged();
		}
	}

}
