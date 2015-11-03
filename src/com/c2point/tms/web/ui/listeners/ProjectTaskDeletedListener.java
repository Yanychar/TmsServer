package com.c2point.tms.web.ui.listeners;

import java.util.EventListener;

import com.c2point.tms.entity.ProjectTask;

public interface ProjectTaskDeletedListener extends EventListener {

	public void wasDeleted( ProjectTask pTask );



}
