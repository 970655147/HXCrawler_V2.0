package com.hx.crawler.util.recursely_task.interf;

import com.hx.crawler.crawler.interf.CrawlerConfig;
import com.hx.crawler.crawler.interf.HttpMethod;
import com.hx.crawler.crawler.interf.Page;

/**
 * file name : RecurseCrawlTask.java
 * created at : 14:43  2017-03-11
 * created by 970655147
 */
public interface RecurseCrawlTask {

    /**
     * 获取 父节点
     *
     * @return the parentNode of current task
     * @author Jerry.X.He
     * @date 5/11/2017 9:10 PM
     * @since 1.0
     */
    RecurseCrawlTaskFacade getParent();

    /**
     * 获取 url
     *
     * @return the parentNode of current task
     * @author Jerry.X.He
     * @date 5/11/2017 9:10 PM
     * @since 1.0
     */
    String getUrl();

    /**
     * 获取 当前任务的深度
     *
     * @return the parentNode of current task
     * @author Jerry.X.He
     * @date 5/11/2017 9:10 PM
     * @since 1.0
     */
    int getDepth();

    /**
     * 获取 当前任务请求url的方法
     *
     * @return the parentNode of current task
     * @author Jerry.X.He
     * @date 5/11/2017 9:10 PM
     * @since 1.0
     */
    HttpMethod getMethod();

    /**
     * 获取 当前任务请求的配置
     *
     * @return the parentNode of current task
     * @author Jerry.X.He
     * @date 5/11/2017 9:10 PM
     * @since 1.0
     */
    CrawlerConfig getConfig();

    /**
     * 获取 当前任务请求到的页面
     *
     * @return the parentNode of current task
     * @author Jerry.X.He
     * @date 5/11/2017 9:10 PM
     * @since 1.0
     */
    Page getPage();

    /**
     * 获取 当前任务的创建时间
     *
     * @return the parentNode of current task
     * @author Jerry.X.He
     * @date 5/11/2017 9:10 PM
     * @since 1.0
     */
    long getCreateTime();

    /**
     * 获取 当前任务的执行时间
     *
     * @return the parentNode of current task
     * @author Jerry.X.He
     * @date 5/11/2017 9:10 PM
     * @since 1.0
     */
    long getRunTime();

    /**
     * 获取 当前任务的完成时间
     *
     * @return the parentNode of current task
     * @author Jerry.X.He
     * @date 5/11/2017 9:10 PM
     * @since 1.0
     */
    long getFinishTime();

    /**
     * 配置 page
     *
     * @return the parentNode of current task
     * @author Jerry.X.He
     * @date 5/11/2017 9:10 PM
     * @since 1.0
     */
    void setPage(Page page);

    /**
     * 配置 createTime
     *
     * @return the parentNode of current task
     * @author Jerry.X.He
     * @date 5/11/2017 9:10 PM
     * @since 1.0
     */
    void setCreateTime(long createTime);

    /**
     * 配置 runTime
     *
     * @return the parentNode of current task
     * @author Jerry.X.He
     * @date 5/11/2017 9:10 PM
     * @since 1.0
     */
    void setRunTime(long runTime);

    /**
     * 配置 finishTime
     *
     * @return the parentNode of current task
     * @author Jerry.X.He
     * @date 5/11/2017 9:10 PM
     * @since 1.0
     */
    void setFinishTime(long finishTime);

}