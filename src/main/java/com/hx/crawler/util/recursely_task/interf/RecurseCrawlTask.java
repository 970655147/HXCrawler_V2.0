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

    RecurseCrawlTaskFacade getParent();

    String getUrl();

    int getDepth();

    HttpMethod getMethod();

    CrawlerConfig getConfig();

    Page getPage();

    long getCreateTime();

    long getRunTime();

    long getFinishTime();

    void setPage(Page page);

    void setCreateTime(long createTime);

    void setRunTime(long runTime);

    void setFinishTime(long finishTime);

}