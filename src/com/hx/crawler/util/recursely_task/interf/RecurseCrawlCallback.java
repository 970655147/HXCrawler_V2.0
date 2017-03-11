package com.hx.crawler.util.recursely_task.interf;

import com.hx.crawler.util.CrawlerUtils;

/**
 * file name : RecurseCrawlCallback.java
 * created at : 14:45  2017-03-11
 * created by 970655147
 */
public interface RecurseCrawlCallback<Task> {

    /**
     * 处理当前页面, 采集数据, 添加任务等等
     *
     * @param task 当前任务信息
     * @param todo 任务列表
     * @return
     * @throws
     * @author 970655147 created at 2017-03-10 18:14
     */
    void run(RecurseCrawlTaskFacade task, RecurseTaskList<Task> todo);

}
