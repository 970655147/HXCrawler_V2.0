/**
 * file name : IndexString.java
 * created at : 13:32:20 PM Apr 09, 2016
 * created by 970655147
 */

package com.hx.crawler.parser.interf;

/**
 * �����ַ����ӿ�
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 5/11/2017 8:21 PM
 */
public abstract class IndexString {

    /**
     * ����root ���, ������
     *
     * @param idxStr �����������ַ���
     *               * @param idxStr
     */
    public IndexString(String idxStr) {

    }

    /**
     * ��ȡ�����ַ�������֮��ĸ��ڵ�
     *
     * @return com.hx.crawler.parser.interf.Endpoint
     * @author 970655147 created at 2017-03-11 12:02
     */
    public abstract Endpoint getRoot();

}
