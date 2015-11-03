package com.c2point.tms.web.ui.listeners;

import java.util.EventListener;

import com.c2point.tms.entity.AbstractReport;

public interface ReportDeletedListener  extends EventListener {

	public void wasDeleted( AbstractReport report );

}
