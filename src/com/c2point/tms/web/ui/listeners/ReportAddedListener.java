package com.c2point.tms.web.ui.listeners;

import java.util.EventListener;

import com.c2point.tms.entity.AbstractReport;

public interface ReportAddedListener  extends EventListener {

	public void wasAdded( AbstractReport report );

}
