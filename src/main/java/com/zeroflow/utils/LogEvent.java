package com.zeroflow.utils;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 日志事件包装类
 */
public class LogEvent {

	private static String logTestFlag;
	static {
		logTestFlag = System.getenv("logTestFlag");
	}

	@Getter
	private String eventName;
	@Getter
	private String message;
	@Getter
	private Map<String, Object> json;
	@Getter
	private Object[] args;
	@Getter
	private Throwable throwable;
	@Getter
	private Map<String, Object> otherInfo;

	private LogEvent(String eventName) {
		this.eventName = eventName;
	}
	private LogEvent(String eventName, String message, Object[] args) {
		this.eventName = eventName;
		this.message = message;
		this.args = args;
	}
	private LogEvent(String eventName, String message, Throwable throwable) {
		this.eventName = eventName;
		this.message = message;
		this.throwable = throwable;
	}

	//用于构造只有eventName的日志
	public static LogEvent of(String eventName) {
		return new LogEvent(eventName);
	}
	//用于构造有eventName， msg字段， msg字段包含有placeholder字段的日志
	public static LogEvent of(String eventName, String message, Object... args) {
		return new LogEvent(eventName, message, args);
	}
	//用于构造有eventName， msg字段， 需要打印错误推荐的日志
	public static LogEvent of(String eventName, String message, Throwable throwable) {
		return new LogEvent(eventName, message, throwable);
	}

	//设置LogEvent的json字段，值可以为常用的类型，返回LogEvent类型， 方便用于链式构造json字段
	public LogEvent analyze(String field, Object value) {
		if(this.json == null) {
			this.json = new HashMap<>();
		}
		this.json.put(field, value);
		return this;
	}

	//设置LogEvent的json字段，值可以为常用的类型，返回LogEvent类型， 方便用于链式构造json字段
	public LogEvent others(String field, Object value) {
		if(this.otherInfo == null) {
			this.otherInfo = new HashMap<>();
		}
		this.otherInfo.put(field, value);
		return this;
	}

	// 仅测试时, 才记录日志
	public LogEvent logWhenTest(String field, Object value) {
		if ("true".equals(logTestFlag)) {
			if(this.otherInfo == null) {
				this.otherInfo = new HashMap<>();
			}
			this.otherInfo.put(field, value);
		}
		return this;
	}
}
