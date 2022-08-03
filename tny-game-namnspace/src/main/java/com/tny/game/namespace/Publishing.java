package com.tny.game.namespace;

import com.tny.game.codec.*;

import java.util.concurrent.CompletableFuture;

/**
 * 发布流程
 * <p>
 *
 * @author kgtny
 * @date 2022/7/9 04:52
 **/
public interface Publishing<T> {

    CompletableFuture<NameNode<T>> doPublish(NamespaceExplorer explorer, String path, T value, ObjectMineType<T> mineType, Lessee lessee);

}
