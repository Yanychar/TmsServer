package com.c2point.tms.web.ui.listeners;

import java.util.EventListener;

import com.c2point.tms.entity.access.SecurityGroup;

public interface SGSelectionChangedListener extends EventListener {

	public void selectionChanged( SecurityGroup group );

}
