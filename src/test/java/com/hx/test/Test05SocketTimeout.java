package com.hx.test;

import com.hx.crawler.crawler.HtmlCrawler;
import com.hx.crawler.crawler.HtmlCrawlerConfig;
import com.hx.crawler.crawler.interf.Page;
import com.hx.log.util.Tools;

import static com.hx.log.util.Log.info;

/**
 * Test05SocketTimeout
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 2019/3/30 9:31
 */
public class Test05SocketTimeout {

    // Test05SocketTimeout
    public static void main(String[] args) throws Exception {

        HtmlCrawlerConfig config = HtmlCrawlerConfig.get();
        HtmlCrawler crawler = HtmlCrawler.getInstance();

        String url = "https://acc.tz12306.com/report/ins/sellStatReport";
        config.addCookie("JSESSIONID", "B0DDEBBA6CB246CF36DAC15CAC994750");
//        config.setSocketTimeout(3_000);
        config.setConnectionTimeout(20_000);

        long start = System.currentTimeMillis();
        Page page = crawler.getPage(url, config);
        long spent = Tools.spent(start);
        info(page.getContent());
        info(" spent " + spent + " ms ... ");

    }

}
