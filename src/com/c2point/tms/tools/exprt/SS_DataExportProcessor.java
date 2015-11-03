package com.c2point.tms.tools.exprt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SS_DataExportProcessor extends UHR_DataExportProcessor {

	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( UHR_DataExportProcessor.class.getName());

	@Override
	protected ExportValidator getRecordsWriter() {
		return new SS_RecordsWriter();
	}

}
