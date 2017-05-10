package com.hx.crawler.util;

import com.hx.log.util.Tools;
import org.apache.http.util.ByteArrayBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * 从InputStream中读取数据的同时, 将数据缓冲在sb中
 * 一种 InputStreamWrapper
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 5/10/2017 9:25 PM
 */
public class StringCachedInputStream extends InputStream {

    /**
     * 给定的输入流
     */
    private InputStream inputStream;
    /**
     * 缓存已经读取了的数据
     */
    private ByteArrayBuffer bytes;

    /**
     * 初始化
     *
     * @param inputStream 给定的输入流
     * @param bytes       需要缓存数据的StringBuilder
     * @since 1.0
     */
    public StringCachedInputStream(InputStream inputStream, ByteArrayBuffer bytes) {
        Tools.assert0(inputStream != null, "'inputStream' can'e be null !");
        Tools.assert0(bytes != null, "'bytes' can'e be null !");

        ByteBuffer buffer = ByteBuffer.allocate(100);
        this.inputStream = inputStream;
        this.bytes = bytes;

    }

    @Override
    public int read() throws IOException {
        int result = inputStream.read();
        bytes.append(result);
        return result;
    }

}
