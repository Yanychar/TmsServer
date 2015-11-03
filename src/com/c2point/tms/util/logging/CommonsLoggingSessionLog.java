package com.c2point.tms.util.logging;

import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommonsLoggingSessionLog extends AbstractSessionLog implements SessionLog {
	private static Logger logger = LogManager.getLogger( CommonsLoggingSessionLog.class.getName());

	@Override
	public void log(SessionLogEntry entry) {
		int level = entry.getLevel();
		String message = entry.getMessage();
		if (entry.getParameters() != null) {
			message += " [";
			int index = 0;
			for (Object object : entry.getParameters()) {
				message += (index++ > 0 ? "," : "") + object;
			}
			message += "]";
		}
		switch (level) {
			case SessionLog.SEVERE:
				logger.error(message);
				break;
			case SessionLog.WARNING:
				logger.warn(message);
				break;
			case SessionLog.INFO:
				logger.info(message);
				break;
			case SessionLog.CONFIG:
				logger.trace(message);
				break;
			default:
				logger.debug(message);
				break;
		}
	}
}