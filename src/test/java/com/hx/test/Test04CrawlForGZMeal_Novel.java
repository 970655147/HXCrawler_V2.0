/**
 * file name : Test03CrawlForGZMeal_Novel.java
 * created at : 22:04:44 2016-09-29
 * created by 970655147
 */

package com.hx.test;

import com.hx.crawler.util.CrawlerUtils;
import com.hx.crawler.util.pipeline_task.PipelineTaskUtils;
import com.hx.log.util.Tools;

import com.hx.json.JSONArray;

public class Test04CrawlForGZMeal_Novel {

	static String categoryConfigPath = System.getProperty("user.dir") + "/com/hx/test/com/hx/crawler/com.hx.test/config/Test04ForCrawlerGZMeal01Category.json";

	// ��ȡgzmeal.com��С˵
	public static void main(String[] args) throws Exception {
		Tools.createAnBuffer(PipelineTaskUtils.DO_PIPELINE_TASK_NAME, PipelineTaskUtils.SAVE_FETCHED_RESULT_PATH);

//		log(categoryConfigPath);
		JSONArray categories = CrawlerUtils.doPipelineTask(categoryConfigPath);

		Tools.awaitTasksEnd();
		Tools.awaitShutdown();
		Tools.closeAnBuffer(PipelineTaskUtils.DO_PIPELINE_TASK_NAME);
	}

}
