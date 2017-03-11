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

// 工具类
public final class CrawlerUtils {

    // disable constructor
    private CrawlerUtils() {
        Tools.assert0("can't instantiate !");
    }

    // parse 相关常量
    public static String PARSE_METHOD_NAME = "parse";
    public static boolean IS_PARSE_METHOD_STATIC = true;
    public static Class[] PARSE_METHOD_PARAMTYPES = new Class[]{ScriptParameter.class};

    // ----------------- crawler 处理相关 -----------------------
    // 获取处理过之后的文档
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

    // 通过xpath 获取结果
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

    // 所有的unicode surrogates代码点的字符串表示
    private static final Set<String> UNICODE_SURROGATES = Tools.asSortedSet();
    // 第一次使用正则踢掉的部分
    private static String NEED_BE_FILTER_REG = "<!DOCTYPE html>" + "|" + "xmlns=\"http://www.w3.org/1999/xhtml\"";

    static {
        for (int i = 55296; i <= 57343; i++) {
            UNICODE_SURROGATES.add(String.valueOf(i));
        }
    }

    // 格式化html [去掉, 添加 不规范的标签]
    // 思路 : 使用tagSoup规范化xml
    // 注意 : 需要重定向ContentHandler的输出
    // 需要去掉xml声明, 需要去掉html标签的xmlns的属性
    public static String normalize(String html) throws Exception {
        Tools.assert0(html != null, "'html' can't be null ");

        StringReader sr = new StringReader(html);
        Parser parser = new Parser();        //实例化Parse

//		// 输出格式化之后的xml
        XMLWriter writer = new XMLWriter();    //实例化XMLWriter，即SAX内容处理器
        parser.setContentHandler(writer);    //设置内容处理器
        StringWriter strWriter = new StringWriter();
        writer.setOutput(strWriter);
        parser.parse(new InputSource(sr));    //解析

        // 获取结构良好的html, 并去掉xml声明, xml声明是放在第一行的, 因此 如果是xml的话, 去掉xml声明
        String res = strWriter.toString();
        int firstLineEnd = res.indexOf("\n");
        if (res.substring(0, firstLineEnd).contains("<?xml")) {
            res = res.substring(firstLineEnd + 1);
        }

        // 就是因为多了这个xmlns的声明, 导致了我的xPath读取数据有问题。。。, 或者说 只能通过/*[@XX='XXX'] 来读取数据
        // 并且读取到的数据 还有一个xmlns的声明...						--2015.07.31
//		String html = "<html xmlns=\"http://www.w3.org/1999/xhtml\"><body><strong><span id=\"ctl01_ContentPlaceHolder1_lblPriceTitle\">ProductPricing</span>:</strong></body></html>";
        // 所以 这里对res进行统一的处理, 去掉html标签的xmlns的声明
        // 增加对于unicode high-UNICODE_SURROGATES, low-UNICODE_SURROGATES 范围内的code的处理..        -- 2017.03.10
        res = res.replaceAll(NEED_BE_FILTER_REG, Tools.EMPTY_STR);
        return trimUnicodeSurrogates(res);
    }

    /**
     * 去掉所有的unicode surrogate代码点范围内的字符, 否则 sax解析会报错误
     *
     * @param res trim surrogate代码点之前的的结果
     * @return java.lang.String
     * @throws
     * @author 970655147 created at 2017-03-10 22:21
     */
    private static String trimUnicodeSurrogates(String res) {
        // 不要直接以所有的 surrogates代码点 &#$code作为分隔符, 不然 嘿嘿, 等着吧.
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
     * 创建一个ScriptParameter
     *
     * @param crawler scriptParameter对应的crawler
     * @param url     需要爬取的url
     * @param param   发送请求所需要的参数
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

    // 通过xpath获取HXCrawler的xpath
    // 可以改写为JSONArray.toString() 实现 [思路来自'duncen'[newEgg同事] ]
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

    // 利用反射调用指定的class的methodName方法
    public static void parse(String className, String url, Map<String, Object> params) throws Exception {
        parse(className, url, params, PARSE_METHOD_NAME, IS_PARSE_METHOD_STATIC, PARSE_METHOD_PARAMTYPES);
    }

    /**
     * 调用给定的className对应的method方法, 来来处理爬取数据相关的业务
     *
     * @param className        clazzName
     * @param url              爬取的url
     * @param params           发送请求所需要的参数
     * @param methodName       方法的名称
     * @param isStaticMethod   给定的方法是否是静态方法
     * @param methodParamTypes 给定的方法的参数类型列表
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

    // 为nextStageParams添加category
    public static void addNameUrlSite(JSONObject category, JSONObject nextStageParams) {
        Tools.assert0(!Tools.isEmpty(category), "'category' can't be null ");
        nextStageParams.put(Tools.NAME, category.getString(Tools.NAME));
        nextStageParams.put(Tools.URL, category.getString(Tools.URL));
        nextStageParams.put(Tools.SITE, nextStageParams.getString(Tools.SITE) + "." + category.getString(Tools.NAME));
    }

    // 获取taskName
    public static String getTaskName(ScriptParameter<?, ?, ?, ?, ?, ?> singleUrlTask) {
        Tools.assert0(singleUrlTask != null, "'singleUrlTask' can't be null ");
        return "crawl " + singleUrlTask.getParam().get(Tools.TASK) + " from " + singleUrlTask.getParam().get(Tools.SITE);
    }

    // ------------ 日志相关 --------------------
    // 相关的logPattern
    public static LogPatternChain taskBeforeLogPatternChain = LogPatternUtils.initLogPattern(Constants.optString(Constants._TASK_BEFORE_LOG_PATTERN));
    public static LogPatternChain taskAfterLogPatternChain = LogPatternUtils.initLogPattern(Constants.optString(Constants._TASK_AFTER_LOG_PATTERN));
    public static LogPatternChain taskExceptionLogPatternChain = LogPatternUtils.initLogPattern(Constants.optString(Constants._TASK_EXCEPTION_LOG_PATTERN));

    // 打印任务的日志信息
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

    // ------------ PipelineTask相关  add at 2016.08.13 --------------------
    // 获取给定的configPath对应的配置, 然后 执行流水线式的执行任务
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

    // ------------ recurselyTask相关  add at 2017.03.10 --------------------

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
        RecurselyTaskUtils.recurselyTask(seedUrl, method, config, callback, isAsync, isBfs);
    }

    public static RecurseCrawlTask newRecurseCrawlTask(RecurseCrawlTaskFacade parent, String url, HttpMethod method, CrawlerConfig config) {
        return RecurselyTaskUtils.newRecurseCrawlTask(parent, url, method, config);
    }


    // ------------ 待续 --------------------


}
