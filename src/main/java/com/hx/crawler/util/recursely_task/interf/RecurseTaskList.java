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
     * ��������������һ��task
     * ���task��ʱ��, ��ע������task��createTime
     *
     * @param task ����������
     * @return void
     * @author 970655147 created at 2017-03-10 17:23
     */
    void add(Task task);

    /**
     * ��������������һ��task
     * ���task��ʱ��, ��ע������task��createTime
     *
     * @param parent ��������
     * @param url    ��ǰ�����url
     * @param method ����ǰ����ķ�ʽ
     * @param config ����ǰ��������Ҫ�Ĳ���
     * @return void
     * @author 970655147 created at 2017-03-10 17:23
     */
    void add(RecurseCrawlTaskFacade parent, String url, HttpMethod method, CrawlerConfig config);

    /**
     * �����������ȡ��һ��
     *
     * @return void
     * @author 970655147 created at 2017-03-10 17:23
     */
    Task take();

    /**
     * ��ǰ���������������
     *
     * @return void
     * @author 970655147 created at 2017-03-10 17:58
     */
    int size();

    /**
     * ��ǰ�����Ƿ�Ϊ��
     *
     * @return void
     * @author 970655147 created at 2017-03-10 17:59
     */
    boolean isEmpty();

}
