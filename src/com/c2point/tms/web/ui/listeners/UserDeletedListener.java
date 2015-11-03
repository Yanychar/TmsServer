package com.c2point.tms.web.ui.listeners;

import java.util.EventListener;

import com.c2point.tms.entity.TmsUser;

public interface UserDeletedListener extends EventListener {

	public void wasDeleted( TmsUser user );

}
