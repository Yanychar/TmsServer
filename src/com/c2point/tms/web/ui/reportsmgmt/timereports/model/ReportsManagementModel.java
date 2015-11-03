package com.c2point.tms.web.ui.reportsmgmt.timereports.model;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.ProjectTask;
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

	private DateModel			          weekModel;
	private ProjectsTasksTreeModel	projectsModel;
	private TimeReportsModel	      timeReportsModel;


	public ReportsManagementModel( TmsApplication app ) {
		super( app );

		weekModel = new DateModel( DateUtil.getDate());
		projectsModel = new ProjectsTasksTreeModel( this );
		timeReportsModel = new TimeReportsModel( this );

//		initModel();

	}

	public void initModel() {

		projectsModel.initModel();

		timeReportsModel.initData();

	}

	public void reInitModel() {

		projectsModel.reInitModel();

		timeReportsModel.reInitData();
	}




	public Container getProjectModel() {


		return projectsModel;
	}

	public DateModel getDateModel() {
		return this.weekModel;
	}

	public TimeReportsModel getTimeReportsModel() {
		return this.timeReportsModel;
	}

	public WeekProjectTaskId getItemId( WeekItem item ) {
		if ( item != null ) {
			ProjectTask projectTask = item.getProjectTask();
			if ( projectTask != null ) {
				return new WeekProjectTaskId( projectTask );
			} else {
				logger.error( " ProjectTask cannot be null!" );
			}
		} else {
			logger.error( " WeekItem cannot be null!" );
		}

		return null;
	}

	@Override
	public void valueChange(ValueChangeEvent event) {
		if ( logger.isDebugEnabled()) logger.debug( "Model received ValueChangeEvent event" );
		if ( event.getProperty().getType() == Date.class ) {
			if ( logger.isDebugEnabled()) logger.debug( "   Date received!" );
			if ( logger.isDebugEnabled()) logger.debug( "   " + weekModel );
			int oldWeek = weekModel.getWeek();
			weekModel.setDate(( Date )event.getProperty().getValue());
			if ( logger.isDebugEnabled()) logger.debug( "   " + weekModel );
			if ( oldWeek != weekModel.getWeek()) {
				if ( logger.isDebugEnabled()) logger.debug( "Old week = " + oldWeek + ",  newWeek = " + weekModel.getWeek());
				reInitModel();
//				fireWeekChanged();
			}
		}
	}

}
