package com.c2point.tms.web.ui.listeners;

import java.util.EventListener;

import com.c2point.tms.web.ui.approveview.model.ProjectHolder;

public interface ProjectReportChangedListener extends EventListener {

	public void wasChanged( ProjectHolder holder );

}
