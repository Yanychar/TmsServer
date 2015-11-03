package com.c2point.tms.web.ui.listeners;

import java.util.EventListener;

import com.c2point.tms.entity.Project;

public interface ProjectDeletedListener extends EventListener {

	public void wasDeleted( Project project );

}
