package com.c2point.tms.web.ui.listeners;

import java.util.EventListener;

import com.c2point.tms.entity.Organisation;

public interface OrganisationDeletedListener extends EventListener {

	public void wasDeleted( Organisation organisation );

}
