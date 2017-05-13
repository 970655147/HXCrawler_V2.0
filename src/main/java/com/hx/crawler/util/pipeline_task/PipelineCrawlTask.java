package com.hx.crawler.util.pipeline_task;

import com.hx.crawler.crawler.HtmlCrawlerConfig;
import com.hx.crawler.crawler.SingleUrlTask;
import com.hx.json.JSONArray;
import com.hx.log.util.Tools;

import static com.hx.log.util.Log.err;

/**
 * file name : PipelineCrawlTask.java
 * created at : 14:36  2017-03-11
 * created by 970655147
 */
public class PipelineCrawlTask implements Runnable {

    /**
     * 当前任务的参数 [任务 用于往线程池里面抛, 而不是一个 任务bean]
     */
    private SingleUrlTask scriptParameter;
    /**
     * 当前任务的爬取数据的配置
     */
    private HtmlCrawlerConfig crawlerConfig;
    /**
     * 当前任务的全局配置
     */
    private JSONArray globalConfig;
    /**
     * 当前任务的 stageId
     */
    private int stageId;

    /**
     * 初始化
     *
     * @param scriptParameter scriptParameter
     * @param crawlerConfig   crawlerConfig
     * @param globalConfig    globalConfig
     * @param stageId         stageId
     * @since 1.0
     */
    public PipelineCrawlTask(SingleUrlTask scriptParameter, HtmlCrawlerConfig crawlerConfig,
                             JSONArray globalConfig, int stageId) {
        this.scriptParameter = scriptParameter;
        this.crawlerConfig = crawlerConfig;
        this.globalConfig = globalConfig;
        this.stageId = stageId;
    }

    @Override
    public void run() {
        try {
            PipelineTaskUtils.doPipelineParse(globalConfig, scriptParameter, crawlerConfig, stageId);
        } catch (Exception e) {
            err("error while parse : '" + scriptParameter.getUrl() + "', exception : '" + Tools.errorMsg(e) + "'");
        }
    }

}
