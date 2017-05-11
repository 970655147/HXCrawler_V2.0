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
     * ��ȡ ���ڵ�
     *
     * @return the parentNode of current task
     * @author Jerry.X.He
     * @date 5/11/2017 9:10 PM
     * @since 1.0
     */
    RecurseCrawlTaskFacade getParent();

    /**
     * ��ȡ url
     *
     * @return the parentNode of current task
     * @author Jerry.X.He
     * @date 5/11/2017 9:10 PM
     * @since 1.0
     */
    String getUrl();

    /**
     * ��ȡ ��ǰ��������
     *
     * @return the parentNode of current task
     * @author Jerry.X.He
     * @date 5/11/2017 9:10 PM
     * @since 1.0
     */
    int getDepth();

    /**
     * ��ȡ ��ǰ��������url�ķ���
     *
     * @return the parentNode of current task
     * @author Jerry.X.He
     * @date 5/11/2017 9:10 PM
     * @since 1.0
     */
    HttpMethod getMethod();

    /**
     * ��ȡ ��ǰ�������������
     *
     * @return the parentNode of current task
     * @author Jerry.X.He
     * @date 5/11/2017 9:10 PM
     * @since 1.0
     */
    CrawlerConfig getConfig();

    /**
     * ��ȡ ��ǰ�������󵽵�ҳ��
     *
     * @return the parentNode of current task
     * @author Jerry.X.He
     * @date 5/11/2017 9:10 PM
     * @since 1.0
     */
    Page getPage();

    /**
     * ��ȡ ��ǰ����Ĵ���ʱ��
     *
     * @return the parentNode of current task
     * @author Jerry.X.He
     * @date 5/11/2017 9:10 PM
     * @since 1.0
     */
    long getCreateTime();

    /**
     * ��ȡ ��ǰ�����ִ��ʱ��
     *
     * @return the parentNode of current task
     * @author Jerry.X.He
     * @date 5/11/2017 9:10 PM
     * @since 1.0
     */
    long getRunTime();

    /**
     * ��ȡ ��ǰ��������ʱ��
     *
     * @return the parentNode of current task
     * @author Jerry.X.He
     * @date 5/11/2017 9:10 PM
     * @since 1.0
     */
    long getFinishTime();

}
