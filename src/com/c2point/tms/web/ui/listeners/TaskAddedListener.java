package com.c2point.tms.web.ui.listeners;

import java.util.EventListener;

import com.c2point.tms.entity.Task;

public interface TaskAddedListener  extends EventListener {

	public void wasAdded( Task task );

}
