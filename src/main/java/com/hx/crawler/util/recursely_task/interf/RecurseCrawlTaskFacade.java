package com.hx.crawler.util.recursely_task.interf;

import com.hx.crawler.crawler.interf.CrawlerConfig;
import com.hx.crawler.crawler.interf.HttpMethod;
import com.hx.crawler.crawler.interf.Page;

/**
 * file name : RecurseCrawlTaskFacade.java
 * created at : 14:44  2017-03-11
 * created by 970655147
 */
public interface RecurseCrawlTaskFacade {

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

}
