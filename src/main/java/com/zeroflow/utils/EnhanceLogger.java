package com.zeroflow.utils;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.Map;
import java.util.function.Supplier;

/**
 * 方便使用的logger
 */
public class EnhanceLogger {

	private Logger logger;

	public static EnhanceLogger of(Logger logger){
		return new EnhanceLogger(logger);
	}
	private EnhanceLogger(Logger logger) {
		this.logger = logger;
	}

	public void debug(LogEvent logEvent) {
		if(this.isDebugEnabled()) {
			String format = this.generateLogFormat(logEvent, "Debug");
			if(logEvent.getArgs() != null) {
				this.logger.debug(format, logEvent.getArgs());
			} else if(logEvent.getThrowable() != null) {
				this.logger.debug(format, logEvent.getThrowable());
			} else {
				this.logger.debug(format);
			}
		}
	}
	public void error(LogEvent logEvent) {
		String format = this.generateLogFormat(logEvent, "Error");
		if(logEvent.getArgs() != null) {
			this.logger.error(format, logEvent.getArgs());
		} else if(logEvent.getThrowable() != null) {
			this.logger.error(format, logEvent.getThrowable());
		} else {
			this.logger.error(format);
		}
	}


	public void info(LogEvent logEvent) {
		if(this.isInfoEnabled()) {
			String message = this.generateLogFormat(logEvent, "Info");
			if(logEvent.getArgs() != null) {
				this.logger.info(message, logEvent.getArgs());
			} else if(logEvent.getThrowable() != null) {
				this.logger.info(message, logEvent.getThrowable());
			} else {
				this.logger.info(message);
			}
		}
	}

	public void infoDelay(Supplier<LogEvent> logEventSupplier) {
		if(this.isInfoEnabled()) {
			LogEvent logEvent = logEventSupplier.get();
			String message = this.generateLogFormat(logEvent, "Info");
			if(logEvent.getArgs() != null) {
				this.logger.info(message, logEvent.getArgs());
			} else if(logEvent.getThrowable() != null) {
				this.logger.info(message, logEvent.getThrowable());
			} else {
				this.logger.info(message);
			}
		}
	}

	public boolean isDebugEnabled() {
		return this.logger.isDebugEnabled();
	}
	public boolean isInfoEnabled() {
		return this.logger.isInfoEnabled();
	}
	private String generateLogFormat(LogEvent logEvent, String logType) {
		String eventName = logEvent.getEventName();
		String message = logEvent.getMessage();
		Object json = logEvent.getJson();
		Map<String, Object> otherInfo = logEvent.getOtherInfo();
		if(StringUtils.isEmpty(logEvent.getEventName())) {
			eventName = this.logger.getName() + ":unclassified" + logType;
		}

		StringBuilder generated = new StringBuilder();
		generated.append("[").append(eventName).append("]");
		if(json != null) {
			generated.append(" json=").append(JSON.toJSON(json));
		}

		generated.append(" msg=").append(StringUtils.isEmpty(message)?"":message);

		if(otherInfo != null) {
			generated.append(" other=").append(JSON.toJSON(otherInfo)).append(" ");
		}
		return generated.toString();
	}
}
