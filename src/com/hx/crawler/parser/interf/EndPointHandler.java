/**
 * file name : EndPointHandler.java
 * created at : 2:02:38 PM Feb 2, 2016
 * created by 970655147
 */

package com.hx.crawler.parser.interf;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.dom4j.Element;

// �����������Endpoint��ʱ��, �������߼�
public abstract class EndPointHandler {

    /**
     * ���ݸ�������Դ, ������ǰ��������, �������յĽ����
     *
     * @param root       ������doc�ĸ��ڵ�
     * @param currentEle doc�е�ǰ���
     * @param url        �����ĵ���url
     * @param res        �������յĽ����
     * @param idx        ����ǽ���ValueNode����Ԫ�صĻ�, ���뵱ǰ����
     * @param child      ��ǰ���
     * @param curObj     ��ǰ����Ӧ�Ľ����
     * @return void
     * @throws
     * @author 970655147 created at 2017-03-11 11:54
     */
    public abstract void handle(Element root, Element currentEle, String url, JSONArray res, int idx,
                                EndPoint child, JSONObject curObj);

}
