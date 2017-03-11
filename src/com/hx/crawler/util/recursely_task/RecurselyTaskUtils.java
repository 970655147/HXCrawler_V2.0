package com.hx.crawler.util.recursely_task;

import com.hx.crawler.crawler.HtmlCrawler;
import com.hx.crawler.crawler.interf.Crawler;
import com.hx.crawler.crawler.interf.CrawlerConfig;
import com.hx.crawler.crawler.interf.HttpMethod;
import com.hx.crawler.crawler.interf.Page;
import com.hx.crawler.util.recursely_task.interf.RecurseCrawlCallback;
import com.hx.crawler.util.recursely_task.interf.RecurseCrawlTask;
import com.hx.crawler.util.recursely_task.interf.RecurseCrawlTaskFacade;
import com.hx.crawler.util.recursely_task.interf.RecurseTaskList;
import com.hx.log.util.Log;
import com.hx.log.util.Tools;

/**
 * file name : RecurselyTaskUtils.java
 * created at : 14:50  2017-03-11
 * created by 970655147
 */
public final class RecurselyTaskUtils {

    // disable constructor
    private RecurselyTaskUtils() {
        Tools.assert0("can't instantiate !");
    }

    public static void recurselyTask(String seedUrl, HttpMethod method, CrawlerConfig config, RecurseCrawlCallback callback) {
        recurselyTask(seedUrl, method, config, callback, true);
    }

    public static void recurselyTask(String seedUrl, HttpMethod method, CrawlerConfig config, RecurseCrawlCallback callback, boolean isBfs) {
        recurselyTask(seedUrl, method, config, callback, false, isBfs);
    }

    /**
     * 传入一个seedUrl, 以及相关的需要context, 以及拿到page之后的回调
     * callback中抓取数据, 以及拿到需要的子任务, 添加到todo中, 然后 迭代迭代
     * 不断的传下去
     *
     * @param seedUrl  种子url
     * @param method   url请求的方式
     * @param config   url请求需要的context
     * @param callback 拿到page之后处理的回调
     * @param isBfs    bfs or dfs
     * @return void
     * @throws
     * @author 970655147 created at 2017-03-10 19:03
     */
    public static void recurselyTask(String seedUrl, HttpMethod method, CrawlerConfig config, RecurseCrawlCallback callback, boolean isAsync, boolean isBfs) {
        Tools.assert0(seedUrl != null, "'seedUrl' can't be null ");
        Tools.assert0(method != null, "'method' can't be null ");
        Tools.assert0(config != null, "'config' can't be null ");
        Tools.assert0(callback != null, "'callback' can't be null ");

        final RecurseCrawlCallback fCallback = callback;
        final RecurseTaskList<RecurseCrawlTask> todo = new RecurseTraskListImpl(isBfs);
        RecurseCrawlTask seedTask = new RecurseCrawlTaskImpl(seedUrl, 0, method, config);
        todo.add(seedTask);

        while (!todo.isEmpty()) {
            final RecurseCrawlTask task = todo.take();
            if (!isAsync) {
                recurselyTask0(task, callback, todo);
            } else {
                Tools.execute(new Runnable() {
                    @Override
                    public void run() {
                        recurselyTask0(task, fCallback, todo);
                    }
                });
            }
        }
    }

    /**
     * 根据给定个的parent, url, 以及相关的配置创建RecurseCrawlTaskImpl
     * 此处是留给用户的创建实例的唯一入口
     *
     * @param parent 当前结点的父节点
     * @param url    当前结点的url
     * @param method 当前结点的请求方式
     * @param config 当前url所需要的参数
     * @return com.hx.crawler.util.CrawlerUtils.RecurseCrawlTaskImpl
     * @throws
     * @author 970655147 created at 2017-03-10 19:43
     */
    public static RecurseCrawlTask newRecurseCrawlTask(RecurseCrawlTaskFacade parent, String url, HttpMethod method, CrawlerConfig config) {
        int depth = (parent == null) ? 0 : parent.getDepth() + 1;
        return new RecurseCrawlTaskImpl(parent, url, depth, method, config);
    }

    /**
     * 处理recurselyTask0的核心业务, 发送请求, 并处理callback
     *
     * @param task     当前任务
     * @param callback 处理结果的回调
     * @param todo     任务列表
     * @return boolean
     * @throws
     * @author 970655147 created at 2017-03-10 22:43
     */
    private static boolean recurselyTask0(RecurseCrawlTask task, RecurseCrawlCallback callback, RecurseTaskList<RecurseCrawlTask> todo) {
        if (task == null) {
            Log.err("got an 'null' task, ignore ");
            return false;
        }

        Page page = null;
        task.setRunTime(System.currentTimeMillis());
        try {
            page = dispatchSendPost(task, task.getMethod());
        } catch (Exception e) {
            Log.err("error while send post for url : " + task.getUrl() + ", with config : " + task.getConfig());
            page = null;
        }
        task.setPage(page);

        if (page == null) {
            task.setFinishTime(System.currentTimeMillis());
            // logged
            return false;
        }

        callback.run(new RecurseCrawlTaskFacadeImpl(task), todo);
        task.setFinishTime(System.currentTimeMillis());
        return true;
    }

    /**
     * 根据给定的task, 发送请求.
     *
     * @param task   给定的task
     * @param method task对应的任务需要请求的方式
     * @return com.hx.crawler.crawler.interf.Page
     * @throws
     * @author 970655147 created at 2017-03-10 18:58
     */
    private static Page dispatchSendPost(RecurseCrawlTask task, HttpMethod method) throws Exception {
        Page page = null;
        if (method == null) {
            Log.err("got an 'null' method while send post for url : " + task.getUrl() + ", with config : " + task.getConfig());
        } else {
            Crawler crawler = HtmlCrawler.getInstance();
            if (HttpMethod.GET == method) {
                page = crawler.getPage(task.getUrl(), task.getConfig());
            } else if (HttpMethod.POST == method) {
                page = crawler.postPage(task.getUrl(), task.getConfig());
            } else if (HttpMethod.PUT == method) {
                page = crawler.putPage(task.getUrl(), task.getConfig());
            } else if (HttpMethod.DELETE == method) {
                page = crawler.deletePage(task.getUrl(), task.getConfig());
            } else if ((HttpMethod.HEAD == method) || (HttpMethod.TRACE == method)) {
                page = crawler.getPage(task.getUrl(), task.getConfig());
            } else {
                Log.err("got an unknown method while send post for url : " + task.getUrl() + ", with config : " + task.getConfig());
            }
        }

        return page;
    }

}
