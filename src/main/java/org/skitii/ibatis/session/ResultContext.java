package org.skitii.ibatis.session;

public interface ResultContext {
    /**
     * 获取结果
     */
    Object getResultObject();

    /**
     * 获取记录数
     */
    int getResultCount();
}
