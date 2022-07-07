package com.tny.game.namespace;

/**
 * 命名空间节点
 * <p>
 *
 * @author kgtny
 * @date 2022/6/29 04:16
 **/
public class NameNode<T> {

    private final String name;

    private final long id;

    private final T value;

    private final long version;

    public NameNode(String name, long id, T value, long version) {
        this.name = name;
        this.id = id;
        this.value = value;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public T getValue() {
        return value;
    }

    public long getVersion() {
        return version;
    }

}
