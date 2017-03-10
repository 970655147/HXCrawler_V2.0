package com.hx.crawler.test;

import com.hx.crawler.crawler.HtmlCrawler;
import com.hx.crawler.crawler.HtmlCrawlerConfig;
import com.hx.crawler.crawler.interf.CrawlerConfig;
import com.hx.crawler.crawler.interf.HttpMethod;
import com.hx.crawler.crawler.interf.Page;
import com.hx.crawler.util.CrawlerUtils;
import com.hx.log.util.Log;
import com.hx.log.util.Tools;
import com.sun.xml.internal.ws.api.message.ExceptionHasMessage;
import net.sf.json.JSONArray;
import org.junit.Test;

import static com.hx.log.util.Log.err;
import static com.hx.log.util.Log.info;

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

    /**
     * 爬取readNovel.com的callback
     */
    CrawlerUtils.RecurseCrawlCallback callback = new CrawlerUtils.RecurseCrawlCallback<CrawlerUtils.RecurseCrawlTask>() {
        @Override
        public void run(CrawlerUtils.RecurseCrawlTaskFacade task, CrawlerUtils.RecurseTaskList<CrawlerUtils.RecurseCrawlTask> todo) {
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
        private void doForHomePage(CrawlerUtils.RecurseCrawlTaskFacade task, CrawlerUtils.RecurseTaskList<CrawlerUtils.RecurseCrawlTask> todo) throws Exception {
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
                    CrawlerUtils.RecurseCrawlTask todoTask = CrawlerUtils.newRecurseCrawlTask(task, link, HttpMethod.GET, HtmlCrawlerConfig.get());
                    todo.add(todoTask);
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
        private void doForDetail(CrawlerUtils.RecurseCrawlTaskFacade task, CrawlerUtils.RecurseTaskList<CrawlerUtils.RecurseCrawlTask> todo) throws Exception {
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
                        info(String.valueOf(fetchedData));
                    } catch (Exception e) {
                        err(Tools.errorMsg(e));
                    }
                    long spent = Tools.spent(start);
                }
            });
        }
    };

}
