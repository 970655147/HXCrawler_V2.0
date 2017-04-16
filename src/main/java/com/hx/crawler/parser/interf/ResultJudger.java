/**
 * file name : ResultJudger.java
 * created at : 5:11:13 PM Oct 25, 2015
 * created by 970655147
 */

package com.hx.crawler.parser.interf;

import com.hx.json.JSONArray;

// 判断给定的结果是否合法
public interface ResultJudger {

    /**
     * 判断给定的结果是否为空
     *
     * @param idx         当前xpath的索引
     * @param fetchedData 当前xpath取到的结果
     * @return
     * @throws
     * @author 970655147 created at 2017-03-11 11:58
     */
    boolean isResultNull(int idx, JSONArray fetchedData);

}
