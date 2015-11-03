package com.c2point.tms.web.ui.listeners;

import java.util.EventListener;

import com.c2point.tms.entity.AbstractReport;

public interface ReportChangedListener extends EventListener {

	public void wasChanged( AbstractReport report );

}
