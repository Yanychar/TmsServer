package com.c2point.tms.web.ui.listeners;

import java.util.EventListener;

import com.c2point.tms.entity.Project;

public interface ProjectAddedListener extends EventListener {

	public void wasAdded( Project project );

}
