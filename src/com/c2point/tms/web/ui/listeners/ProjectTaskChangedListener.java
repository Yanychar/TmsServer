package com.c2point.tms.web.ui.listeners;

import java.util.EventListener;

import com.c2point.tms.entity.ProjectTask;

public interface ProjectTaskChangedListener extends EventListener {

	public void wasChanged( ProjectTask pTask );


}
