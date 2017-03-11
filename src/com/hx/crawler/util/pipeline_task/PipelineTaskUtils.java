package com.hx.crawler.util.pipeline_task;

import com.hx.attrHandler.attrHandler.interf.AttrHandler;
import com.hx.attrHandler.util.AttrHandlerUtils;
import com.hx.crawler.crawler.HtmlCrawler;
import com.hx.crawler.crawler.HtmlCrawlerConfig;
import com.hx.crawler.crawler.SingleUrlTask;
import com.hx.crawler.crawler.interf.Crawler;
import com.hx.crawler.crawler.interf.CrawlerConfig;
import com.hx.crawler.crawler.interf.Page;
import com.hx.crawler.parser.interf.ResultJudger;
import com.hx.crawler.util.CrawlerUtils;
import com.hx.log.util.Constants;
import com.hx.log.util.JSONExtractor;
import com.hx.log.util.Tools;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;

import static com.hx.log.util.Log.err;

/**
 * file name : PipelineTaskUtils.java
 * created at : 14:33  2017-03-11
 * created by 970655147
 */
public final class PipelineTaskUtils {

    // disable constructor
    private PipelineTaskUtils() {
        Tools.assert0("can't instantiate !");
    }

    // ------------ PipelineTask���  add at 2016.08.13 --------------------
    // PipelineTask��س���
    public static final String URL = "url";
    public static final String METHOD = "method";
    public static final String XPATHES = "xpathes";
    public static final String JUDGER = "judger";
    public static final String START_UP = "startUp";
    public static final String TASK_PARAM = "taskParam";
    public static final String CRAWLER_CONFIG = "crawlerConfig";
    public static final String CLEAR_PREV_HEADERS = "clearPrevHeaders";
    public static final String CLEAR_PREV_COOKIES = "clearPrevCookies";
    public static final String CLEAR_PREV_DATA = "clearPrevData";
    public static final String HEADERS = "headers";
    public static final String COOKIES = "cookies";
    public static final String DATA = "data";
    public static final String TIMEOUT = "timeout";
    public static final String DEBUG_ENABLE = "debugEnable";
    public static final String SAVE_PREPARE_DOC = "savePreparedDoc";
    public static final String SAVE_FETCHED_RESULT = "saveFetchedResult";
    public static final String EXTRACT_FETCHED_RESULT = "extractFetchedResult";
    public static final String FETCHED_RESULT_MAPPER = "fetchedResultMapper";
    public static final String NEXT_STAGE_PARSE_ASYNC = "nextStageParseAsync";
    public static final String NEXT_STAGE_URL_PATTERN = "nextStageUrlPat";
    public static final String NEXT_STAGE_PARAM_PATTERN = "nextStageParamPat";

    // ��������
    public static String SAVE_PREPARED_DOC_PATH = Tools.getTmpPath("preparedDoc", Tools.HTML);
    public static String SAVE_FETCHED_RESULT_PATH = Tools.getTmpPath("fetchedResult", Tools.TXT);
    public static final String DO_PIPELINE_TASK_NAME = "doPipelineTask";


    public static void doPipelineTaskAsync(String configPath) throws Exception {
        doPipelineTask(configPath, null, null, true);
    }

    public static void doPipelineTaskAsync(String configPath, SingleUrlTask scriptParameter) throws Exception {
        doPipelineTask(configPath, scriptParameter, null, true);
    }

    public static void doPipelineTaskAsync(String configPath, HtmlCrawlerConfig crawlerConfig) throws Exception {
        doPipelineTask(configPath, null, crawlerConfig, true);
    }

    public static void doPipelineTaskAsync(String configPath, SingleUrlTask scriptParameter, HtmlCrawlerConfig crawlerConfig) throws Exception {
        doPipelineTask(configPath, scriptParameter, crawlerConfig, true);
    }

    public static void doPipelineTaskAsync(JSONArray globalConfig) throws Exception {
        doPipelineTask(globalConfig, null, null, true);
    }

    public static void doPipelineTaskAsync(JSONArray globalConfig, SingleUrlTask scriptParameter) throws Exception {
        doPipelineTask(globalConfig, scriptParameter, null, true);
    }

    public static void doPipelineTaskAsync(JSONArray globalConfig, HtmlCrawlerConfig crawlerConfig) throws Exception {
        doPipelineTask(globalConfig, null, crawlerConfig, true);
    }

    public static void doPipelineTaskAsync(JSONArray globalConfig, SingleUrlTask scriptParameter, HtmlCrawlerConfig crawlerConfig) throws Exception {
        doPipelineTask(globalConfig, scriptParameter, crawlerConfig, true);
    }

    public static JSONArray doPipelineTask(String configPath) throws Exception {
        return doPipelineTask(configPath, null, null, false);
    }

    public static JSONArray doPipelineTask(String configPath, SingleUrlTask scriptParameter) throws Exception {
        return doPipelineTask(configPath, scriptParameter, null, false);
    }

    public static JSONArray doPipelineTask(String configPath, HtmlCrawlerConfig crawlerConfig) throws Exception {
        return doPipelineTask(configPath, null, crawlerConfig, false);
    }

    public static JSONArray doPipelineTask(String configPath, SingleUrlTask scriptParameter, HtmlCrawlerConfig crawlerConfig) throws Exception {
        return doPipelineTask(configPath, scriptParameter, crawlerConfig, false);
    }

    public static JSONArray doPipelineTask(JSONArray globalConfig) throws Exception {
        return doPipelineTask(globalConfig, null, null, false);
    }

    public static JSONArray doPipelineTask(JSONArray globalConfig, SingleUrlTask scriptParameter) throws Exception {
        return doPipelineTask(globalConfig, scriptParameter, null, false);
    }

    public static JSONArray doPipelineTask(JSONArray globalConfig, HtmlCrawlerConfig crawlerConfig) throws Exception {
        return doPipelineTask(globalConfig, null, crawlerConfig, false);
    }

    public static JSONArray doPipelineTask(JSONArray globalConfig, SingleUrlTask scriptParameter, HtmlCrawlerConfig crawlerConfig) throws Exception {
        return doPipelineTask(globalConfig, scriptParameter, crawlerConfig, false);
    }

    private static JSONArray doPipelineTask(String configPath, SingleUrlTask scriptParameter,
                                            HtmlCrawlerConfig crawlerConfig, boolean async) throws Exception {
        JSONArray globalConfig = JSONArray.fromObject(Tools.getContent(configPath));
        return doPipelineTask(globalConfig, scriptParameter, crawlerConfig, async);
    }

    /**
     * @param globalConfig    һ��һ������������
     * @param scriptParameter scriptParameter[ͨ��startUp��������г�ʼ��]
     * @param crawlerConfig   crawlerConfig[ͨ��startUp��������г�ʼ��]
     * @param async           �Ƿ��첽ִ��
     * @return
     * @throws Exception
     * @Name: doPipelineTask0
     * @Description: ��ˮ��ʽ��ִ�и��������õ�����
     * ����globalConfig, �ҵ�startUpΪtrue������, �Ӹ�����ʼִ��
     * ��ʼ��scriptParameter, crawlerConfig, Ȼ������Ƿ��첽ִ������ ����ʹ�õ�ǰ�߳�ִ������, ����ʹ��Tools�е��̳߳�ִ������
     * ������첽ִ������, resΪ��Array, ���� resΪ���������񷵻صĽ��
     * @Create at 2016-09-30 20:02:54 by '970655147'
     */
    public static JSONArray doPipelineTask(JSONArray globalConfig, SingleUrlTask scriptParameter,
                                             HtmlCrawlerConfig crawlerConfig, boolean async) throws Exception {
        Tools.assert0(globalConfig != null, "'globalConfig' can't be null ");

        JSONArray res = new JSONArray();
        for (int i = 0, len = globalConfig.size(); i < len; i++) {
            JSONObject stageConfig = globalConfig.getJSONObject(i);
            if (stageConfig.optBoolean(START_UP)) {
                HtmlCrawlerConfig nextStageCrawlerConfig = crawlerConfig;
                if (crawlerConfig == null) {
                    nextStageCrawlerConfig = HtmlCrawlerConfig.get();
                }
                encapCrawlerConfig(stageConfig.optJSONObject(CRAWLER_CONFIG), nextStageCrawlerConfig);
                SingleUrlTask nextStageScriptParameter = scriptParameter;
                if (scriptParameter == null) {
                    Crawler crawler = HtmlCrawler.getInstance();
                    nextStageScriptParameter = CrawlerUtils.newSingleUrlTask(crawler, stageConfig.optString(URL),
                            stageConfig.optJSONObject(TASK_PARAM));
                } else {
                    nextStageScriptParameter.addParam(stageConfig.optJSONObject(TASK_PARAM));
                }

                if (async) {
                    Runnable task = new PipelineCrawlTask(nextStageScriptParameter, nextStageCrawlerConfig, globalConfig, i);
                    Tools.execute(task);
                } else {
                    res = doPipelineParse(globalConfig, nextStageScriptParameter, nextStageCrawlerConfig, i);
                }
                break;
            }
        }

        // user's program do
//		if(async) {
//			Tools.awaitShutdown();
//		}
//		Tools.closeAnBuffer(DO_PIPELINE_TASK_NAME);
        return res;
    }

    /**
     * @param scriptParameter ��ǰstage����Ҫ�Ĳ���
     * @param crawlerConfig   ������������Ҫ��config
     * @param globalConfig    һ��һ�������������
     * @param stageId         ��ǰ������������һ��stage
     * @return ���ص�ǰ����ץȡ���Ľ��
     * @throws Exception
     * @Name: doPipelineParse
     * @Description: ��ˮ��ʽ��ִ�и���������
     * У��scriptParameter, crawerConfig, globalConfig, ��β��֦
     * ��ȡ��ǰ���������, ��У��
     * �Ƿ���Ҫ��ӡ��־, ��ӡץȡ����ʼ����־
     * ��ȡ��ǰ�����xpathes, Ȼ��������õ������������get, post����, У���ȡ���Ľ��, �����Ҫ����ץȡ���ĵ�, �򱣴��ĵ�
     * Ȼ�� ����xpathes�����ĵ�, ���������õ�JUDGERȷ��"�ؼ�������"����[����JUDGER����ResultJudger]
     * У����, �����Ҫ������, �򱣴���
     * <p>
     * ������н�������Ҫִ�е�����, ���fetchedResult�л�ȡ��һ�������url(��), Ȼ��ץȡ��Ҫ�Ĳ���, ��װscriptParameter, �����Ƿ��첽ִ������, ������ʹ�õ�ǰ�߳�ִ������, ����ʹ��Tools�̳߳��е��߳�ִ������
     * @Create at 2016-09-30 20:08:18 by '970655147'
     */
    public static JSONArray doPipelineParse(JSONArray globalConfig, SingleUrlTask scriptParameter,
                                            HtmlCrawlerConfig crawlerConfig, int stageId) throws Exception {
        Tools.assert0(scriptParameter != null, "'scriptParameter' can't be null ");
        Tools.assert0(crawlerConfig != null, "'crawlerConfig' can't be null ");
        Tools.assert0(globalConfig != null, "'globalConfig' can't be null ");

        // prepare
        if (stageId >= globalConfig.size()) {
            // after finalStage
            return new JSONArray();
        }
        final JSONObject config = globalConfig.getJSONObject(stageId);
        if (Tools.isEmpty(config)) {
            err("got empty config while stageId : '" + stageId + "'");
            return new JSONArray();
        }

        Crawler crawler = scriptParameter.getCrawler();
        String url = scriptParameter.getUrl();
        JSONArray fetchedResult = sendRequestAndFetchResult(scriptParameter, crawlerConfig, config);

        if ((fetchedResult == null) || (fetchedResult.isEmpty())) {
            err("fetched nothing with url : '" + url + "'");
            return new JSONArray();
        }

        if (config.optBoolean(SAVE_FETCHED_RESULT)) {
            saveFetchedResult(fetchedResult, config);
        }

        // process next stage
        if (stageId < globalConfig.size() - 1) {
            processNextStage(globalConfig, fetchedResult, config, crawler, crawlerConfig, stageId);
        }

        return fetchedResult;
    }

    /**
     * ���ݵ�ǰstage������, ��������, ��ץȡ����
     *
     * @param scriptParameter ��ǰstage����Ҫ�Ĳ���
     * @param crawlerConfig   ������������Ҫ��config
     * @param config          ��ǰstage��������Ϣ
     * @return net.sf.json.JSONArray
     * @throws
     * @author 970655147 created at 2017-03-11 14:16
     */
    private static JSONArray sendRequestAndFetchResult(SingleUrlTask scriptParameter, HtmlCrawlerConfig crawlerConfig,
                                                       final JSONObject config) throws Exception {
        Crawler crawler = scriptParameter.getCrawler();
        String url = scriptParameter.getUrl();
        boolean debugEnable = Boolean.valueOf(scriptParameter.getParam(Tools.DEBUG_ENABLE).toString());

        long start = System.currentTimeMillis();
        CrawlerUtils.logBeforeTask(scriptParameter, debugEnable);

        // encap config, post requests
        encapCrawlerConfig(config.optJSONObject(CRAWLER_CONFIG), crawlerConfig);
        Page<HttpResponse> page = null;
        if (Tools.GET.equalsIgnoreCase(config.optString(METHOD))) {
            page = crawler.getPage(url, crawlerConfig);
        } else if (Tools.POST.equalsIgnoreCase(config.optString(METHOD))) {
            page = crawler.postPage(url, crawlerConfig);
        }
        if (page == null) {
            err("got empty page while fetch : '" + url + "'");
            return null;
        }

        // get result by xpathes
        String html = page.getContent();
        if (config.optBoolean(SAVE_PREPARE_DOC)) {
            CrawlerUtils.getPreparedDoc(url, html, SAVE_PREPARED_DOC_PATH);
        }
        // encap xpathes
        JSONArray xpathesFromConfig = config.getJSONArray(XPATHES);
        String[] xpathes = new String[xpathesFromConfig.size()];
        for (int i = 0, len = xpathesFromConfig.size(); i < len; i++) {
            xpathes[i] = xpathesFromConfig.getString(i);
        }

        JSONArray fetchedResult = CrawlerUtils.getResultByXPathes(html, url, xpathes, new ResultJudger() {
            @Override
            public boolean isResultNull(int idx, JSONArray fetchedData) {
                JSONArray judgersConfig = config.optJSONArray(JUDGER);
                int gotResNum = -1;
                for (Object _judger : judgersConfig) {
                    String judgerPat = _judger.toString();
                    if (!Tools.isEmpty(judgerPat)) {
                        JSONArray arr = JSONExtractor.extractInfoFromJSON(fetchedData, judgerPat);
                        if (Tools.isEmpty(arr)) {
                            return true;
                        }

                        if (gotResNum < 0) {
                            gotResNum = arr.size();
                        } else {
                            if (gotResNum != arr.size()) {
                                return true;
                            }
                        }
                    }
                }

                return false;
            }
        });

        long spent = System.currentTimeMillis() - start;
        CrawlerUtils.logAfterTask(scriptParameter, String.valueOf(fetchedResult), String.valueOf(spent), debugEnable);
        return fetchedResult;
    }

    /**
     * �����Ҫ����ץȡ�������ݵĻ�, �־û�ץȡ������
     *
     * @param fetchedResult ץȡ���Ľ��
     * @param config        ��ǰstage��������Ϣ
     * @return void
     * @throws
     * @author 970655147 created at 2017-03-11 14:24
     */
    private static void saveFetchedResult(JSONArray fetchedResult, JSONObject config) throws Exception {
        JSONArray extractedFetchedResult = fetchedResult;
        if (!Tools.isEmpty(config.optString(EXTRACT_FETCHED_RESULT))) {
            extractedFetchedResult = JSONExtractor.extractInfoFromJSON(extractedFetchedResult, config.optString(EXTRACT_FETCHED_RESULT));
        }

        String saveFetchedResult = String.valueOf(extractedFetchedResult);
        if (!Tools.isEmpty(config.optString(FETCHED_RESULT_MAPPER))) {
            AttrHandler handler = AttrHandlerUtils.handlerParse(config.optString(FETCHED_RESULT_MAPPER), Constants.HANDLER);
            saveFetchedResult = handler.handle(String.valueOf(saveFetchedResult));
        }
        Tools.appendBufferCRLF(DO_PIPELINE_TASK_NAME, String.valueOf(saveFetchedResult));
    }

    /**
     * ������һ��stage������, ͬ�� �����첽
     *
     * @param globalConfig  һ��һ�������������
     * @param fetchedResult ��ǰstageץȡ��������
     * @param config        ��ǰstage��������Ϣ
     * @param crawler       crawler
     * @param crawlerConfig ������������Ҫ��config
     * @param stageId       ��ǰstage��id
     * @return void
     * @throws
     * @author 970655147 created at 2017-03-11 14:28
     */
    private static void processNextStage(JSONArray globalConfig, JSONArray fetchedResult, JSONObject config,
                                         Crawler crawler, HtmlCrawlerConfig crawlerConfig, int stageId) {
        String nextStageUrlPat = config.optString(NEXT_STAGE_URL_PATTERN);
        JSONObject nextStageUrlParam = config.optJSONObject(NEXT_STAGE_PARAM_PATTERN);
        boolean parseAsync = config.optBoolean(NEXT_STAGE_PARSE_ASYNC);

        if (!Tools.isEmpty(nextStageUrlPat)) {
            JSONArray nextStageUrls = JSONExtractor.extractInfoFromJSON(fetchedResult, nextStageUrlPat);
            JSONObject nextStageParams = new JSONObject();
            for (Object _key : nextStageUrlParam.names()) {
                String key = (String) _key;
                if (!Tools.isEmpty(key)) {
                    JSONArray val = JSONExtractor.extractInfoFromJSON(fetchedResult, nextStageUrlParam.getString(key));
                    nextStageParams.element(key, val);
                }
            }

            for (int i = 0; i < nextStageUrls.size(); i++) {
                String nextStageUrl = nextStageUrls.getString(i);
                JSONObject nextStageConfig = globalConfig.getJSONObject(stageId + 1);
                HtmlCrawlerConfig nextStageCrawlerConfig = new HtmlCrawlerConfig(crawlerConfig);
                // duplicate, rm
                // encapCrawlerConfig(nextStageConfig.optJSONObject(CRAWLER_CONFIG), nextStageCrawlerConfig);
                SingleUrlTask nextStageScriptParameter = CrawlerUtils.newSingleUrlTask(crawler, nextStageUrl, nextStageConfig.optJSONObject(TASK_PARAM));
                for (Object _key : nextStageParams.names()) {
                    String key = (String) _key;
                    JSONArray attrs = nextStageParams.getJSONArray(key);
                    if (!Tools.isEmpty(attrs)) {
                        nextStageScriptParameter.addParam(key, attrs.opt(i));
                    }
                }

                Runnable nextStage = new PipelineCrawlTask(nextStageScriptParameter, nextStageCrawlerConfig, globalConfig, stageId + 1);
                if (parseAsync) {
                    Tools.execute(nextStage);
                } else {
                    nextStage.run();
                }
            }
        }
    }

    /**
     * @param crawlerConfigObj �����е�config����, �����е����ݷ�װ��CrawlerConfig��
     * @param crawlerConfig    ʵ����Ҫ��װ��CrawlerConfig����
     * @Name: encapCrawlerConfig
     * @Description: ���ݸ��������÷�װcrawlerConfig
     * �����Ҫ��� header, cookie, data, �������crawlerConfig�е�����
     * Ȼ�������config�����õ�header, cookie, data
     * @Create at 2016-09-30 20:25:14 by '970655147'
     */
    private static void encapCrawlerConfig(JSONObject crawlerConfigObj, CrawlerConfig crawlerConfig) {
        if (!Tools.isEmpty(crawlerConfigObj)) {
            if (crawlerConfigObj.optBoolean(CLEAR_PREV_HEADERS)) {
                crawlerConfig.getHeaders().clear();
            }
            if (crawlerConfigObj.optBoolean(CLEAR_PREV_COOKIES)) {
                crawlerConfig.getCookies().clear();
            }
            if (crawlerConfigObj.optBoolean(CLEAR_PREV_DATA)) {
                crawlerConfig.getData().clear();
            }
            // headers, cookies, data
            crawlerConfig.addHeaders(crawlerConfigObj.optJSONObject(HEADERS));
            crawlerConfig.addCookies(crawlerConfigObj.optJSONObject(COOKIES));
            crawlerConfig.addData(crawlerConfigObj.optJSONObject(DATA));
        }
    }

}
