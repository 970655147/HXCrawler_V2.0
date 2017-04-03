/**
 * file name : ScriptParameter.java
 * created at : 8:19:53 PM Jul 31, 2015
 * created by 970655147
 */

package com.hx.crawler.crawler.interf;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

// 脚本的参数
public abstract class ScriptParameter<ResponseType, HeaderType, HeaderValType, DataType, DataValType, CookieValType> {
	
	// taskGroupId, taskId
	// 请求的url, 参数, crawler
	protected int taskGroupId;
	protected int taskId;
	protected String url;
	protected Map<String, Object> param;
	protected Crawler<ResponseType, HeaderType, HeaderValType, DataType, DataValType, CookieValType> crawler;
	
	// setter & getter
	public int getTaskGroupId() {
		return taskGroupId;
	}
	public void setTaskGroupId(int taskGroupId) {
		this.taskGroupId = taskGroupId;
	}
	public int getTaskId() {
		return taskId;
	}
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void setParam(Map<String, Object> param) {
		this.param = param;
	}
	public void setParam(String key, Object value) {
		this.param.put(key, value);
	}
	public void setCrawler(Crawler<ResponseType, HeaderType, HeaderValType, DataType, DataValType, CookieValType> crawler) {
		this.crawler = crawler;
	}
	public String getUrl() {
		return url;
	}
	public Map<String, Object> getParam() {
		return param;
	}
	public Crawler<ResponseType, HeaderType, HeaderValType, DataType, DataValType, CookieValType> getCrawler() {
		return crawler;
	}
	public Object getParam(String key) {
		return param.get(key);
	}
	public String getParamStr(String key) {
		return String.valueOf(getParam(key) );
	}
	// add at 2016.06.09
	public Set<String> paramNames() {
		return new LinkedHashSet<>(param.keySet() );
	}
	// add at 2016.08.13
	public ScriptParameter<ResponseType, HeaderType, HeaderValType, DataType, DataValType, CookieValType> addParam(String key, Object value) {
		this.param.put(key, value);
		return this;
	}
	public ScriptParameter<ResponseType, HeaderType, HeaderValType, DataType, DataValType, CookieValType> addParam(Map<String, Object> incParam) {
		this.param.putAll(incParam);
		return this;
	}
	
	
	
}
