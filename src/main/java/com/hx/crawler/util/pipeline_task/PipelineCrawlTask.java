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
     * ��ǰ����Ĳ��� [���� �������̳߳�������, ������һ�� ����bean]
     */
    private SingleUrlTask scriptParameter;
    /**
     * ��ǰ�������ȡ���ݵ�����
     */
    private HtmlCrawlerConfig crawlerConfig;
    /**
     * ��ǰ�����ȫ������
     */
    private JSONArray globalConfig;
    /**
     * ��ǰ����� stageId
     */
    private int stageId;

    /**
     * ��ʼ��
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
