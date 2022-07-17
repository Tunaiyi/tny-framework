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

    private final long revision;

    public NameNode(String name, long id, T value, long version, long revision) {
        this.name = name;
        this.id = id;
        this.value = value;
        this.version = version;
        this.revision = revision;
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

    public long getRevision() {
        return revision;
    }

}
