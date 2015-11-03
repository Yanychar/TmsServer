package com.c2point.tms.web.ui.listeners;

import java.util.EventListener;

import com.c2point.tms.entity.CheckInOutRecord;

public interface CheckInOutSelectionListener  extends EventListener {

	public void selected( CheckInOutRecord record );


}
