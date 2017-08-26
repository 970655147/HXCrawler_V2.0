package com.hx.crawler.parser.interf;

import com.hx.crawler.util.CrawlerConstants;

/**
 * EndpointType
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 8/26/2017 8:17 PM
 */
public enum EndpointType {
    /**
     * attribute
     */
    ATTRIBUTE(CrawlerConstants.ATTRIBUTE),
    /**
     * values
     */
    VALUES(CrawlerConstants.VALUES);

    // ----------------- ���߷��� -----------------------

    /**
     * ���ݸ����� type ��ȡ��Ӧ�� EndpointType
     *
     * @param type type
     * @return com.hx.crawler.parser.interf.EndpointType
     * @author Jerry.X.He
     * @date 8/26/2017 8:21 PM
     * @since 1.0
     */
    public static EndpointType byType(String type) {
        for (EndpointType ele : values()) {
            if (ele.type.equals(type)) {
                return ele;
            }
        }

        return null;
    }

    // ----------------- ʵ������ -----------------------

    /**
     * ����
     */
    private String type;

    EndpointType(String type) {
        this.type = type;
    }

    public String type() {
        return type;
    }

}
