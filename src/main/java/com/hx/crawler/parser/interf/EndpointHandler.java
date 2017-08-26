/**
 * file name : EndPointHandler.java
 * created at : 2:02:38 PM Feb 2, 2016
 * created by 970655147
 */

package com.hx.crawler.parser.interf;

import com.hx.common.interf.common.Result;
import com.hx.json.JSONArray;
import com.hx.json.JSONObject;
import org.dom4j.Element;


/**
 * �����������Endpoint��ʱ��, �������߼�
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 5/11/2017 8:21 PM
 */
public abstract class EndpointHandler {

    /**
     * У������� Endpoint
     *
     * @param endpoint endpoint
     * @param current  current
     * @return
     * @author Jerry.X.He
     * @date 8/26/2017 6:53 PM
     * @since 1.0
     */
    public abstract Result validate(Endpoint endpoint, JSONObject current);

    /**
     * ���ݸ�����������Ϣ ����һ�� Endpoint
     *
     * @param parent  parent
     * @param current current
     * @return
     * @author Jerry.X.He
     * @date 8/26/2017 6:55 PM
     * @since 1.0
     */
    public abstract Endpoint createInstance(Endpoint parent, JSONObject current);

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
     * @author 970655147 created at 2017-03-11 11:54
     */
    public abstract void handle(Element root, Element currentEle, String url, JSONArray res, int idx,
                                Endpoint child, JSONObject curObj);

}
