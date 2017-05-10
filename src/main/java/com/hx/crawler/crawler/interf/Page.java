/**
 * file name : Page.java
 * created at : 11:32:42 PM Apr 09, 2016
 * created by 970655147
 */

package com.hx.crawler.crawler.interf;

import java.io.InputStream;
import java.util.Map;

/**
 * ������Ӧ��Page
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 5/10/2017 7:19 PM
 */
public interface Page<ResponseType> {


    /**
     * ���ø�������Ӧ���ַ���
     *
     * @return void
     * @author Jerry.X.He
     * @date 5/10/2017 7:17 PM
     * @since 1.0
     */
    void setCharset(String charset);

    /**
     * ��ȡ��������Ӧ
     *
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 5/10/2017 7:17 PM
     * @since 1.0
     */
    ResponseType getResponse();

    /**
     * ��ȡ��������Ӧ���ַ���
     *
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 5/10/2017 7:17 PM
     * @since 1.0
     */
    String getCharset();

    /**
     * ��ȡ������cookie
     *
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 5/10/2017 7:17 PM
     * @since 1.0
     */
    Map<String, String> getCookies();

    /**
     * ��ȡ���е���Ӧͷ
     *
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 5/10/2017 7:17 PM
     * @since 1.0
     */
    Map<String, String> getHeaders();

    /**
     * ��ȡ������cookie
     *
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 5/10/2017 7:17 PM
     * @since 1.0
     */
    String getCookie(String key);

    /**
     * ��ȡ��������Ӧͷ
     *
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 5/10/2017 7:17 PM
     * @since 1.0
     */
    String getHeader(String key);

    /**
     * ��ȡ��Ӧ���������
     *
     * @return the String of response
     * @author Jerry.X.He
     * @date 5/10/2017 7:17 PM
     * @since 1.0
     */
    String getContent();

    /**
     * ��ȡ��Ӧ�����������
     *
     * @return the String of response
     * @author Jerry.X.He
     * @date 5/10/2017 7:17 PM
     * @since 1.0
     */
    InputStream getInputStream();

}
