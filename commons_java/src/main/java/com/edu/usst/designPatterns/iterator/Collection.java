package com.edu.usst.designPatterns.iterator;


/**
 * Created by pengcheng.wan on 2016/8/16.
 */
public interface Collection {
    public Iterator iterator();

    //取得集合元素
    public Object get(int i);

    //取得集合大小
    public int size();
}
