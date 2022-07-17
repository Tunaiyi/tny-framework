package com.tny.game.namespace;

import java.util.concurrent.CompletableFuture;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/7/9 04:52
 **/
public interface Publishing<T> {

    CompletableFuture<NameNode<T>> publish(NamespaceExplorer explorer, String path, T value, Lessee lessee);

}
