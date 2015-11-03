package com.c2point.tms.web.ui.approveview;

import java.util.List;

import com.c2point.tms.entity.AbstractReport;
import com.c2point.tms.entity.CheckInOutRecord;
import com.c2point.tms.entity.TaskReport;
import com.c2point.tms.web.application.TmsApplication;

public interface ModifyTaskIf {

	public TaskReport updateTaskReport( TaskReport newReport );
	public List<CheckInOutRecord> getCheckInOutList( AbstractReport report );
	public TmsApplication getApp();
}
