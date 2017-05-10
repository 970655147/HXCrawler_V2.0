/**
 * file name : Page.java
 * created at : 11:32:42 PM Apr 09, 2016
 * created by 970655147
 */

package com.hx.crawler.crawler.interf;

import java.io.InputStream;
import java.util.Map;

/**
 * 接收响应的Page
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 5/10/2017 7:19 PM
 */
public interface Page<ResponseType> {


    /**
     * 配置给定的响应的字符集
     *
     * @return void
     * @author Jerry.X.He
     * @date 5/10/2017 7:17 PM
     * @since 1.0
     */
    void setCharset(String charset);

    /**
     * 获取给定的响应
     *
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 5/10/2017 7:17 PM
     * @since 1.0
     */
    ResponseType getResponse();

    /**
     * 获取给定的响应的字符集
     *
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 5/10/2017 7:17 PM
     * @since 1.0
     */
    String getCharset();

    /**
     * 获取给定的cookie
     *
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 5/10/2017 7:17 PM
     * @since 1.0
     */
    Map<String, String> getCookies();

    /**
     * 获取所有的响应头
     *
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 5/10/2017 7:17 PM
     * @since 1.0
     */
    Map<String, String> getHeaders();

    /**
     * 获取给定的cookie
     *
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 5/10/2017 7:17 PM
     * @since 1.0
     */
    String getCookie(String key);

    /**
     * 获取给定的响应头
     *
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 5/10/2017 7:17 PM
     * @since 1.0
     */
    String getHeader(String key);

    /**
     * 获取响应结果的内容
     *
     * @return the String of response
     * @author Jerry.X.He
     * @date 5/10/2017 7:17 PM
     * @since 1.0
     */
    String getContent();

    /**
     * 获取响应结果的输入流
     *
     * @return the String of response
     * @author Jerry.X.He
     * @date 5/10/2017 7:17 PM
     * @since 1.0
     */
    InputStream getInputStream();

}
