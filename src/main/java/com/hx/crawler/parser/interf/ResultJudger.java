/**
 * file name : ResultJudger.java
 * created at : 5:11:13 PM Oct 25, 2015
 * created by 970655147
 */

package com.hx.crawler.parser.interf;

import com.hx.json.JSONArray;

// �жϸ����Ľ���Ƿ�Ϸ�
public interface ResultJudger {

    /**
     * �жϸ����Ľ���Ƿ�Ϊ��
     *
     * @param idx         ��ǰxpath������
     * @param fetchedData ��ǰxpathȡ���Ľ��
     * @return
     * @throws
     * @author 970655147 created at 2017-03-11 11:58
     */
    boolean isResultNull(int idx, JSONArray fetchedData);

}
