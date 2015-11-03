package com.c2point.tms.web.ui.listeners;

import java.util.EventListener;

import com.c2point.tms.entity.TmsUser;

public interface UserChangedListener extends EventListener {

	public void wasChanged( TmsUser user );


}
