package com.hx.crawler.util.recursely_task.interf;

/**
 * file name : RecurseTaskList.java
 * created at : 14:45  2017-03-11
 * created by 970655147
 */
public interface RecurseTaskList<Task> {

    /**
     * ��������������һ��task
     * ���task��ʱ��, ��ע������task��createTime
     *
     * @param task ����������
     * @return
     * @throws
     * @author 970655147 created at 2017-03-10 17:23
     */
    void add(Task task);

    /**
     * �����������ȡ��һ��
     *
     * @return
     * @throws
     * @author 970655147 created at 2017-03-10 17:23
     */
    Task take();

    /**
     * ��ǰ���������������
     *
     * @return
     * @throws
     * @author 970655147 created at 2017-03-10 17:58
     */
    int size();

    /**
     * ��ǰ�����Ƿ�Ϊ��
     *
     * @return
     * @throws
     * @author 970655147 created at 2017-03-10 17:59
     */
    boolean isEmpty();

}
