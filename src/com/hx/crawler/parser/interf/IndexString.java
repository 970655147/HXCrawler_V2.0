/**
 * file name : IndexString.java
 * created at : 13:32:20 PM Apr 09, 2016
 * created by 970655147
 */

package com.hx.crawler.parser.interf;

// �����ַ����ӿ�
public abstract class IndexString {

	// ����root ���, ������
	public IndexString(String idxStr) {
		
	}

	/**
	 * ��ȡ�����ַ�������֮��ĸ��ڵ�
	 *
	 * @return com.hx.crawler.parser.interf.EndPoint
	 * @throws
	 * @author 970655147 created at 2017-03-11 12:02
	 */
	public abstract EndPoint getRoot();
	
}
