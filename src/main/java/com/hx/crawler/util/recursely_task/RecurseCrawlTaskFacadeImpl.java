package com.hx.crawler.util.recursely_task;

import com.hx.crawler.crawler.interf.CrawlerConfig;
import com.hx.crawler.crawler.interf.HttpMethod;
import com.hx.crawler.crawler.interf.Page;
import com.hx.crawler.util.recursely_task.interf.RecurseCrawlTask;
import com.hx.crawler.util.recursely_task.interf.RecurseCrawlTaskFacade;
import com.hx.log.util.Tools;

/**
 * file name : RecurseCrawlTaskFacadeImpl.java
 * created at : 14:48  2017-03-11
 * created by 970655147
 */
public class RecurseCrawlTaskFacadeImpl implements RecurseCrawlTaskFacade {

    private RecurseCrawlTask task;

    /**
     * ≥ı ºªØ
     */
    public RecurseCrawlTaskFacadeImpl(RecurseCrawlTask task) {
        Tools.assert0(task != null, "task can't be null !");
        this.task = task;
    }

    @Override
    public RecurseCrawlTaskFacade getParent() {
        return task.getParent();
    }

    @Override
    public String getUrl() {
        return task.getUrl();
    }

    @Override
    public int getDepth() {
        return task.getDepth();
    }

    @Override
    public HttpMethod getMethod() {
        return task.getMethod();
    }

    @Override
    public CrawlerConfig getConfig() {
        return task.getConfig();
    }

    @Override
    public Page getPage() {
        return task.getPage();
    }

    @Override
    public long getCreateTime() {
        return task.getCreateTime();
    }

    @Override
    public long getRunTime() {
        return task.getRunTime();
    }

    @Override
    public long getFinishTime() {
        return task.getFinishTime();
    }

}
