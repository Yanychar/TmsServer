package com.c2point.tms.entity.stubs.taskreport;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.c2point.tms.entity.ApprovalFlagType;
import com.c2point.tms.entity.ProjectTask;
import com.c2point.tms.entity.TaskReport;

@XmlRootElement(name = "task")
@XmlType(propOrder = { "uniqueReportId", "code", "name", "hours", "comment", "approvalFlagType" })
public class TaskReportStub {

    // UniqueID of TaskReport (OPTIONAL). Used:
	//   - Save modified TaskReport
	//   - Get the List of existing TaskReports
	private String 	uniqueReportId;
    
    // Code of ProjectTask (OPTIONAL). Used:
	//   - Save NEW TaskReport
	//   - GetReports
    private String	code;
    // Name of ProjectTask/Task assigned to the project (OPTIONAL). Used:
	//   - Save NEW TaskReport
	//   - GetReports
	private String	name;
	
    // Hours spent on ProjectTask (MANDATORY).
	private Float	hours;
    // Comment to TaskReport (MANDATORY).
	private String	comment;	
	
	private ApprovalFlagType	approvalFlagType;
	
	public TaskReportStub() {
	}
	
	public TaskReportStub( TaskReport report ) {
		this.uniqueReportId = report.getUniqueReportId();
		this.code = report.getProjectTask().getTask().getCode();
		this.name = report.getProjectTask().getTask().getName();
//		this.prj_code = report.getProjectTask().getProject().getCode();
		this.hours = report.getHours();
		this.comment = report.getComment();
		this.approvalFlagType = report.getApprovalFlagType();
	}

	public TaskReportStub( ProjectTask  prTask ) {
		this.code = prTask.getTask().getCode();
		this.name = prTask.getTask().getName();
	}

	
	@XmlElement( name = "reportId" )
	public String getUniqueReportId() {
		return uniqueReportId;
	}

	public void setUniqueReportId( String uniqueReportId ) {
		this.uniqueReportId = uniqueReportId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
/*	
	@XmlElement(name = "project")
	public String getPrj_code() {
		return prj_code;
	}

	public void setPrj_code(String prj_code) {
		this.prj_code = prj_code;
	}
*/
	public Float getHours() {
		return hours;
	}

	public void setHours( float hours ) {
		this.hours = hours;
	}

	public void setHours( Float hours ) {
		this.hours = hours;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public ApprovalFlagType getApprovalFlagType() {
		return approvalFlagType;
	}

	public void setApprovalFlagType( ApprovalFlagType approvalFlagType ) {
		this.approvalFlagType = approvalFlagType;
	}

	@Override
	public String toString() {
		return "TaskReportStub [" + "uniqueReportId=" + (uniqueReportId != null ? uniqueReportId : "null" ) + ", "
				+ "Ref. to ProjTask [code="+ ( code != null ? code : "null") + ", " 
								  + "name=" +(name != null ? name : "null") + "], " 
				+ "hours=" + hours + ", "
				+ "comment=" + (comment != null ? comment : "null" ) + "]";
	}
}
