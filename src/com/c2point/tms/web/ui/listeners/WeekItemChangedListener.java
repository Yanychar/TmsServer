package com.c2point.tms.web.ui.listeners;

import java.util.EventListener;

import com.c2point.tms.web.ui.reportsmgmt.timereports.model.WeekItem;

public interface WeekItemChangedListener extends EventListener {
	
	public void wasChanged( WeekItem item );

}
