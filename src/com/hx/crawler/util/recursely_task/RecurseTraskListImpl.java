package com.hx.crawler.util.recursely_task;

import com.hx.crawler.util.recursely_task.interf.RecurseCrawlTask;
import com.hx.crawler.util.recursely_task.interf.RecurseTaskList;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;

/**
 * file name : RecurseTraskListImpl.java
 * created at : 14:49  2017-03-11
 * created by 970655147
 */
public class RecurseTraskListImpl implements RecurseTaskList<RecurseCrawlTask> {

    private Deque<RecurseCrawlTask> list;
    private boolean bfs;

    /**
     * ≥ı ºªØ
     */
    public RecurseTraskListImpl(boolean bfs) {
        this(bfs, 10);
    }

    public RecurseTraskListImpl(boolean bfs, int estimateSize) {
        this.bfs = bfs;
        if (bfs) {
            list = new LinkedList<>();
        } else {
            list = new ArrayDeque<>(estimateSize);
        }
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
