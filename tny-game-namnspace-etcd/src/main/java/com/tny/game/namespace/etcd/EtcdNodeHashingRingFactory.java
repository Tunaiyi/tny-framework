package com.tny.game.namespace.etcd;

import com.tny.game.codec.*;
import com.tny.game.namespace.*;
import com.tny.game.namespace.consistenthash.*;

/**
 * 一致性节点Hash环 工厂
 * <p>
 *
 * @author kgtny
 * @date 2022/7/21 10:30
 **/
public class EtcdNodeHashingRingFactory implements NodeHashingFactory {

    private static final EtcdNodeHashingRingFactory FACTORY = new EtcdNodeHashingRingFactory();

    public static NodeHashingFactory getDefault() {
        return FACTORY;
    }

    @Override
    public <N extends ShardingNode> NodeHashing<N> create(String rootPath, HashingOptions<N> option, NamespaceExplorer explorer,
            ObjectCodecAdapter adapter) {
        return new EtcdNodeHashingRing<>(rootPath, option, explorer, adapter);
    }

}
