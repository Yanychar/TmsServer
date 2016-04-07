package com.c2point.tms.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.stubs.taskreport.TaskReportStub;

@SuppressWarnings("unused")
@Entity
@Table(name="taskreport")
@NamedQueries({
	@NamedQuery(name = "findTaskReportByCode", 
					query = "SELECT report FROM TaskReport report WHERE report.uniqueReportId = :reportId"
	),
	@NamedQuery(name = "findTasksByDate&Person&Project", 
					query = "SELECT report FROM TaskReport report WHERE " +
							"report.date = :date AND " +
							"report.user = :user AND " +
							"report.projectTask.project.code = :projectCode" 
	),
	@NamedQuery(name = "findTasksByPersonAndPeriod",
					query = "SELECT report FROM TaskReport report WHERE " +
							"report.user = :user AND " +
							"report.date BETWEEN :startDate AND :endDate" 
	),
	@NamedQuery(name = "findAllByOrg&Period",
					query = "SELECT report FROM TaskReport report WHERE " +
							"report.org = :org AND " + 
							"report.date BETWEEN :startDate AND :endDate" 
	),
	@NamedQuery(name = "findByManager&Period",
					query = "SELECT report FROM TaskReport report WHERE " +
							"report.projectTask.project.projectManager = :mngr AND " + 
							"report.date BETWEEN :startDate AND :endDate" 
	),
	@NamedQuery(name = "findByManager&Period&ToReport",
					query = "SELECT report FROM TaskReport report WHERE " +
							"report.projectTask.project.projectManager = :mngr AND " + 
							"report.date BETWEEN :startDate AND :endDate AND " +
							"( report.approvalFlagType = com.c2point.tms.entity.ApprovalFlagType.APPROVED OR " +
							"  report.approvalFlagType = com.c2point.tms.entity.ApprovalFlagType.PROCESSED )"
	),
})
public class TaskReport extends AbstractReport {
	private static Logger logger = LogManager.getLogger( TaskReport.class.getName());

	private ProjectTask	projectTask;
	
	private float	hours;

	private float	numValue;
	
	@Column( length=1024 )
	private String	comment;	
	
	/**
	 * 
	 */
	public TaskReport() {
		super();
	}

	public TaskReport modifyReport( TaskReport otherReport ) {
		if ( otherReport.getProjectTask() != null ) {
			this.projectTask = otherReport.getProjectTask();
		}
		this.setApprovalFlagType( otherReport.getApprovalFlagType());
		this.hours = otherReport.getHours();
		this.numValue = otherReport.getNumValue();
		this.comment = otherReport.getComment();
		
		return this;
	}

	public TaskReport initReport( Date date, TmsUser user, ProjectTask projectTask, float hours, float numValue, String comment ) {
		return initReport( null , date, user, projectTask, hours, numValue, comment );
	}
	
	public TaskReport initReport( String uniqueReportId, Date date, TmsUser user, ProjectTask projectTask, float hours, float numValue, String comment ) {
		super.initReport( uniqueReportId, date, user);
		this.projectTask = projectTask;
		this.hours = hours;
		this.numValue = numValue;
		this.comment = comment;

		return this;
	}

	public ProjectTask getProjectTask() {
		return projectTask;
	}

	public void setProjectTask( ProjectTask projectTask ) {
		this.projectTask = projectTask;
	}

	public float getHours() { return hours; }
	public void setHours( float hours ) { this.hours = hours; }

	public float getNumValue() { return numValue; }
	public void setNumValue( float numValue ) { this.numValue = numValue; }

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	@Override
	public String toString() {
		return "TaskReport ["+(uniqueReportId != null ? "uniqueID=" + uniqueReportId + ", " : ", ")
				+ (date != null ? "date=" + date + ", " : ", ")
				+ (user != null ? "user=" + user.getFirstAndLastNames() + ", " : ", ")
				+ (this.getApprovalFlagType() != null ? "flag=" + this.getApprovalFlagType() + ", " : ", ")
				+ (getProject() != null ? "prj.code=" + getProject().getCode() + ", " : ", ")
				+ (getTask() != null ? "task.code=" + getTask() + ", " : ", ")
				+ "hours=" + hours + ", value=" + numValue + ", "
				+ ", " + (comment != null ? "comment=" + comment + ", " : " ")
				+ "id=" + id + "]";
	}

	public Project getProject() {
		if ( projectTask == null ) return null;
		return projectTask.getProject();
	}
	public Task getTask() {
		if ( projectTask == null ) return null;
		return projectTask.getTask();
	}


}
