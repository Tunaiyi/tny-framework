package com.tny.game.namespace.etcd;

import com.tny.game.codec.*;
import com.tny.game.namespace.*;
import com.tny.game.namespace.consistenthash.*;

/**
 * 多节点 HashMap 工厂
 * <p>
 *
 * @author kgtny
 * @date 2022/7/21 10:30
 **/
public class EtcdNodeHashingMultimapFactory implements NodeHashingFactory {

    private static final EtcdNodeHashingMultimapFactory FACTORY = new EtcdNodeHashingMultimapFactory();

    public static NodeHashingFactory getDefault() {
        return FACTORY;
    }

    @Override
    public <N extends ShardingNode> NodeHashing<N> create(String rootPath, HashingOptions<N> option, NamespaceExplorer explorer,
            ObjectCodecAdapter adapter) {
        return new EtcdNodeHashingMultimap<>(rootPath, option, explorer, adapter);
    }

}
