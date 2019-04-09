package com.server.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.TreeSet;

/**
 * 创建集合的公共方法
 *
 * @author CYX
 * @create 2019-04-09-17:52
 */
public class CollectionUtil {

    /**
     * 创建HashMap
     *
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> HashMap<K, V> newHashMapInstance() {
        return new HashMap<K, V>(16);
    }

    /**
     * 创建ArrayList
     *
     * @param <V>
     * @return
     */
    public static <V> ArrayList<V> newArrayListInstance() {
        return new ArrayList<V>();
    }

    /**
     * 创建LinedList
     *
     * @param <V>
     * @return
     */
    public static <V> LinkedList<V> newLinkedList() {
        return new LinkedList<V>();
    }

    /**
     * 创建HashSet
     *
     * @param <V>
     * @return
     */
    public static <V> HashSet<V> newHashSetInstance() {
        return new HashSet<V>();
    }

    /**
     * 创建LinkedHashMap
     *
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> LinkedHashMap<K, V> newLinkedHashMapInstance() {
        return new LinkedHashMap<K, V>();
    }

    /**
     * 创建TreeSet
     *
     * @param <V>
     * @return
     */
    public static <V> TreeSet<V> newTreeSetInstance() {
        return new TreeSet<V>();
    }
}
