/**
 * file name : ScriptParameter.java
 * created at : 8:19:53 PM Jul 31, 2015
 * created by 970655147
 */

package com.hx.crawler.crawler.interf;

import com.hx.log.util.Tools;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * 脚本的参数
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 5/10/2017 7:35 PM
 */
public abstract class ScriptParameter<ResponseType, HeaderValType, DataValType, CookieValType> {

    /**
     * 任务所属组的id
     */
    protected int taskGroupId;
    /**
     * 任务的id
     */
    protected int taskId;
    /**
     * 当前任务访问的url
     */
    protected String url;
    /**
     * 当前任务的参数
     */
    protected Map<String, Object> param;
    /**
     * 当前任务对应的爬虫实例
     */
    protected Crawler<ResponseType, HeaderValType, DataValType, CookieValType> crawler;

    /**
     * setter & getter
     */
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

    public void setCrawler(Crawler<ResponseType, HeaderValType, DataValType, CookieValType> crawler) {
        this.crawler = crawler;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, Object> getParam() {
        return param;
    }

    public Crawler<ResponseType, HeaderValType, DataValType, CookieValType> getCrawler() {
        return crawler;
    }

    /**
     * 获取给定的key对应的值
     *
     * @return java.lang.Object
     * @author Jerry.X.He
     * @date 5/10/2017 7:35 PM
     * @since 1.0
     */
    public Object getParam(String key) {
        return param.get(key);
    }

    /**
     * 获取给定的key对应的值的字符串表示
     *
     * @return java.lang.Object
     * @author Jerry.X.He
     * @date 5/10/2017 7:35 PM
     * @since 1.0
     */
    public String getStrParam(String key) {
        return String.valueOf(getParam(key));
    }

    /**
     * 获取所有的参数key的集合        -- add at 2016.06.09
     *
     * @return java.lang.Object
     * @author Jerry.X.He
     * @date 5/10/2017 7:35 PM
     * @since 1.0
     */
    public Set<String> paramNames() {
        return new LinkedHashSet<>(param.keySet());
    }

    /**
     * 添加一个参数kv对            -- add at 2016.08.13
     *
     * @return java.lang.Object
     * @author Jerry.X.He
     * @date 5/10/2017 7:35 PM
     * @since 1.0
     */
    public ScriptParameter<ResponseType, HeaderValType, DataValType, CookieValType> addParam(String key, Object value) {
        this.param.put(key, value);
        return this;
    }

    /**
     * 添加多个参数kv对            -- add at 2016.08.13
     *
     * @return java.lang.Object
     * @author Jerry.X.He
     * @date 5/10/2017 7:35 PM
     * @since 1.0
     */
    public ScriptParameter<ResponseType, HeaderValType, DataValType, CookieValType> addParam(Map<String, Object> incParam) {
        if (Tools.isEmpty(incParam)) {
            return this;
        }

        this.param.putAll(incParam);
        return this;
    }


}
