package com.hx.crawler.util.recursely_task;

import com.hx.crawler.crawler.interf.CrawlerConfig;
import com.hx.crawler.crawler.interf.HttpMethod;
import com.hx.crawler.util.recursely_task.interf.RecurseCrawlTask;
import com.hx.crawler.util.recursely_task.interf.RecurseCrawlTaskFacade;
import com.hx.crawler.util.recursely_task.interf.RecurseTaskList;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;

/**
 * file name : RecurseTaskListImpl.java
 * created at : 14:49  2017-03-11
 * created by 970655147
 */
public class RecurseTaskListImpl implements RecurseTaskList<RecurseCrawlTask> {

    /**
     * �������
     */
    private Deque<RecurseCrawlTask> list;
    /**
     * �� bfs ���� dfs
     */
    private boolean bfs;

    /**
     * ��ʼ��
     *
     * @param bfs          bfs
     * @param estimateSize ���Ƶ�������еĴ�С
     * @return
     */
    public RecurseTaskListImpl(boolean bfs, int estimateSize) {
        this.bfs = bfs;
        if (bfs) {
            list = new LinkedList<>();
        } else {
            list = new ArrayDeque<>(estimateSize);
        }
    }

    public RecurseTaskListImpl(boolean bfs) {
        this(bfs, 10);
    }

    @Override
    public void add(RecurseCrawlTask recurseCrawlTask) {
        recurseCrawlTask.setCreateTime(System.currentTimeMillis());
        synchronized (this) {
            if (bfs) {
                list.addFirst(recurseCrawlTask);
            } else {
                list.addLast(recurseCrawlTask);
            }
        }
    }

    @Override
    public void add(RecurseCrawlTaskFacade parent, String url, HttpMethod method, CrawlerConfig config) {
        RecurseCrawlTask task = RecurselyTaskUtils.newRecurseCrawlTask(parent, url, method, config);
        add(task);
    }

    @Override
    public RecurseCrawlTask take() {
        if (list.isEmpty()) {
            return null;
        }

        synchronized (this) {
            if (list.isEmpty()) {
                return null;
            }
            return list.removeLast();
        }
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

}
