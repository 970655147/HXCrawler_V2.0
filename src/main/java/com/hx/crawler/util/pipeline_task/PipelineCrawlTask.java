package com.hx.crawler.util.pipeline_task;

import com.hx.crawler.crawler.HtmlCrawlerConfig;
import com.hx.crawler.crawler.SingleUrlTask;
import com.hx.log.util.Tools;
import com.hx.json.JSONArray;

import static com.hx.log.util.Log.err;

/**
 * file name : PipelineCrawlTask.java
 * created at : 14:36  2017-03-11
 * created by 970655147
 */
public class PipelineCrawlTask implements Runnable {

    private SingleUrlTask scriptParameter;
    private HtmlCrawlerConfig crawlerConfig;
    private JSONArray globalConfig;
    private int stageId;

    public PipelineCrawlTask(SingleUrlTask scriptParameter, HtmlCrawlerConfig crawlerConfig, JSONArray globalConfig, int stageId) {
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
