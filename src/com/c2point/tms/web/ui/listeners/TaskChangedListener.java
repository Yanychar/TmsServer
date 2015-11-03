package com.c2point.tms.web.ui.listeners;

import java.util.EventListener;

import com.c2point.tms.entity.Task;

public interface TaskChangedListener extends EventListener {

	public void wasChanged( Task task );


}
