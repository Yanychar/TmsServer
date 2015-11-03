package com.c2point.tms.web.ui.listeners;

import java.util.EventListener;

import com.c2point.tms.entity.ProjectTask;

public interface ProjectTaskAddedListener  extends EventListener {

	public void wasAdded( ProjectTask pTask );

}
