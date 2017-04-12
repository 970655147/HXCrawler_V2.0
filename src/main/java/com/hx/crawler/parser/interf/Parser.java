/**
 * file name : Parser.java
 * created at : 13:37:01 PM Apr 09, 2016
 * created by 970655147
 */

package com.hx.crawler.parser.interf;


import java.io.StringReader;
import net.sf.json.JSONArray;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

// ͨ��indexString �͸�����document �������
public abstract class Parser {

    /**
     * ���ݵ�ǰ�ĵ�, �Լ�xpath�����ַ���, ȡ����Ҫ�õ��Ľ��
     *
     * @param root   �ĵ��ĸ��ڵ�
     * @param url    �ĵ���Ӧ��url
     * @param idxStr xpath�����ַ���
     * @return net.sf.json.JSONArray
     * @throws
     * @author 970655147 created at 2017-03-11 11:59
     */
    public abstract JSONArray parse(Element root, String url, String idxStr);

    /**
     * ���ݵ�ǰ�ĵ�, �Լ�xpath�����ַ���, ȡ����Ҫ�õ��Ľ��
     *
     * @param html   ��ǰ�ĵ�������
     * @param url    �ĵ���Ӧ��url
     * @param idxStr xpath�����ַ���
     * @return net.sf.json.JSONArray
     * @throws
     * @author 970655147 created at 2017-03-11 11:59
     */
    public JSONArray parse(String html, String url, String idxStr) throws Exception {
        SAXReader saxReader = new SAXReader();
        Element root = saxReader.read(new StringReader(html)).getRootElement();
        return parse(root, url, idxStr);
    }

}
