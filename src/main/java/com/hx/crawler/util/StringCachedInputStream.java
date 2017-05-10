package com.hx.crawler.util;

import com.hx.log.util.Tools;
import org.apache.http.util.ByteArrayBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * ��InputStream�ж�ȡ���ݵ�ͬʱ, �����ݻ�����sb��
 * һ�� InputStreamWrapper
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 5/10/2017 9:25 PM
 */
public class StringCachedInputStream extends InputStream {

    /**
     * ������������
     */
    private InputStream inputStream;
    /**
     * �����Ѿ���ȡ�˵�����
     */
    private ByteArrayBuffer bytes;

    /**
     * ��ʼ��
     *
     * @param inputStream ������������
     * @param bytes       ��Ҫ�������ݵ�StringBuilder
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
