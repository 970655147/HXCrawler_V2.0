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

    public Parser() {

    }

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

    /**
     * ����target Endpoint
     * �������ep���, һ�ν���ÿһ����������, ��������res��
     *
     * @param root       root�ڵ�
     * @param currentEle ��ǰ���ڷ��ʵ�Ԫ�� [��Ӧһ��ep]
     * @param url        ������ȡ��url
     * @param ep         ���ڷ��ʵ�endiPoint
     * @param res        �ռ������JSONArray
     * @param idx        ��ǰ���ڴ����Ԫ�ص�����
     * @return void
     * @author Jerry.X.He
     * @date 5/11/2017 8:45 PM
     * @since 1.0
     */
    public abstract void parse(Element root, Element currentEle, String url, Endpoint ep, JSONArray res, int idx);

}
