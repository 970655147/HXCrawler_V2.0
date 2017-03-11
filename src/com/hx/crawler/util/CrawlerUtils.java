/**
 * file name : Tools.java
 * created at : 6:58:34 PM Jul 25, 2015
 * created by 970655147
 */

package com.hx.crawler.util;

import static com.hx.log.util.Log.err;
import static com.hx.log.util.Log.info;

import com.hx.crawler.crawler.interf.*;
import com.hx.crawler.util.pipeline_task.PipelineTaskUtils;
import com.hx.crawler.util.recursely_task.RecurselyTaskUtils;
import com.hx.crawler.util.recursely_task.interf.RecurseCrawlCallback;
import com.hx.crawler.util.recursely_task.interf.RecurseCrawlTask;
import com.hx.crawler.util.recursely_task.interf.RecurseCrawlTaskFacade;
import com.hx.log.util.*;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.*;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.ccil.cowan.tagsoup.Parser;
import org.ccil.cowan.tagsoup.XMLWriter;
import org.xml.sax.InputSource;

import com.hx.crawler.crawler.HtmlCrawler;
import com.hx.crawler.crawler.HtmlCrawlerConfig;
import com.hx.crawler.crawler.SingleUrlTask;
import com.hx.crawler.parser.xpathImpl.XPathParser;
import com.hx.crawler.parser.interf.ResultJudger;
import com.hx.log.util.interf.LogPattern.LogPatternChain;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

// ������
public final class CrawlerUtils {

    // disable constructor
    private CrawlerUtils() {
        Tools.assert0("can't instantiate !");
    }

    // parse ��س���
    public static String PARSE_METHOD_NAME = "parse";
    public static boolean IS_PARSE_METHOD_STATIC = true;
    public static Class[] PARSE_METHOD_PARAMTYPES = new Class[]{ScriptParameter.class};

    // ----------------- crawler ������� -----------------------
    // ��ȡ�����֮����ĵ�
    public static void getPreparedDoc(String url, String file) throws Exception {
        Tools.assert0(url != null, "'url' can't be null ");
        String html = HtmlCrawler.getInstance().getPage(url).getContent();
        getPreparedDoc(url, html, file);
    }

    public static void getPreparedDoc(String url, String html, String file) throws Exception {
        Tools.assert0(html != null, "'html' can't be null ");
        Tools.assert0(file != null, "'file' can't be null ");
        Tools.save(StringEscapeUtils.unescapeHtml(CrawlerUtils.normalize(html)), file);
    }

    public static final com.hx.crawler.parser.interf.Parser xpathParser = new XPathParser();

    // ͨ��xpath ��ȡ���
    public static JSONArray getResultByXPath(String html, String url, String xpath) throws Exception {
        Tools.assert0(html != null, "'html' can't be null ");
        Tools.assert0(xpath != null, "'xpath' can't be null ");
        return xpathParser.parse(CrawlerUtils.normalize(html), url, xpath);
    }

    public static JSONArray getResultByXPathes(String html, String url, String[] xpathes, ResultJudger judger) throws Exception {
        Tools.assert0(html != null, "'html' can't be null ");
        Tools.assert0(xpathes != null, "'xpathes' can't be null ");
        Tools.assert0(judger != null, "'judger' can't be null ");

        for (int i = 0; i < xpathes.length; i++) {
            JSONArray res = getResultByXPath(html, url, xpathes[i]);
            if (!judger.isResultNull(i, res)) {
                return res;
            }
        }

        return new JSONArray();
    }

    // ���е�unicode surrogates�������ַ�����ʾ
    private static final Set<String> UNICODE_SURROGATES = Tools.asSortedSet();
    // ��һ��ʹ�������ߵ��Ĳ���
    private static String NEED_BE_FILTER_REG = "<!DOCTYPE html>" + "|" + "xmlns=\"http://www.w3.org/1999/xhtml\"";

    static {
        for (int i = 55296; i <= 57343; i++) {
            UNICODE_SURROGATES.add(String.valueOf(i));
        }
    }

    // ��ʽ��html [ȥ��, ��� ���淶�ı�ǩ]
    // ˼· : ʹ��tagSoup�淶��xml
    // ע�� : ��Ҫ�ض���ContentHandler�����
    // ��Ҫȥ��xml����, ��Ҫȥ��html��ǩ��xmlns������
    public static String normalize(String html) throws Exception {
        Tools.assert0(html != null, "'html' can't be null ");

        StringReader sr = new StringReader(html);
        Parser parser = new Parser();        //ʵ����Parse

//		// �����ʽ��֮���xml
        XMLWriter writer = new XMLWriter();    //ʵ����XMLWriter����SAX���ݴ�����
        parser.setContentHandler(writer);    //�������ݴ�����
        StringWriter strWriter = new StringWriter();
        writer.setOutput(strWriter);
        parser.parse(new InputSource(sr));    //����

        // ��ȡ�ṹ���õ�html, ��ȥ��xml����, xml�����Ƿ��ڵ�һ�е�, ��� �����xml�Ļ�, ȥ��xml����
        String res = strWriter.toString();
        int firstLineEnd = res.indexOf("\n");
        if (res.substring(0, firstLineEnd).contains("<?xml")) {
            res = res.substring(firstLineEnd + 1);
        }

        // ������Ϊ�������xmlns������, �������ҵ�xPath��ȡ���������⡣����, ����˵ ֻ��ͨ��/*[@XX='XXX'] ����ȡ����
        // ���Ҷ�ȡ�������� ����һ��xmlns������...						--2015.07.31
//		String html = "<html xmlns=\"http://www.w3.org/1999/xhtml\"><body><strong><span id=\"ctl01_ContentPlaceHolder1_lblPriceTitle\">ProductPricing</span>:</strong></body></html>";
        // ���� �����res����ͳһ�Ĵ���, ȥ��html��ǩ��xmlns������
        // ���Ӷ���unicode high-UNICODE_SURROGATES, low-UNICODE_SURROGATES ��Χ�ڵ�code�Ĵ���..        -- 2017.03.10
        res = res.replaceAll(NEED_BE_FILTER_REG, Tools.EMPTY_STR);
        return trimUnicodeSurrogates(res);
    }

    /**
     * ȥ�����е�unicode surrogate����㷶Χ�ڵ��ַ�, ���� sax�����ᱨ����
     *
     * @param res trim surrogate�����֮ǰ�ĵĽ��
     * @return java.lang.String
     * @throws
     * @author 970655147 created at 2017-03-10 22:21
     */
    private static String trimUnicodeSurrogates(String res) {
        // ��Ҫֱ�������е� surrogates����� &#$code��Ϊ�ָ���, ��Ȼ �ٺ�, ���Ű�.
        Set<String> surrogateSeps = Tools.asSet("&#", ";");
        WordsSeprator sep = new WordsSeprator(res, surrogateSeps, null, true, true);
        StringBuilder sb = new StringBuilder();
        while (sep.hasNext()) {
            String next = sep.next();

            if ("&#".equals(next)) {
                if (sep.hasNext()) {
                    String maySurrogate = sep.next();
                    if (UNICODE_SURROGATES.contains(maySurrogate)) {
                        // trim ';'
                        sep.next();
                    } else {
                        sb.append(next);
                        sb.append(maySurrogate);
                    }
                    continue;
                }
            }
            sb.append(next);
        }

        return sb.toString();
    }

    /**
     * ����һ��ScriptParameter
     *
     * @param crawler scriptParameter��Ӧ��crawler
     * @param url     ��Ҫ��ȡ��url
     * @param param   ������������Ҫ�Ĳ���
     * @return com.hx.crawler.crawler.SingleUrlTask
     * @throws
     * @author 970655147 created at 2017-03-11 13:57
     */
    public static SingleUrlTask newSingleUrlTask(Crawler<HttpResponse, Header, String, NameValuePair, String, String> crawler,
                                                 String url, Map<String, Object> param) {
        SingleUrlTask res = new SingleUrlTask();
        res.setCrawler(crawler);
        res.setUrl(url);
        res.setParam(param);
        crawler.setScriptParameter(res);
        res.setTaskGroupId(110);
        res.setTaskId(120);

        return res;
    }

    // ͨ��xpath��ȡHXCrawler��xpath
    // ���Ը�дΪJSONArray.toString() ʵ�� [˼·����'duncen'[newEggͬ��] ]
    public static String getRealXPathByXPathObj(String xpath) {
        Tools.assert0(xpath != null, "'xpath' can't be null ");
        return new JSONArray().element(xpath).toString();
    }

    public static String getRealXPathByXPathObj(String... xpathes) {
        Tools.assert0(xpathes != null, "'xpathes' can't be null ");
        JSONArray res = new JSONArray();
        for (String xpath : xpathes) {
            res.add(xpath);
        }

        return res.toString();
    }

    // ���÷������ָ����class��methodName����
    public static void parse(String className, String url, Map<String, Object> params) throws Exception {
        parse(className, url, params, PARSE_METHOD_NAME, IS_PARSE_METHOD_STATIC, PARSE_METHOD_PARAMTYPES);
    }

    /**
     * ���ø�����className��Ӧ��method����, ����������ȡ������ص�ҵ��
     *
     * @param className        clazzName
     * @param url              ��ȡ��url
     * @param params           ������������Ҫ�Ĳ���
     * @param methodName       ����������
     * @param isStaticMethod   �����ķ����Ƿ��Ǿ�̬����
     * @param methodParamTypes �����ķ����Ĳ��������б�
     * @return void
     * @throws
     * @author 970655147 created at 2017-03-11 13:56
     */
    public static void parse(String className, String url, Map<String, Object> params, String methodName,
                             boolean isStaticMethod, Class[] methodParamTypes) throws Exception {
        Tools.assert0(className != null, "'className' can't be null ");
        Tools.assert0(url != null, "'url' can't be null ");
        Tools.assert0(methodName != null, "'methodName' can't be null ");

        Class clazz = Class.forName(className);
        Method method = clazz.getMethod(methodName, methodParamTypes);
        SingleUrlTask singleUrlTask = newSingleUrlTask(HtmlCrawler.newInstance(), url, params);

        try {
            if (isStaticMethod) {
                method.invoke(null, singleUrlTask);
            } else {
                method.invoke(clazz.newInstance(), singleUrlTask);
            }
        } catch (Exception e) {
            logErrorMsg(singleUrlTask, e);
        }
    }

    public static void parseAsync(final String className, final String url, final Map<String, Object> params) throws Exception {
        parseAsync(className, url, params, PARSE_METHOD_NAME, IS_PARSE_METHOD_STATIC, PARSE_METHOD_PARAMTYPES);
    }

    public static void parseAsync(final String className, final String url, final Map<String, Object> params, final String methodName, final boolean isStaticMethod, final Class[] methodParamTypes) throws Exception {
        Tools.execute(new Runnable() {
            public void run() {
                try {
                    parse(className, url, params, methodName, isStaticMethod, methodParamTypes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // ΪnextStageParams���category
    public static void addNameUrlSite(JSONObject category, JSONObject nextStageParams) {
        Tools.assert0(!Tools.isEmpty(category), "'category' can't be null ");
        nextStageParams.put(Tools.NAME, category.getString(Tools.NAME));
        nextStageParams.put(Tools.URL, category.getString(Tools.URL));
        nextStageParams.put(Tools.SITE, nextStageParams.getString(Tools.SITE) + "." + category.getString(Tools.NAME));
    }

    // ��ȡtaskName
    public static String getTaskName(ScriptParameter<?, ?, ?, ?, ?, ?> singleUrlTask) {
        Tools.assert0(singleUrlTask != null, "'singleUrlTask' can't be null ");
        return "crawl " + singleUrlTask.getParam().get(Tools.TASK) + " from " + singleUrlTask.getParam().get(Tools.SITE);
    }

    // ------------ ��־��� --------------------
    // ��ص�logPattern
    public static LogPatternChain taskBeforeLogPatternChain = LogPatternUtils.initLogPattern(Constants.optString(Constants._TASK_BEFORE_LOG_PATTERN));
    public static LogPatternChain taskAfterLogPatternChain = LogPatternUtils.initLogPattern(Constants.optString(Constants._TASK_AFTER_LOG_PATTERN));
    public static LogPatternChain taskExceptionLogPatternChain = LogPatternUtils.initLogPattern(Constants.optString(Constants._TASK_EXCEPTION_LOG_PATTERN));

    // ��ӡ�������־��Ϣ
    public static void logBeforeTask(ScriptParameter<?, ?, ?, ?, ?, ?> singleUrlTask, boolean debugEnable) {
        Tools.assert0(singleUrlTask != null, "'singleUrlTask' can't be null ");
        if (debugEnable) {
            String info = LogPatternUtils.formatLogInfo(taskBeforeLogPatternChain, new JSONObject()
                    .element(Constants.LOG_PATTERN_URL, singleUrlTask.getUrl()).element(Constants.LOG_PATTERN_TASK_NAME, CrawlerUtils.getTaskName(singleUrlTask))
                    .element(Constants.LOG_PATTERN_MODE, Constants.LOG_MODES[Constants.OUT_IDX])
            );
            Log.log(info);
        }
    }

    public static void logBeforeTask(ScriptParameter<?, ?, ?, ?, ?, ?> singleUrlTask, String debugEnable) {
        logBeforeTask(singleUrlTask, Boolean.parseBoolean(debugEnable));
    }

    public static void logAfterTask(ScriptParameter<?, ?, ?, ?, ?, ?> singleUrlTask, String fetchedResult, String spent, boolean debugEnable) {
        Tools.assert0(singleUrlTask != null, "'singleUrlTask' can't be null ");
        if (debugEnable) {
            String info = LogPatternUtils.formatLogInfo(taskAfterLogPatternChain, new JSONObject()
                    .element(Constants.LOG_PATTERN_RESULT, fetchedResult).element(Constants.LOG_PATTERN_TASK_NAME, CrawlerUtils.getTaskName(singleUrlTask))
                    .element(Constants.LOG_PATTERN_SPENT, spent).element(Constants.LOG_PATTERN_MODE, Constants.LOG_MODES[Constants.OUT_IDX])
            );
            Log.log(info);
        }
    }

    public static void logAfterTask(ScriptParameter<?, ?, ?, ?, ?, ?> singleUrlTask, String fetchedResult, String spent, String debugEnable) {
        logAfterTask(singleUrlTask, fetchedResult, spent, Boolean.parseBoolean(debugEnable));
    }

    public static void logErrorMsg(ScriptParameter<?, ?, ?, ?, ?, ?> singleUrlTask, Exception e) {
        Tools.assert0(singleUrlTask != null, "'singleUrlTask' can't be null ");
        String info = LogPatternUtils.formatLogInfo(taskExceptionLogPatternChain, new JSONObject()
                .element(Constants.LOG_PATTERN_EXCEPTION, e.getClass().getName() + " : " + e.getMessage())
                .element(Constants.LOG_PATTERN_TASK_NAME, CrawlerUtils.getTaskName(singleUrlTask))
                .element(Constants.LOG_PATTERN_URL, singleUrlTask.getUrl()).element(Constants.LOG_PATTERN_MODE, Constants.LOG_MODES[Constants.ERR_IDX])
        );
        Log.err(info);
    }

    // ------------ PipelineTask���  add at 2016.08.13 --------------------
    // ��ȡ������configPath��Ӧ������, Ȼ�� ִ����ˮ��ʽ��ִ������
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

    public static JSONArray doPipelineTask(JSONArray globalConfig, SingleUrlTask scriptParameter,
                                           HtmlCrawlerConfig crawlerConfig, boolean async) throws Exception {
        return PipelineTaskUtils.doPipelineTask(globalConfig, scriptParameter, crawlerConfig, async);
    }

    // ------------ recurselyTask���  add at 2017.03.10 --------------------

    public static void recurselyTask(String seedUrl, HttpMethod method, CrawlerConfig config, RecurseCrawlCallback callback) {
        recurselyTask(seedUrl, method, config, callback, true);
    }

    public static void recurselyTask(String seedUrl, HttpMethod method, CrawlerConfig config, RecurseCrawlCallback callback, boolean isBfs) {
        recurselyTask(seedUrl, method, config, callback, false, isBfs);
    }

    /**
     * ����һ��seedUrl, �Լ���ص���Ҫcontext, �Լ��õ�page֮��Ļص�
     * callback��ץȡ����, �Լ��õ���Ҫ��������, ��ӵ�todo��, Ȼ�� ��������
     * ���ϵĴ���ȥ
     *
     * @param seedUrl  ����url
     * @param method   url����ķ�ʽ
     * @param config   url������Ҫ��context
     * @param callback �õ�page֮����Ļص�
     * @param isBfs    bfs or dfs
     * @return void
     * @throws
     * @author 970655147 created at 2017-03-10 19:03
     */
    public static void recurselyTask(String seedUrl, HttpMethod method, CrawlerConfig config, RecurseCrawlCallback callback, boolean isAsync, boolean isBfs) {
        RecurselyTaskUtils.recurselyTask(seedUrl, method, config, callback, isAsync, isBfs);
    }

    public static RecurseCrawlTask newRecurseCrawlTask(RecurseCrawlTaskFacade parent, String url, HttpMethod method, CrawlerConfig config) {
        return RecurselyTaskUtils.newRecurseCrawlTask(parent, url, method, config);
    }


    // ------------ ���� --------------------


}
