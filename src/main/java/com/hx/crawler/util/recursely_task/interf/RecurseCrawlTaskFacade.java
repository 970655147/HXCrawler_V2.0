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

    RecurseCrawlTaskFacade getParent();

    String getUrl();

    int getDepth();

    HttpMethod getMethod();

    CrawlerConfig getConfig();

    Page getPage();

    long getCreateTime();

    long getRunTime();

    long getFinishTime();

}
