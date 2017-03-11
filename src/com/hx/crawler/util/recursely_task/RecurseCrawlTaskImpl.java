package com.hx.crawler.util.recursely_task;

import com.hx.crawler.crawler.interf.CrawlerConfig;
import com.hx.crawler.crawler.interf.HttpMethod;
import com.hx.crawler.crawler.interf.Page;
import com.hx.crawler.util.recursely_task.interf.RecurseCrawlTask;
import com.hx.crawler.util.recursely_task.interf.RecurseCrawlTaskFacade;
import com.hx.log.util.Tools;

/**
 * file name : RecurseCrawlTaskImpl.java
 * created at : 14:47  2017-03-11
 * created by 970655147
 */
public class RecurseCrawlTaskImpl implements RecurseCrawlTask {

    RecurseCrawlTaskFacade parent;
    String url;
    int depth;
    HttpMethod method;
    CrawlerConfig config;
    Page page;
    long createTime;
    long runTime;
    long finishTime;

    /**
     * ³õÊ¼»¯
     */
    RecurseCrawlTaskImpl(String url, int depth, HttpMethod method, CrawlerConfig config) {
        this(null, url, depth, method, config);
    }

    RecurseCrawlTaskImpl(RecurseCrawlTaskFacade parent, String url, int depth, HttpMethod method, CrawlerConfig config) {
        super();
        Tools.assert0(url != null, "url can't be null ! ");
        Tools.assert0(depth >= 0, "depth must gt 0 ! ");
        Tools.assert0(method != null, "method can't be null ! ");
        Tools.assert0(config != null, "config can't be null ! ");

        this.parent = parent;
        this.url = url;
        this.depth = depth;
        this.method = method;
        this.config = config;
    }

    @Override
    public RecurseCrawlTaskFacade getParent() {
        return parent;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public int getDepth() {
        return depth;
    }

    @Override
    public HttpMethod getMethod() {
        return method;
    }

    @Override
    public CrawlerConfig getConfig() {
        return config;
    }

    @Override
    public Page getPage() {
        return page;
    }

    @Override
    public long getCreateTime() {
        return createTime;
    }

    @Override
    public long getRunTime() {
        return runTime;
    }

    @Override
    public long getFinishTime() {
        return finishTime;
    }

    @Override
    public void setPage(Page page) {
        this.page = page;
    }

    @Override
    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    @Override
    public void setRunTime(long runTime) {
        this.runTime = runTime;
    }

    @Override
    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
    }

}
