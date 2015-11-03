package com.c2point.tms.web.ui.listeners;

import java.util.EventListener;

import com.c2point.tms.web.ui.reportsmgmt.timereports.model.WeekItem;

public interface WeekItemAddedListener  extends EventListener {

	public void wasAdded( WeekItem item );

}
