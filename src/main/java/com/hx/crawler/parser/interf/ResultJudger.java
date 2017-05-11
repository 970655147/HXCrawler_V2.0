/**
 * file name : ResultJudger.java
 * created at : 5:11:13 PM Oct 25, 2015
 * created by 970655147
 */

package com.hx.crawler.parser.interf;

/**
 * 判断给定的结果是否合法
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 5/11/2017 8:22 PM
 */
public interface ResultJudger {

    /**
     * 判断给定的结果是否为空
     *
     * @param context 抓取结果的Context
     * @return if the fetchedData is valid
     * @author 970655147 created at 2017-03-11 11:58
     */
    boolean isResultNull(ResultContext context);

}
