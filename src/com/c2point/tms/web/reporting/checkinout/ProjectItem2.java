package com.c2point.tms.web.reporting.checkinout;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.CheckInOutRecord;
import com.c2point.tms.entity.Project;
import com.c2point.tms.entity.TmsUser;

public class ProjectItem2 extends AggregateItem {

	private static Logger logger = LogManager.getLogger( ProjectItem2.class.getName());
	
	private Project project;
	
	public ProjectItem2( AggregateItem parent, Project project ) {
		
		super( parent );
		
		setProject( project );
		
		setHasChilds();
		
	}

	@Override
	protected boolean isValid() {
		
		boolean bRes = false;
		
		if ( getProject() != null &&
			 getProject().getCode() != null
		) {
			bRes = true;
		} else {
			if ( logger.isDebugEnabled()) {
				
				logger.debug( "Project is not valid because:" );
				logger.debug( getProject() == null ? "Project == Null" : "" );
				logger.debug( getProject() != null 
						      && getProject().getCode() == null ? "Project.Code == Null" : "" );
				
			}
			
		}
		
		return bRes;
	}

	public Project getProject() { return project; }
	public void setProject( Project project ) { this.project = project; }

	@Override
	protected String getToCompare() {

		if ( project != null && project.getName() != null ) {
			
			return project.getName(); 
		}
		return null;
	}

	@Override
	protected String getKey() {
		
		return getProject().getCode();

	}

	@Override
	protected String getKey( TmsUser user, CheckInOutRecord record ) {

		return record.getProject().getCode();

	}

	@Override
	protected AggregateItem createChild( TmsUser user, CheckInOutRecord record) {

		return new DateItem( this, record.getDateCheckedIn());
		
	}

}
