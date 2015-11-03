package com.c2point.tms.web.ui.approveview;

import java.util.List;

import com.c2point.tms.entity.AbstractReport;
import com.c2point.tms.entity.CheckInOutRecord;
import com.c2point.tms.entity.TravelReport;
import com.c2point.tms.web.application.TmsApplication;

public interface ModifyTravelIf {

	public TravelReport updateTravelReport( TravelReport report );
	public List<CheckInOutRecord> getCheckInOutList( AbstractReport report );
	public TmsApplication getApp();
}
