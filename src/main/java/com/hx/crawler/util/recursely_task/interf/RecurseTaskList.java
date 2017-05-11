package com.hx.crawler.util.recursely_task.interf;

import com.hx.crawler.crawler.interf.CrawlerConfig;
import com.hx.crawler.crawler.interf.HttpMethod;

/**
 * file name : RecurseTaskList.java
 * created at : 14:45  2017-03-11
 * created by 970655147
 */
public interface RecurseTaskList<Task> {

    /**
     * 向任务队列中添加一个task
     * 添加task的时候, 请注意配置task的createTime
     *
     * @param task 给定的任务
     * @return void
     * @author 970655147 created at 2017-03-10 17:23
     */
    void add(Task task);

    /**
     * 向任务队列中添加一个task
     * 添加task的时候, 请注意配置task的createTime
     *
     * @param parent 父级任务
     * @param url    当前任务的url
     * @param method 请求当前任务的方式
     * @param config 请求当前任务所需要的参数
     * @return void
     * @author 970655147 created at 2017-03-10 17:23
     */
    void add(RecurseCrawlTaskFacade parent, String url, HttpMethod method, CrawlerConfig config);

    /**
     * 从任务队列中取出一个
     *
     * @return void
     * @author 970655147 created at 2017-03-10 17:23
     */
    Task take();

    /**
     * 当前队列中任务的数量
     *
     * @return void
     * @author 970655147 created at 2017-03-10 17:58
     */
    int size();

    /**
     * 当前队列是否为空
     *
     * @return void
     * @author 970655147 created at 2017-03-10 17:59
     */
    boolean isEmpty();

}
