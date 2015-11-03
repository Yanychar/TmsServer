package com.c2point.tms.web.ui.listeners;

import java.util.EventListener;

import com.c2point.tms.entity.TmsUser;

public interface UserAddedListener extends EventListener {

	public void wasAdded( TmsUser user );

}
