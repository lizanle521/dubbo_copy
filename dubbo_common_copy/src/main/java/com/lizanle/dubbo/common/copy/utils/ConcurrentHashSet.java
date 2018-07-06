package com.lizanle.dubbo.common.copy.utils;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashSet<E> extends AbstractSet<E> implements Set<E>, Serializable {
    private static final long serialVersionUID = -4517568175795227469L;

    private final static Object PRESENT = new Object();

    private final ConcurrentHashMap<E,Object> map ;

    public ConcurrentHashSet() {
        map = new ConcurrentHashMap<>();
    }

    public ConcurrentHashSet(int initCapacity) {
        map = new ConcurrentHashMap<>(initCapacity);
    }

    @Override
    public Iterator<E> iterator() {
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        return map.size();
    }

    public boolean isEmpty(){
        return map.isEmpty();
    }

    public boolean contains(Object obj){
        return map.containsKey(obj);
    }

    public boolean add(E obj){
        return map.put(obj,PRESENT) == null;
    }

    public boolean remove(Object obj){
        return map.remove(obj) == PRESENT;
    }

    public void clear(){
        map.clear();
    }
}
