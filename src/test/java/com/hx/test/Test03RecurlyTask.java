package com.hx.test;

import static com.hx.log.util.Log.err;
import static com.hx.log.util.Log.info;

import org.junit.Test;

import com.hx.crawler.crawler.HtmlCrawlerConfig;
import com.hx.crawler.crawler.interf.CrawlerConfig;
import com.hx.crawler.crawler.interf.HttpMethod;
import com.hx.crawler.crawler.interf.Page;
import com.hx.crawler.util.CrawlerUtils;
import com.hx.crawler.util.recursely_task.interf.RecurseCrawlCallback;
import com.hx.crawler.util.recursely_task.interf.RecurseCrawlTask;
import com.hx.crawler.util.recursely_task.interf.RecurseCrawlTaskFacade;
import com.hx.crawler.util.recursely_task.interf.RecurseTaskList;
import com.hx.log.util.Tools;

import com.hx.json.JSONArray;
import com.hx.json.JSONObject;

/**
 * file name : Test03RecurlyTask.java
 * created at : 19:53  2017-03-10
 * created by 970655147
 */
public class Test03RecurlyTask {

    @Test
    public void testRecurlyTask() {

        String seedUrl = "https://www.readnovel.com/";
        HttpMethod seedMethod = HttpMethod.GET;
        CrawlerConfig config = HtmlCrawlerConfig.get();

        // 如果是有需要去重的需求, 还是留到业务层来做吧., 否则 同步的开销会增大
        CrawlerUtils.recurselyTask(seedUrl, seedMethod, config, callback, true);

    }

    // Nested exception: org.xml.sax.SAXParseException; lineNumber: 4129; columnNumber: 84; 字符引用 "&#
    @Test
    public void testForException () throws Exception {

        // unicode 中的 high-surrogates, low-surrogates 范围内的sax解析会报上面的错误
        CrawlerUtils.xpathParser.parse(Tools.getContent(Tools.getTmpPath("recurlyTask", Tools.HTML)), "", "");

    }

    @Test
    public void testElementNull() {

        info(new JSONObject().element("name", (String) null).element("sdf", "sdff"));

    }

    @Test
    public void getRealXPath() {

        String xpath = "{\"xpath\":\"//div[@class='rank-list']/div[@class='book-rank-list']//li/a\",\"attribute\":\"href\"}";
        String str = CrawlerUtils.getRealXPathByXPathObj(xpath);
        info(str);

    }

    /**
     * 爬取readNovel.com的callback
     */
    RecurseCrawlCallback callback = new RecurseCrawlCallback<RecurseCrawlTask>() {
        @Override
        public void run(RecurseCrawlTaskFacade task, RecurseTaskList<RecurseCrawlTask> todo) throws Exception {
            int depth = task.getDepth();
            try {
                if (depth == 0) {
                    doForHomePage(task, todo);
                } else if (depth == 1) {
                    doForDetail(task, todo);
                }
            } catch (Exception e) {
                e.printStackTrace();
                // ignore
                err(Tools.errorMsg(e) );
            }
        }

        /**
         * bizes
         */
        // 抓取首页数据
        private void doForHomePage(RecurseCrawlTaskFacade task, RecurseTaskList<RecurseCrawlTask> todo) throws Exception {
            String url = task.getUrl();
            Page page = task.getPage();

            String xpath = "{\"xpath\":\"//div[@class='rank-list']/div[@class='book-rank-list']//li/a\",\"attribute\":\"href\"}";
//            CrawlerUtils.getPreparedDoc(url, page.getContent(), Tools.getTmpPath("recurlyTask", Tools.HTML));
            JSONArray fetchedData = CrawlerUtils.getResultByXPath(page.getContent(), url, CrawlerUtils.getRealXPathByXPathObj(xpath));
            if(! fetchedData.isEmpty()) {
                JSONArray links = fetchedData.getJSONArray(0);
                for(Object _link : links ) {
                    String link = (String) _link;
                    link = reCoupleUrl(link);
                    todo.add(task, link, HttpMethod.GET, HtmlCrawlerConfig.get());
                }
            }
            info("crawl for homePage end !");
        }
        // 重组url
        private String reCoupleUrl(String link) {
            String sep = ".com//";
            return "https://" + link.substring(link.indexOf(sep) + sep.length());
        }
        // 抓取小说详情数据
        private void doForDetail(RecurseCrawlTaskFacade task, RecurseTaskList<RecurseCrawlTask> todo) throws Exception {
            final String url = task.getUrl();
            final Page page = task.getPage();
            final String nameXpath = "{\"name\":\"bookName\",\"xpath\":\"//div[@class='book-info']/h1/em\",\"attribute\":\"text\"}";
            final String briefXpath = "{\"name\":\"brief\",\"xpath\":\"//div[@class='book-info']/p[@class='intro']\",\"attribute\":\"text\"}";

            Tools.execute(new Runnable() {
                @Override
                public void run() {
//                  CrawlerUtils.getPreparedDoc(url, page.getContent(), Tools.getTmpPath("recurlyTask", Tools.HTML));
                    long start = Tools.now();
                    try {
                        JSONArray fetchedData = CrawlerUtils.getResultByXPath(page.getContent(), url, CrawlerUtils.getRealXPathByXPathObj(nameXpath, briefXpath));
//                        JSONArray fetchedData = CrawlerUtils.getResultByXPath(
//                                Tools.getContent(page.getInputStream(), page.getCharset()), url,
//                                CrawlerUtils.getRealXPathByXPathObj(nameXpath, briefXpath));
                        info(String.valueOf(fetchedData));
                    } catch (Exception e) {
                        e.printStackTrace();
                        err(Tools.errorMsg(e));
                    }
                    long spent = Tools.spent(start);
                }
            });
        }
    };

}
