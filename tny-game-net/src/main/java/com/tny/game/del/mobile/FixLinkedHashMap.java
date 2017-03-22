package com.tny.game.del.mobile;

import java.util.LinkedHashMap;

public class FixLinkedHashMap<K, V> extends LinkedHashMap<K, V> {

    /**
     *
     */
    private static final long serialVersionUID = 7140688366154457969L;

    private int maxSize = 10;

    public FixLinkedHashMap(int maxSize) {
        super();
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
        return this.size() >= this.maxSize;
    }

    public static void main(String[] args) {
        FixLinkedHashMap<Integer, String> map = new FixLinkedHashMap<Integer, String>(5);
        for (int index = 0; index < 10; index++) {
            map.put(index, "第" + index + "个");
            System.out.println(map);
        }
    }

}
