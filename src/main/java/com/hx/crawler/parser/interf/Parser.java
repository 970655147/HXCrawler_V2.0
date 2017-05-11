/**
 * file name : Parser.java
 * created at : 13:37:01 PM Apr 09, 2016
 * created by 970655147
 */

package com.hx.crawler.parser.interf;


import com.hx.json.JSONArray;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.StringReader;

/**
 * ͨ��indexString �͸�����document �������
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 5/11/2017 8:22 PM
 */
public abstract class Parser {

    /**
     * ���ݵ�ǰ�ĵ�, �Լ�xpath�����ַ���, ȡ����Ҫ�õ��Ľ��
     *
     * @param root   �ĵ��ĸ��ڵ�
     * @param url    �ĵ���Ӧ��url
     * @param idxStr xpath�����ַ���
     * @return com.hx.json.JSONArray
     * @author 970655147 created at 2017-03-11 11:59
     */
    public abstract JSONArray parse(Element root, String url, String idxStr);

    /**
     * ���ݵ�ǰ�ĵ�, �Լ�xpath�����ַ���, ȡ����Ҫ�õ��Ľ��
     *
     * @param html   ��ǰ�ĵ�������
     * @param url    �ĵ���Ӧ��url
     * @param idxStr xpath�����ַ���
     * @return com.hx.json.JSONArray
     * @author 970655147 created at 2017-03-11 11:59
     */
    public JSONArray parse(String html, String url, String idxStr) throws Exception {
        SAXReader saxReader = new SAXReader();
        Element root = saxReader.read(new StringReader(html)).getRootElement();
        return parse(root, url, idxStr);
    }

}
